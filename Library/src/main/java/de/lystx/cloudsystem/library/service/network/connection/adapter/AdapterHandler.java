package de.lystx.cloudsystem.library.service.network.connection.adapter;

import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.channel.handler.NetworkChannelHandler;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

@Getter
public class AdapterHandler {

    private final List<PacketHandlerAdapter> registeredadapters;

    public AdapterHandler() {
        this.registeredadapters = new LinkedList<>();
    }

    public void registerAdapter(PacketHandlerAdapter adapterHandler) {
        this.registeredadapters.add(adapterHandler);
    }

    public void unregisterAdapter(PacketHandlerAdapter adapterHandler) {
        this.registeredadapters.remove(adapterHandler);
    }


    public void handelAdapterHandler(NetworkChannel networkChannel, Packet packet) {
        try {
            for (PacketHandlerAdapter adapter : this.registeredadapters) {
                adapter.handle(packet);
            }
        } catch (ConcurrentModificationException e) {}
    }
}
