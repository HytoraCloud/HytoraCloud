package de.lystx.hytoracloud.driver.connection.protocol.netty.messenger;

import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;

public interface IChannelHandler {


    /**
     * Called when handling
     *
     * @param packet the packet
     * @param json the json string
     * @param message the channel message
     */
    void handle(PacketChannelMessage packet, String json, IChannelMessage message);
}
