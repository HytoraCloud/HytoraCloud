package de.lystx.cloudsystem.handler.managing;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInCreateCloudSign;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInDeleteCloudSign;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import de.lystx.cloudsystem.library.service.serverselector.sign.SignService;

public class PacketHandlerCloudSign extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerCloudSign(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInCreateCloudSign) {
            PacketPlayInCreateCloudSign packetPlayInCreateCloudSign = (PacketPlayInCreateCloudSign)packet;
            CloudSign sign = packetPlayInCreateCloudSign.getCloudSign();
            CloudSign get = this.cloudSystem.getService(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
            if (get == null) {
                this.cloudSystem.getService(SignService.class).add(sign);
                this.cloudSystem.getService(SignService.class).save();
                this.cloudSystem.getService(SignService.class).load();
                this.cloudSystem.getService(SignService.class).loadSigns();
                this.cloudSystem.reload();
            }

        } else if (packet instanceof PacketPlayInDeleteCloudSign) {
            PacketPlayInDeleteCloudSign packetPlayInDeleteCloudSign = (PacketPlayInDeleteCloudSign)packet;
            CloudSign sign = packetPlayInDeleteCloudSign.getCloudSign();
            CloudSign get = this.cloudSystem.getService(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
            if (get != null) {
                this.cloudSystem.getService(SignService.class).remove(get);
                this.cloudSystem.getService(SignService.class).save();
                this.cloudSystem.getService(SignService.class).load();
                this.cloudSystem.getService(SignService.class).loadSigns();
                this.cloudSystem.reload();
            }
        }
    }
}
