package net.hytora.networking.connection;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.ComponentSender;


import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketOptions;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Setter @Getter
public class HytoraConnectionBridge implements ComponentSender {

    /**
     * The incoming objects
     */
    private final Object syncIn;

    /**
     * Outgoing objects
     */
    private final Object syncOut;

    /**
     * If this login was verified
     */
    private boolean enabled;

    /**
     * The output
     */
    private ObjectOutputStream objectOutputStream;

    /**
     * The input
     */
    private ObjectInputStream objectInputStream;

    /**
     * The server instance
     */
    public final HytoraServer server;

    /**
     * The socket instance
     */
    private final Socket socket;

    /**
     * The name of this bridge
     * mostly the username of login
     */
    private String name;


    /**
     * Creates a new bridge with a server and client instance
     *
     * @param server the server
     * @param socket the socket
     */
    public HytoraConnectionBridge(HytoraServer server, Socket socket) {
        this.server = server;
        this.socket = socket;

        this.syncOut = new Object();
        this.syncIn = new Object();
        this.enabled = false;


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
            ) {

                this.objectOutputStream = out;
                this.objectInputStream = in;

                if (this.waitLogin()) {

                    socket.setSoTimeout(0);

                    this.enabled = true;
                    this.server.getUserManager().registerUser(this);

                    //Handling the login handler
                    if (!this.server.getAcceptedNames().contains(this.name)) {
                        this.server.getAcceptedNames().add(this.name);
                        for (Consumer<HytoraConnectionBridge> connectionHandler : this.server.getCatcher().getConnectionHandlers()) {
                            connectionHandler.accept(this);
                        }
                    }

                    while (this.server.isOpened() && this.enabled) {

                        //Listening for incoming components
                        try {

                            Object incomingObject;
                            synchronized (this.syncIn) {
                                incomingObject = this.objectInputStream.readObject();
                            }

                            //Only components matter
                           if (incomingObject instanceof Component) {
                               Component component = (Component) incomingObject;

                                component.setSender(this.name); //Setting sender to this

                               //Checking for forwarding or receiver
                                if (component.getReceiver().equalsIgnoreCase("SERVER")) { // to the server

                                    if (component.isReply()) {
                                        this.server.getCatcher().handleReply(component);
                                    } else {
                                        this.server.getCatcher().handleComponent(component, this);
                                    }

                                } else {
                                    if (component.isReply()) {
                                        this.server.getCatcher().handleReply(component);
                                    } else {
                                        if ((!component.getReceiver().equalsIgnoreCase("ALL"))) {
                                            this.server.sendComponent(component);
                                        }
                                    }
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (e instanceof EOFException || e instanceof SocketException) {
                    return;
                }

                e.printStackTrace();
            } finally {
                if (this.enabled) {
                    this.enabled = false;
                    this.server.getUserManager().unregisterUser(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * Sends a {@link Component}
     * to the other connection instance
     *
     * @param component the component
     */
    public void sendComponent(Component component) {
        this.sendObject(component);
    }

    /**
     * Sends an object which is {@link Serializable}
     *
     * @param object the object
     */
    public void sendObject(Serializable object) {
        if (!this.socket.isConnected()) {
            return;
        }

        try {
            synchronized (this.syncOut) {
                this.objectOutputStream.writeObject(object);
                this.objectOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a {@link Component}
     * to the other connection instance
     *
     * @param consumer the consumer to handle the component
     */
    public void sendComponent(Consumer<Component> consumer) {
        Component component = new Component();
        consumer.accept(component);

        this.sendComponent(component);
    }

    /**
     * Sends a {@link Component} to the server
     *
     * In this method the reply is also included and will be accepted in the given consumer
     * If no reply is given or the reply timed out the boolean part of the consumer will be set to false
     *
     * @param hytoraComponent the component
     * @param delay the timeout delay
     * @param replyConsumer the callback for the reply
     */
    @SneakyThrows
    public void sendComponent(Component hytoraComponent, int delay, BiConsumer<Component, Boolean> replyConsumer) {
        if (!this.enabled) {
            return;
        }

        Field requestID = hytoraComponent.getClass().getDeclaredField("requestID");
        requestID.setAccessible(true);
        requestID.set(hytoraComponent, this.server.getRandom().nextLong());

        this.server.getCatcher().registerReplyHandler(hytoraComponent.getRequestID(), delay, replyConsumer);

        this.sendComponent(hytoraComponent);
    }


    /**
     * Sends a {@link Component} to the server
     *
     * In this method the reply is also included and will be accepted in the given consumer
     * If no reply is given or the reply timed out the boolean part of the consumer will be set to false
     *
     * @param componentConsumer the consumer for the component
     * @param delay the timeout delay
     * @param replyConsumer the callback for the reply
     */
    @SneakyThrows
    public void sendComponent(Consumer<Component> componentConsumer, int delay, BiConsumer<Component, Boolean> replyConsumer) {
        if (!this.enabled) {
            return;
        }

        Component component = new Component();
        componentConsumer.accept(component);


        Field requestID = component.getClass().getDeclaredField("requestID");
        requestID.setAccessible(true);
        requestID.set(component, this.server.getRandom().nextLong());
        this.server.getCatcher().registerReplyHandler(component.getRequestID(), delay, replyConsumer);

        this.sendComponent(component);
    }

    /**
     * Starts the connection and checks if the login
     * is successful
     *
     * @return boolean if success
     * @throws IOException if something goes wrong
     * @throws ClassNotFoundException if something goes wrong too
     */
    private boolean waitLogin() throws IOException, ClassNotFoundException {
        String response;

        boolean state;
        String password;

        synchronized (this.syncIn) {
            this.name = objectInputStream.readUTF();
            password = objectInputStream.readUTF();

        }

        BiFunction<String, String, Boolean> loginManager = this.server.getLoginManager();
        state = loginManager == null || loginManager.apply(this.name, password);
        response = (state) ? "OK" : "INVALID";

        this.sendObject(response);

        return state;
    }


    /**
     * Replies to a query
     *
     * @param content the content to reply
     */
    @SneakyThrows
    public void reply(Serializable content) {
        Component hytoraReply = new Component();
        hytoraReply.setMessage(content);
        Field reply = hytoraReply.getClass().getDeclaredField("reply");
        reply.setAccessible(true);
        reply.set(hytoraReply, true);
        this.sendComponent(hytoraReply);
    }

    /**
     * Replies to a query
     *
     * @param consumer the consumer to handle
     */
    @SneakyThrows
    public void reply(Consumer<Component> consumer) {
        Component hytoraReply = new Component();
        consumer.accept(hytoraReply);

        Field reply = hytoraReply.getClass().getDeclaredField("reply");
        reply.setAccessible(true);
        reply.set(hytoraReply, true);
        this.sendComponent(hytoraReply);

    }

    /**
     * Disconnects from the server
     */
    public void disconnect() {
        System.out.println("[Client] '" + this.getName() + "' has disconnected!");
        this.enabled = false;
        this.server.getUserManager().unregisterUser(this);
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
