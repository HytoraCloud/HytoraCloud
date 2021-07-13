package net.hytora.networking.elements.component;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.hytora.networking.elements.packet.PacketManager;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class Component implements Serializable {

    /**
     * the id of the request
     */
    private long requestID;

    /**
     * The recipient of this message
     */
    @Setter
    private String receiver;

    /**
     * The sender of this message
     */
    @Setter
    private String sender;

    /**
     * The channel to send it to
     */
    @Setter
    private String channel;

    /**
     * The content of this message
     */
    @Setter
    private Serializable message;

    /**
     * If this component
     * is a reply or a message
     */
    private final boolean reply;

    /**
     * Constructs a default Message
     */
    public Component() {
        this.channel = "main";
        this.receiver = "server";
        this.sender = "no_sender_provided";
        this.reply = false;
    }

    /**
     * Creates a reply from this component
     * to get status and other information out of it
     *
     * @return reply object
     */
    public Reply reply() {
        return new Reply(this);
    }

    /**
     * Allows to modify the map contents of this message
     *
     * @param consumer the consumer to handle the map
     * @param <K> generic key type
     * @param <V> generic value type
     */
    public <K, V> Component append(Consumer<Map<K, V>> consumer) {
        Map<K, V> map = this.getContentAsMap();
        consumer.accept(map);
        this.message = (Serializable) map;
        return this;
    }

    /**
     * Puts a value stored under a key
     *
     * @param key the key
     * @param value the value
     * @return current component
     */
    public <V> Component put(String key, V value) {
        return this.append(map -> map.put(key, value));
    }

    /**
     * Puts an {@link ComponentObject} in this component
     *
     * @param object the object
     * @param <V> generic type
     * @return current component
     */
    public <V> Component putObject(String key, ComponentObject<V> object) {
        this.put(key, object.getClass().getName());
        object.write(this);
        return this;
    }

    /**
     * Tries to return this content as map if it is
     * an instance of map
     * otherwise a empty {@link HashMap} will be returned
     *
     * @return map or empty map
     */
    public <K, V> Map<K, V> getContentAsMap() {
        if (this.message instanceof Map) {
            return (Map<K, V>) this.message;
        }
        return new HashMap<>();
    }

    /**
     * Gets an object from this content
     *
     * @param key the key where its stored
     * @return object
     */
    public <T> T get(String key) {

        Object object = this.getContentAsMap().get(key);

        return (T) object;
    }

    @SneakyThrows
    public <V> V getComponentObject(String key) {
        if (!this.has(key)) {
            return null;
        }
        String class_ = this.get(key);
        this.remove(key);

        Class<? extends ComponentObject<?>> componentClass = (Class<? extends ComponentObject<?>>) Class.forName(class_);
        ComponentObject<V> componentObject = (ComponentObject<V>) PacketManager.getInstance(componentClass);

        if (componentObject != null) {
            return componentObject.read(this);
        }

        return null;
    }

    /**
     * Checks if a specific value is stored
     * under a key
     *
     * @param key the key
     * @return if contains the key
     */
    public boolean has(String key) {
        return this.getContentAsMap().containsKey(key);
    }

    /**
     * Removes a value from the content
     * if its a map
     *
     * @param key the key
     */
    public void remove(String key) {
        if (!this.has(key)) {
            return;
        }
        Map<Object, Object> contentAsMap = this.getContentAsMap();
        contentAsMap.remove(key);
        this.message = (Serializable) contentAsMap;
    }

    @Override
    public String toString() {
        Map<Object, Object> map = this.getContentAsMap();

        if (map.isEmpty()) {
            map.put("message", this.message);
        }

        return map.toString();
    }


    @AllArgsConstructor
    public static class Reply {

        private final Component hytoraComponent;

        @Getter
        private final long time;

        public Reply(Component hytoraComponent) {
            this.hytoraComponent = hytoraComponent;

            this.time = hytoraComponent.has("_time") ? hytoraComponent.get("_time") : System.currentTimeMillis();
            hytoraComponent.remove("_time");
        }


        /**
         * Gets the {@link ResponseStatus} if this component
         * is part of a response from a query
         *
         * @return status or errors
         */
        public ResponseStatus getStatus() {
            if (!this.hytoraComponent.has("_status")) {
                return ResponseStatus.NOT_FOUND;
            }
            return ResponseStatus.valueOf(this.hytoraComponent.get("_status"));
        }

        /**
         * Gets the Message if this component
         * is part of a response from a query
         *
         * @return status or errors
         */
        public String getMessage() {
            if (!this.hytoraComponent.has("_message")) {
                return "Not a Query-Reply component";
            }
            return this.hytoraComponent.get("_message");
        }
    }

}
