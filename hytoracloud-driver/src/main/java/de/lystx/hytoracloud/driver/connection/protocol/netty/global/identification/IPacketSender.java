package de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;

public interface IPacketSender {

    /**
     * Sends an {@link IPacket}
     *
     * @param packet the packet
     */
    void sendPacket(IPacket packet);

    /**
     * The parent connection
     *
     * @return the parent
     */
    INetworkConnection parent();
}
