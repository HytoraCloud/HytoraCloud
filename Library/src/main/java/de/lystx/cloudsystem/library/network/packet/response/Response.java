package de.lystx.cloudsystem.library.network.packet.response;

import de.lystx.cloudsystem.library.network.extra.util.ReflectionUtil;
import lombok.Getter;
import de.lystx.cloudsystem.library.network.extra.exception.NettyInputException;
import de.lystx.cloudsystem.library.network.packet.impl.PacketRespond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {

    @Getter
    private PacketRespond handle;

    /**
     * The header title of the packet (e.g. "databaseInfo")
     */
    @Getter
    private String header;

    /**
     * The response status of the task from the packet. Similar to http(s) responses
     */
    @Getter
    private ResponseStatus status;

    /**
     * The message of the {@link #handle} as list
     */
    private List<String> message;

    /**
     * The element map (of complex ones) if the message has already been converted
     */
    @Getter
    private final Map<Class<?>, List<?>> complexElementMap = new HashMap<>();

    /**
     * The element map (of primitive ones) if the message has already been converted
     */
    @Getter
    private final Map<Class<?>, List<?>> primitiveElementMap = new HashMap<>();

    @Getter
    private long processingTime;

    public Response(PacketRespond respond) {
        if(respond == null) return;
        this.handle = respond;
        this.header = respond.header;
        this.status = respond.status;
        this.message = respond.message;

        this.processingTime = System.currentTimeMillis() - respond.getStamp();
    }

    public Response(ResponseStatus status) {
        this(new PacketRespond(status));
    }

    public Response(List<String> msg) {
        this(new PacketRespond("", msg, ResponseStatus.SUCESS));
    }

    /**
     * Checks the state
     *
     * @throws NettyInputException If the state is not OK
     */
    public void checkState() throws NettyInputException {
        if(status.isCritically()) {
            throw new IllegalStateException("Received critical state: " + status);
        }
        if(!isOk()) throw new NettyInputException(this);
    }

    /**
     * Checks if the status is OK
     *
     * @return The result as boolean
     */
    public boolean isOk() {
        return getStatus() == ResponseStatus.SUCESS;
    }

    /**
     * Checks if the status is NBOK
     *
     * @return The result as boolean
     */
    public boolean isNotOk() {
        return getStatus() == ResponseStatus.FAILED;
    }

    /**
     * Gets the message as list
     *
     * @return The list of strings
     */
    public List<String> getMessageAsList() {
        if(message == null || message.isEmpty() || message.get(0).isEmpty()) return new ArrayList<>();
        return message;
    }

    /**
     * Gets the message of the response
     *
     * @return The message
     */
    public String getMessage() {
        if(message.isEmpty()) return "";
        return message.get(0);
    }

    /**
     * Gets the string from the response with given index
     *
     * @param index The index
     * @return The string
     */
    public String get(int index) {
        List<String> l = getMessageAsList();
        if(index < 0 || index >= l.size()) return null;
        return l.get(index);
    }
    /**
     * Casts the response message into a list of given eClass's objects
     * This method is only for primitive types like {@link Integer}
     *
     * @param eClass The element class
     * @param <E>    The element type
     * @return The list of elements
     */
    public <E> List<E> toPrimitives(Class<E> eClass) throws NettyInputException {
        this.checkState();
        if(primitiveElementMap.containsKey(eClass)) return (List<E>) primitiveElementMap.get(eClass);
        List<E> l = new ArrayList<>();

        for(String msg : getMessageAsList()) {
            Object o = ReflectionUtil.safeCast(msg);

            if(o != null && eClass.isAssignableFrom(o.getClass())) {
                l.add((E) o);
            }
        }
        primitiveElementMap.put(eClass, l);
        return l;
    }

    public <E> E toPrimitive(Class<E> eClass) throws NettyInputException {
        this.checkState();
        List<E> l = toPrimitives(eClass);
        if(l.isEmpty()) return null;
        return l.get(0);
    }

}
