package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.elements.interfaces.CloudService;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;

import java.io.Serializable;
import java.util.function.Consumer;

public class Packet implements Serializable {



    //Document serialisation via String
    protected String document = "{}";
    private Unsafe unsafe = null;

    /**
     * Appends a value to the packet document
     * @param key
     * @param value
     * @return current packet
     */
    public Packet append(String key, Object value) {
        this.document = this.document().append(key, value).toString();
        return this;
    }

    /**
     * Appeding whole value to packet
     * @param value
     * @return current packet
     */
    public Packet append(Object value) {
        this.document = this.document().append(value).toString();
        return this;
    }

    /**
     * Gets packet as provided class object
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getAs(Class<T> tClass) {
        return this.document().getAs(tClass);
    }

    /**
     * Transforms packet to document
     * @return Document
     */
    public Document document() {
        return new Document(this.document);
    }

    /**
     * Creates an Unsafe way to send packets
     * Not recommendet to make Packets async because
     * they might not work properly anymore!
     * It's recommendet to not use this!
     * @return Unsafe
     */
    @Deprecated
    public Unsafe unsafe() {
        if (unsafe == null) {
            unsafe = new Unsafe();
        }
        return unsafe;
    }

    /**
     * Its not recommended to use this method
     * But you have to say it looks pretty nice doesn't it?
     */
    @Getter
    public class Unsafe implements Serializable {

        protected boolean async; //Wheter the packet should be send sync or async

        /**
         * Creates the unsafe packet
         */
        public Unsafe() {
            this.async = false;
        }

        /**
         * Sends the packet very unsafe
         * @param cloudExecutor
         */
        public void send(CloudExecutor cloudExecutor) {
            cloudExecutor.sendPacket(Packet.this);
        }

        public void send(CloudExecutor cloudExecutor, Consumer<PacketState> consumer) {
            cloudExecutor.sendPacket(Packet.this, consumer);
        }

        /**
         * Sends a packet with {@link CloudService}
         * @param cloudService
         */
        public void send(CloudService cloudService) {
            cloudService.getCurrentExecutor().sendPacket(Packet.this);
        }

        public void send(CloudService cloudService, Consumer<PacketState> consumer) {
            cloudService.getCurrentExecutor().sendPacket(Packet.this, consumer);
        }

        /**
         * Not recommendet to use
         * The {@link Constants#EXECUTOR} could be null
         * or not defined yet!
         */
        @Deprecated
        public void send() {
            Constants.EXECUTOR.sendPacket(Packet.this);
        }

        @Deprecated
        public void send(Consumer<PacketState> consumer) {
            Constants.EXECUTOR.sendPacket(Packet.this, consumer);
        }

        /**
         * Makes Packet sending async
         * @return current Packet
         */
        public Unsafe async() {
            this.async = true;
            return this;
        }

        /**
         * Makes Packet sending sync
         * @return current Packet
         */
        public Unsafe sync() {
            this.async = false;
            return this;
        }
    }
}
