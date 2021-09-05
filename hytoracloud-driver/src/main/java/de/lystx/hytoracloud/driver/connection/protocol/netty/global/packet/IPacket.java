package de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response.PacketRespond;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response.ResponseStatus;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

public interface IPacket {

    /**
     * The {@link UUID} of the packet
     *
     * @return uuid
     */
    UUID getUniqueId();

    /**
     * Sets the {@link UUID} of this packet
     * @param uniqueId the uuid
     */
    void setUniqueId(UUID uniqueId);

    /**
     * The netty {@link Channel} of this packet
     *
     * @return channel
     */
    INetworkChannel getChannel();

    /**
     * Sets the channel instance of this packet
     *
     * @param channel the channel
     */
    void setChannel(INetworkChannel channel);

    /**
     * The time when it was sent
     *
     * @return long millis
     */
    long getStamp();

    default long getDif() {
        return System.currentTimeMillis() - getStamp();
    }

    /**
     * Get the address of the channel
     *
     * @return address
     */
    InetSocketAddress getAddress();
    /**
     * The buf where all the data is stored
     *
     * @return buf
     */
    ByteBuf getBuf();

    /**
     * The protocolVersion to identify packets
     *
     * @return id as int
     */
    int getProtocolVersion();

    /**
     * Converts given bytebuffer into this packets
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
     */
    void read(PacketBuffer buf) throws IOException;

    /**
     * Converts this packets into given bytebuffer
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
     */
    void write(PacketBuffer buf) throws IOException;


    //Query Managing =================================


    default void respond(INetworkConnection connection, JsonDocument document, ResponseStatus responseStatus) {
        IPacket packetRespond = new PacketRespond(responseStatus, document);
        packetRespond.setUniqueId(this.getUniqueId());
        connection.sendPacket(packetRespond);
    }

    default PacketRespond createQuery(INetworkConnection connection) {

        UUID uniqueId = this.getUniqueId();
        int timeOut = 3000;
        long start = System.currentTimeMillis();
        PacketRespond[] response = {null};

        connection.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handle(IPacket packet) {

                if (packet instanceof PacketRespond) {
                    PacketRespond packetRespond = (PacketRespond)packet;
                    if (packet.getUniqueId().equals(uniqueId)) {
                        response[0] = packetRespond;
                        connection.unregisterPacketHandler(this);
                    }
                }
            }
        });

        connection.sendPacket(this);
        int count = 0;
        while (response[0] == null && count++ < timeOut) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (count >= timeOut) {
            response[0] = new PacketRespond(ResponseStatus.FAILED, (JsonDocument) new JsonDocument().append("message", "The request timed out!"));
        }
        return response[0];
    }
}
