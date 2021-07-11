package net.hytora.networking.connection.server;

import lombok.SneakyThrows;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.connection.HytoraConnectionBridge;
import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.packet.PacketManager;
import net.hytora.networking.elements.other.UserManager;
import net.hytora.networking.elements.component.Component;

import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public class HytoraServer implements HytoraConnection {

    /**
     * The catcher manager
     */
    private final ServerCatcher catcher;

    /**
     * The user manager
     */
    private final UserManager userManager;

    /**
     * The server socket
     */
    private ServerSocket server;

    /**
     * The random
     */
    protected final Random random;

    /**
     * The packetManager to handle packets
     */
    private final PacketManager packetManager;

    /**
     * The port of the server
     */
    private final int port;

    /**
     * The login manager to check for logins
     */
    private BiFunction<String, String, Boolean> loginManager;

    /**
     * If the client is enabled
     */
    private boolean opened = true;

    /**
     * All already accepted names
     */
    private final List<String> acceptedNames;

    /**
     * Allows you to start the server with a port number and its secret key.
     *
     * @param port The listening port of the server.
     */
    public HytoraServer(int port) {

        this.port = port;
        this.acceptedNames = new LinkedList<>();

        this.userManager = new UserManager(this);
        this.catcher = new ServerCatcher();
        this.packetManager = new PacketManager(this);
        this.random = new Random();

        this.registerLoginHandler(bridge -> {
            this.sendComponent(component -> {
                component.setChannel("hytora_internal");
                component.setReceiver(bridge.getName());
                component.put("handshake", "true");
            });
        });
    }

    /**
     * Creates this server and starts it
     *
     * @return future
     */
    @Override
    public Future<HytoraConnection> createConnection() {

        CompletableFuture<HytoraConnection> future = new CompletableFuture<>();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                this.server = server;

                future.complete(this);

                while (this.opened && !server.isClosed()) {
                    Socket socket = server.accept();
                    socket.setSoTimeout(1000); // timeout 1 seconds while login
                    HytoraConnectionBridge bridge = new HytoraConnectionBridge(this, socket);
                }
            } catch (Exception e) {
                //Ignoring when server is closed
            } finally {
                future.complete(this);
                this.catcher.shutdown();
                this.userManager.shutdown();
            }
        });

        service.shutdown();
        future.join();

        return future;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return new InetSocketAddress(this.port);
    }

    /**
     * Sends an object to one client instance or all
     *
     * @param component the component to send
     */
    @SneakyThrows
    public void sendComponent(Component component) {
        this.sendObject(component, component.getReceiver());
    }

    @Override
    public void sendObject(Serializable object) {
        this.sendObject(object, "ALL");
    }

    /**
     * Sends an serializable {@link Object} to a given receiver
     *
     * @param object the object
     * @param receiver the receiver
     */
    public void sendObject(Serializable object, String receiver) {
        try {
            if (receiver.equalsIgnoreCase("ALL")) {
                for (HytoraConnectionBridge user : this.userManager.getConnectedUsers()) {
                    synchronized (user.getSyncOut()) {
                        user.getObjectOutputStream().writeObject(object);
                        user.getObjectOutputStream().flush();
                    }
                }
            } else if (!receiver.equalsIgnoreCase("SERVER")) {
                for (HytoraConnectionBridge user : this.userManager.getUsers(receiver)) {
                    synchronized (user.getSyncOut()) {
                        user.getObjectOutputStream().writeObject(object);
                        user.getObjectOutputStream().flush();
                    }
                }
            }
        } catch (Exception e) {
            //IGNORING THIS ONE
        }
    }

    @Override
    public String getName() {
        return "HytoraServer@" + port;
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
        hytoraComponent.setReceiver("ALL");
        componentConsumer.accept(hytoraComponent);

        this.sendComponent(hytoraComponent, delay, replyConsumer);

    }


    /**
     * Sends a {@link Component} but with a
     * consumer to work with lambda and not having
     * to create an extra component object
     *
     * @param consumer the consumer
     */
    public void sendComponent(Consumer<Component> consumer)  {

        Component hytoraComponent = new Component();
        hytoraComponent.setReceiver("ALL");
        consumer.accept(hytoraComponent);

        this.sendComponent(hytoraComponent);
    }

    /**
     * Registers a handler for a given channel to listen for
     *
     * @param channel the name of the channel
     * @param consumer the consumer
     */
    public void registerChannelHandler(String channel, Consumer<RepliableComponent> consumer) {
        this.catcher.registerChannelHandler(channel, consumer);
    }

    /**
     * Registers a handler for whenever a client connects
     *
     * @param consumer the consumer
     */
    public void registerLoginHandler(Consumer<HytoraConnectionBridge> consumer) {
        this.catcher.registerLoginHandler(consumer);
    }

    /**
     * Enables the login check to check
     * if a certain login is allowed or not
     *
     * @param loginCheckFunction the check
     */
    public void registerLoginHandler(BiFunction<String, String, Boolean> loginCheckFunction) {
        this.loginManager = loginCheckFunction;
    }

    /**
     * Closes this server
     */
    @Override
    public void close() {
        this.opened = false;
        try {
            this.server.close();
            this.userManager.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAvailable() {
        return opened;
    }
}
