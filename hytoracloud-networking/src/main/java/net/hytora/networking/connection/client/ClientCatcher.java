package net.hytora.networking.connection.client;


import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.other.TimedHashMap;
import net.hytora.networking.elements.component.Component;

import lombok.Getter;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class ClientCatcher {

    /**
     * All the message events and their handlers
     */
    private final Map<String, Set<Consumer<RepliableComponent>>> channelHandlers;

    /**
     * All the login handlers
     */
    private final List<BiConsumer<String, String>> loginHandler;

    /**
     * All reply events and their handlers
     */
    private final TimedHashMap<Long, BiConsumer<Component, Boolean>> replyHandlers;

    public ClientCatcher() {
        this.channelHandlers = new HashMap<>();
        this.replyHandlers = new TimedHashMap<>();
        this.loginHandler = new ArrayList<>();
    }

    /**
     * Handles the login for a given code and message
     * for all registered handlers
     *
     * @param code the code (OK, ERROR etc)
     * @param message the provided message
     */
    public void handleLogin(String code, String message) {
        for (BiConsumer<String, String> consumer : this.loginHandler) {
            consumer.accept(code, message);
        }
    }

    /**
     * Closes this catcher
     */
    public void shutdown() {
        this.replyHandlers.clear();
        this.loginHandler.clear();
        this.channelHandlers.clear();
        this.replyHandlers.close();
    }

    /**
     * Registers a handler for an incoming channel
     *
     * @param channel the channel
     * @param consumer the consumer
     */
    public void registerChannelHandler(String channel, Consumer<RepliableComponent> consumer) {
        Set<Consumer<RepliableComponent>> candidate = channelHandlers.get(channel);
        if (candidate != null) {
            candidate.add(consumer);
        } else {
            candidate = new HashSet<>();
            candidate.add(consumer);
            channelHandlers.put(channel, candidate);
        }
    }

    /**
     * Registers a handler for the login of the client
     *
     * @param consumer the consumer to handle
     */
    public void registerLoginHandler(BiConsumer<String, String> consumer) {
        this.loginHandler.add(consumer);
    }

    /**
     * Registers a handler for an incoming event
     *
     * @param id the request id
     * @param delay the delay
     * @param consumer the consumer to handle
     */
    public void registerReplyHandler(long id, int delay, BiConsumer<Component, Boolean> consumer) {
        replyHandlers.put(id, consumer, delay);
    }

    /**
     * Handles a given {@link Component} for a given {@link HytoraClient}
     *
     * @param client the client
     * @param hytoraComponent the component
     */
    public void handleComponent(HytoraClient client, Component hytoraComponent) {
        Set<Consumer<RepliableComponent>> candidates = channelHandlers.get(hytoraComponent.getChannel());
        if (candidates != null) {
            for (Consumer<RepliableComponent> consumer : candidates) {
                consumer.accept(new RepliableComponent(client, hytoraComponent));
            }
        }
    }

    /**
     * Handles a given reply as {@link Component} for this catcher
     *
     * @param hytoraReply the reply as component
     */
    public void handleReply(Component hytoraReply) {
        BiConsumer<Component, Boolean> candidate = replyHandlers.get(hytoraReply.getRequestID());
        if (candidate != null) {
            candidate.accept(hytoraReply, true);
            replyHandlers.remove(hytoraReply.getRequestID());
        }
    }
}

