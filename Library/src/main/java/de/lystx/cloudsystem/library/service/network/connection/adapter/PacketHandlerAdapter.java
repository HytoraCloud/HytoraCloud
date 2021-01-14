package de.lystx.cloudsystem.library.service.network.connection.adapter;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public abstract class PacketHandlerAdapter {

    public abstract void handle(Packet packet);

}
