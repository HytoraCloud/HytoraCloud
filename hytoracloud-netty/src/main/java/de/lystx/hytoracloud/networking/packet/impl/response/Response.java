package de.lystx.hytoracloud.networking.packet.impl.response;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.networking.provided.objects.NetworkObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class Response {

    /**
     * The response status of the task from the packet. Similar to http(s) responses
     */
    private ResponseStatus status;

    /**
     * The message of the response
     */
    private String message;

    /**
     * A list of serialized objects
     */
    private List<NetworkObject> networkObjects;

    /**
     * The time the response started
     */
    @Setter
    private long stamp = 1L;

    public Response(PacketRespond respond) {

        if (respond == null) {
            return;
        }

        this.status = respond.getStatus();
        this.message = respond.getMessage();
        this.networkObjects = respond.getNetworkObjects();

    }

    public Response(ResponseStatus status) {
        this(new PacketRespond(status));
    }

    public Response(String message) {
        this(new PacketRespond(message, ResponseStatus.OK));
    }

    /**
     * Transforms this {@link Response} to a {@link com.google.gson.JsonObject}
     *
     * @return jsonBuilder
     */
    public JsonBuilder toJson() {
        return new JsonBuilder(this.message);
    }

    /**
     * Transforms an object stored in the message
     *
     * @param key the key where its located
     * @param tClass the class of the object you want
     * @param <T> the genericType
     * @return object or null if something goes wrong
     */
    public <T> T get(Object key, Class<T> tClass) {
        try {
            return toJson().getObject(String.valueOf(key), tClass);
        } catch (Exception e) {
            return null;
        }
    }
}
