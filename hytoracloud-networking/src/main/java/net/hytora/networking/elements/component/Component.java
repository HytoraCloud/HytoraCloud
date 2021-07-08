package net.hytora.networking.elements.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter
public class Component implements Serializable {

    /**
     * The serialVersionUID
     */
    private static final long serialVersionUID = -3107782551846954635L;

    /**
     * the id of the request
     */
    private long idRequest;

    /**
     * The recipient of this message
     */
    private String recipient;

    /**
     * The sender of this message
     */
    private String sender;

    /**
     * The channel to send it to
     */
    private String channel;

    /**
     * The content of this message
     */
    private Object content;

    /**
     * If this component
     * is a reply or a message
     */
    private boolean reply;

    /**
     * Constructs a default Message
     */
    public Component() {
        this.channel = "main";
        this.recipient = "server";
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
        this.content = map;
        return this;
    }

    /**
     * Puts a value stored under a key
     *
     * @param key the key
     * @param value the value
     * @return current component
     */
    public Component put(String key, Object value) {
        return this.append(map -> map.put(key, value));
    }

    /**
     * Tries to return this content as map if it is
     * an instance of map
     * otherwise a empty {@link HashMap} will be returned
     *
     * @return map or empty map
     */
    public <K, V> Map<K, V> getContentAsMap() {
        if (this.content instanceof Map) {
            return (Map<K, V>) this.content;
        }
        return new HashMap<>();
    }

    /**
     * Gets an object from this content
     *
     * @param key the key where its stored
     * @return object
     */
    public Object getObject(String key) {
        return this.getContentAsMap().get(key);
    }

    /**
     * Gets an object from this content
     *
     * @param key the key where its stored
     * @return object
     */
    public <T> T get(String key) {
        return (T) this.getObject(key);
    }

    /**
     * Gets an object from this content
     * as String
     *
     * @param key the key where its stored
     * @return object
     */
    public String getString(String key) {
        return (String) this.getObject(key);
    }

    /**
     * Gets an object from this content
     * as integer
     *
     * @param key the key where its stored
     * @return object
     */
    public Integer getInteger(String key) {
        return (Integer) this.getObject(key);
    }

    /**
     * Gets an object from this content
     * as long
     *
     * @param key the key where its stored
     * @return object
     */
    public Long getLong(String key) {
        return (Long) this.getObject(key);
    }

    /**
     * Gets an object from this content
     * as boolean
     *
     * @param key the key where its stored
     * @return object
     */
    public Boolean getBoolean(String key) {
        return (Boolean) this.getObject(key);
    }

    /**
     * Gets an object from this content
     * as uuid
     *
     * @param key the key where its stored
     * @return object
     */
    public UUID getUUID(String key) {
        return (UUID) this.getObject(key);
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
        this.content = contentAsMap;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this.getContentAsMap());
    }


    @AllArgsConstructor
    public static class Reply {

        private final Component hytoraComponent;

        @Getter
        private final long time;

        public Reply(Component hytoraComponent) {
            this.hytoraComponent = hytoraComponent;

            this.time = hytoraComponent.has("_time") ? hytoraComponent.getLong("_time") : System.currentTimeMillis();
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
            return ResponseStatus.valueOf(this.hytoraComponent.getString("_status"));
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
            return this.hytoraComponent.getString("_message");
        }
    }

}
