package net.hytora.networking.connection.server;


import lombok.Getter;
import net.hytora.networking.connection.HytoraConnectionBridge;
import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.other.TimedHashMap;
import net.hytora.networking.elements.component.Component;


import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class ServerCatcher {

    /**
     * The registered message events and their handlers
     */
    private final Map<String, Set<Consumer<RepliableComponent>>> channelHandlers;

    /**
     * The registered handlers for connections
     */
    private final List<Consumer<HytoraConnectionBridge>> connectionHandlers;

    /**
     * The registered reply events and their handlers
     */
    private final TimedHashMap<Long, BiConsumer<Component, Boolean>> replyHandlers;

    public ServerCatcher() {
        this.channelHandlers = new HashMap<>();
        this.replyHandlers = new TimedHashMap<>();
        this.connectionHandlers = new LinkedList<>();
    }
    /**
     * Closes this catcher
     */
    public void shutdown() {
        this.connectionHandlers.clear();
        this.channelHandlers.clear();

        this.replyHandlers.clear();
        this.replyHandlers.close();
    }

    /**
     * Registers a channel handler to listen for a channel
     *
     * @param name the channel
     * @param consumer the consumer
     */
    public void registerChannelHandler(String name, Consumer<RepliableComponent> consumer) {
        Set<Consumer<RepliableComponent>> candidate = channelHandlers.get(name);
        if (candidate != null) {
            candidate.add(consumer);
        } else {
            candidate = new HashSet<>();
            candidate.add(consumer);
            channelHandlers.put(name, candidate);
        }
    }

    /**
     * Registers a handler for a reply to reply to some query
     *
     * @param id the id of the query
     * @param delay the delay
     * @param consumer the consumer
     */
    public void registerReplyHandler(long id, int delay, BiConsumer<Component, Boolean> consumer) {
        replyHandlers.put(id, consumer, delay);
    }

    /**
     * Registers a handler for whenever a client connects
     *
     * @param consumer the consumer
     */
    public void registerLoginHandler(Consumer<HytoraConnectionBridge> consumer) {
        this.connectionHandlers.add(consumer);
    }

    /**
     * Handles a component
     *
     * @param hytoraComponent the component
     * @param client the client
     */
    public void handleComponent(Component hytoraComponent, HytoraConnectionBridge client) {
        Set<Consumer<RepliableComponent>> candidates = channelHandlers.get(hytoraComponent.getChannel());
        if (candidates != null) {
            for (Consumer<RepliableComponent> consumer : candidates) {
                consumer.accept(new RepliableComponent(client, hytoraComponent));
            }
        }
    }

    /**
     * Handles a reply
     *
     * @param hytoraReply the reply
     */
    public void handleReply(Component hytoraReply) {
        BiConsumer<Component, Boolean> candidate = replyHandlers.get(hytoraReply.getRequestID());
        if (candidate != null) {
            candidate.accept(hytoraReply, true);
            replyHandlers.remove(hytoraReply.getRequestID());
        }
    }
}
