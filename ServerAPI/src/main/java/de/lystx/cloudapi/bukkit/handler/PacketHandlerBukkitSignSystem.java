package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutCloudSigns;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.SignLayOut;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

@Getter
public class PacketHandlerBukkitSignSystem extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitSignSystem(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutCloudSigns) {
            CloudAPI.getInstance().setJoinable(true);
            PacketPlayOutCloudSigns packetPlayOutCloudSigns = (PacketPlayOutCloudSigns)packet;
            CloudServer.getInstance().getSignManager().setSignLayOut(new SignLayOut(new Document(packetPlayOutCloudSigns.getSignLayOut())));
            CloudServer.getInstance().getSignManager().setCloudSigns(packetPlayOutCloudSigns.getCloudSigns());
            CloudServer.getInstance().getSignManager().run();
        }
    }
}
