package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

@Getter
public class PacketHandlerSubChannel extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerSubChannel(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunicationSubMessage) {
            PacketCommunicationSubMessage subMessage = (PacketCommunicationSubMessage)packet;
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onDocumentReceive(subMessage.getChannel(), subMessage.getKey(), subMessage.getDocument());
            }
        }
    }
}
