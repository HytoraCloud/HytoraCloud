package net.hytora.networking.connection.client;

import lombok.SneakyThrows;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.other.HytoraLogin;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.PacketManager;
import net.hytora.networking.elements.component.Component;

import net.hytora.networking.elements.other.UserManager;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class HytoraClient implements HytoraConnection {

    /**
     * Enabled until close is called
     */
    private boolean available;

    /**
     * The login for this client
     */
    private HytoraLogin hytoraLogin;

    /**
     * The socket
     */
    private Socket socket;

    /**
     * The sender
     */
    private ObjectOutputStream objectOutputStream;

    /**
     * The receiver
     */
    private ObjectInputStream objectInputStream;

    /**
     * instance object for synchronise
     */
    private final Object objectIncoming = new Object();

    /**
     * must be different out != in
     */
    private final Object objectOutgoing = new Object();

    /**
     * New random
     */
    private final Random random;

    /**
     * The packetManager to handle packets
     */
    private final PacketManager packetManager;

    /**
     * The catcherManager
     */
    private final ClientCatcher catcher;

    /**
     * The options
     */
    private HytoraClientOptions options;

    /**
     * The host to connect to
     */
    private final String host;

    /**
     * The port to connect to
     */
    private final int port;

    /**
     * The consumer for the login
     */
    private Consumer<HytoraClient> loginConsumer;

    /**
     * The client listener
     */
    private ClientListener clientListener;

    /**
     * Creates a new client with given options and login
     *
     * @param host the host to connect to
     * @param port the port to connect to
     */
    public HytoraClient(String host, int port) {

        this.host = host;
        this.port = port;
        this.available = true;
        this.clientListener = null;

        this.hytoraLogin = new HytoraLogin("No-Name-Client");
        this.catcher = new ClientCatcher();
        this.options = new HytoraClientOptions();
        this.packetManager = new PacketManager(this);
        this.random = new Random();

        this.registerPacketHandler(packet -> {
            if (this.clientListener != null) {
                this.clientListener.packetIn(packet);
            }
        });

        this.registerChannelHandler("hytora_internal", repliableComponent -> {
            Component component = repliableComponent.getComponent();
            if (component.has("handshake")) {
                if (clientListener != null) {
                    clientListener.onHandshake();
                }
            }
        });

        this.loginHandler(hytoraClient -> {

            if (this.clientListener != null) {
                this.clientListener.onConnect(new InetSocketAddress(this.host, this.port));
            }
        });
    }

    /**
     * Sets the options for this client
     *
     * @param options the options
     * @return current client
     */
    public HytoraClient options(HytoraClientOptions options) {
        this.options = options;
        return this;
    }

    /**
     * Sets the listener for this client
     *
     * @param listener the listener
     * @return current client
     */
    public HytoraClient listener(ClientListener listener) {
        this.clientListener = listener;
        return this;
    }

    @Override
    public void sendPacket(HytoraPacket packet) {
        HytoraConnection.super.sendPacket(packet);

        if (this.clientListener != null) {
            this.clientListener.packetOut(packet);
        }
    }

    @Override
    public void sendPacket(HytoraPacket packet, String receiver) {
        HytoraConnection.super.sendPacket(packet, receiver);

        if (this.clientListener != null) {
            this.clientListener.packetOut(packet);
        }
    }

    /**
     * Sets the login for this client
     *
     * @param login the login
     * @return current client
     */
    public HytoraClient login(HytoraLogin login) {
        this.hytoraLogin = login;
        return this;
    }

    /**
     * Sets the login consumer for the login
     *
     * @param loginConsumer the consumer
     * @return current client
     */
    public HytoraClient loginHandler(Consumer<HytoraClient> loginConsumer) {
        this.loginConsumer = loginConsumer;
        return this;
    }

    /**
     * Connects this client to the server
     *
     * @return future
     */
    @Override
    public Future<HytoraConnection> createConnection() {

        CompletableFuture<HytoraConnection> future = new CompletableFuture<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            int loopedAmount = 0;

            try {
                while (this.available) {
                    try (Socket socket = new Socket(this.host, this.port);
                         ObjectOutputStream sender = new ObjectOutputStream(socket.getOutputStream());
                         ObjectInputStream receiver = new ObjectInputStream(socket.getInputStream())
                    ) {

                        this.socket = socket;
                        this.objectOutputStream = sender;
                        this.objectInputStream = receiver;


                        if (this.loginToServer()) {

                            future.complete(null);

                            while (this.available) {
                                try {
                                    Object incomingObject;
                                    synchronized (this.objectIncoming) {
                                        incomingObject = this.objectInputStream.readObject();
                                    }

                                    if (this.clientListener != null) {
                                        this.clientListener.onReceive(this, incomingObject);
                                    }

                                    //Only the components matter
                                    if (incomingObject instanceof Component) {
                                        Component component = (Component) incomingObject;
                                        if (component.isReply()) {
                                            this.catcher.handleReply(component);
                                        } else {
                                            this.catcher.handleComponent(this, component);
                                        }
                                    }
                                } catch (ClassNotFoundException e) {
                                    //We ignoring this exception
                                }
                            }
                        }

                        future.complete(this);

                    } catch (IOException e) {
                        if (e instanceof java.net.SocketException) {
                            return;
                        }
                        if (this.options.isDebug()) {
                            e.printStackTrace();
                        }
                    }

                    if (loopedAmount++ == this.options.getMaxRetry()) {
                        this.available = false;
                        break;
                    }

                    //Retrying with the given retry delay
                    this.available = false;
                    TimeUnit.MILLISECONDS.sleep(this.options.getRetryDelay());
                }
                if (this.clientListener != null) {
                    this.clientListener.onDisconnect();
                }
            } catch (Exception e) {
                if (this.options.isDebug()) {
                    e.printStackTrace();
                }
            } finally {
                this.catcher.shutdown();
                future.complete(this); // and we can free the constructor
            }
        });

        executorService.shutdown();
        future.join();

        return future;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    @Override
    public UserManager getUserManager() {
        return new UserManager(this);
    }

    /**
     * This waits for the login to be accepted
     * it sends the username of this client and the credentials
     *
     * @return if accepted by server
     */
    @SneakyThrows
    private boolean loginToServer()  {
        Object objectIncoming;
        synchronized (this.objectOutgoing) {
            this.objectOutputStream.writeUTF(this.hytoraLogin.getName());
            this.objectOutputStream.writeUTF(this.hytoraLogin.getCredentials());
            this.objectOutputStream.flush();

            objectIncoming = this.objectInputStream.readObject();
        }

        if (!(objectIncoming instanceof String)) {
            this.catcher.handleLogin("ERROR", "The first received object by the client was not an AuthResponse but a " + objectIncoming.getClass().getName() + "!");
            return false;
        }

        String response = (String) objectIncoming;

        if (response.equalsIgnoreCase("OK")) {
            this.catcher.handleLogin(response, " Connected and logged in as '" + this.hytoraLogin.getName() + "'");

            //Login successful handling consumer
            if (this.loginConsumer != null) {
                this.loginConsumer.accept(this);
                this.loginConsumer = null;
            }
            this.available = true;
            return true;
        } else {
            this.catcher.handleLogin(response, " Invalid credential for user '" + this.hytoraLogin.getName() + "'");
            this.available = false;
            return false;
        }
    }

    /**
     * Sends a {@link Component} and receives a reply
     * if given else it will just time out and set an empty reply
     *
     * @param component the component
     * @return reply or empty reply
     */
    public Component sendComponentToReply(Component component) {
        if (!this.available) {
            return new Component();
        }

        int timeOut = 3000;
        Component[] hytoraReply = {null};

        this.sendComponent(component, 0, (reply, aBoolean) -> hytoraReply[0] = reply);

        int count = 0;
        while (hytoraReply[0] == null && count++ < timeOut) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (count >= timeOut) {
            hytoraReply[0] = new Component();
        }

        return hytoraReply[0];
    }

    /**
     * Sends a {@link Component} and receives a reply
     * if given else it will just time out and set an empty reply
     *
     * @param consumer the consumer
     * @return reply or empty reply
     */
    public Component sendComponentToReply(Consumer<Component> consumer) {
        Component component = new Component();
        consumer.accept(component);

        return this.sendComponentToReply(component);
    }

    /**
     * Sends a component to the server
     *
     * @param component the component
     */
    @Override @SneakyThrows
    public void sendComponent(Component component) {
        this.sendObject(component);
    }


    /**
     * Sends an object which is {@link Serializable}
     *
     * @param object the object
     */
    @Override @SneakyThrows
    public void sendObject(Serializable object) {

        if (!this.available) {
            return;
        }
        synchronized (this.objectOutgoing) {
            this.objectOutputStream.writeObject(object);
            this.objectOutputStream.flush();
        }
    }

    @Override
    public String getName() {
        return this.hytoraLogin == null ? "HytoraClient@" + host + ":" + port : this.hytoraLogin.getName();
    }

    /**
     * Sends a component to the server
     * If the client is not connected, the request will be placed on a waiting list to be sent later.
     *
     * @param consumer The Message request consumer.
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
     * @param componentConsumer the consumer for the component
     * @param delay the timeout delay
     * @param replyConsumer the callback for the reply
     */
    public void sendComponent(Consumer<Component> componentConsumer, int delay, BiConsumer<Component, Boolean> replyConsumer) {
        Component hytoraComponent = new Component();
        componentConsumer.accept(hytoraComponent);

        this.sendComponent(hytoraComponent, delay, replyConsumer);

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

        Field requestID = hytoraComponent.getClass().getDeclaredField("requestID");
        requestID.setAccessible(true);
        requestID.set(hytoraComponent, this.random.nextLong());


        this.catcher.registerReplyHandler(hytoraComponent.getRequestID(), delay, replyConsumer);

        this.sendComponent(hytoraComponent);

    }

    /**
     * Registers a channel handler to receive components
     *
     * @param channel the channel to listen for
     * @param consumer the consumer
     */
    public void registerChannelHandler(String channel, Consumer<RepliableComponent> consumer) {
        this.catcher.registerChannelHandler(channel, consumer);
    }


    /**
     * Closes the connection
     * between SERVER <-> CLIENT
     * and shuts down all managers
     */
    @Override
    public void close() {
        try {
            this.available = false;
            this.socket.close();
            this.catcher.shutdown();
        } catch (IOException e) {
            if (this.options.isDebug()) {
                e.printStackTrace();
            }
        }
    }



}
