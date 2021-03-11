package de.lystx.cloudsystem.cloud.handler.managing;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInCreateSign;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInDeleteSign;
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
        if (packet instanceof PacketInCreateSign) {
            PacketInCreateSign packetInCreateSign = (PacketInCreateSign)packet;
            CloudSign sign = packetInCreateSign.getCloudSign();
            CloudSign get = this.cloudSystem.getService(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
            if (get == null) {
                this.cloudSystem.getService(SignService.class).getCloudSigns().add(sign);
                this.cloudSystem.getService(SignService.class).save();
                this.cloudSystem.getService(SignService.class).load();
                this.cloudSystem.getService(SignService.class).loadSigns();
                this.cloudSystem.reload();
            }

        } else if (packet instanceof PacketInDeleteSign) {
            PacketInDeleteSign packetInDeleteSign = (PacketInDeleteSign)packet;
            CloudSign sign = packetInDeleteSign.getCloudSign();
            CloudSign get = this.cloudSystem.getService(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
            if (get != null) {
                this.cloudSystem.getService(SignService.class).getCloudSigns().remove(get);
                this.cloudSystem.getService(SignService.class).save();
                this.cloudSystem.getService(SignService.class).load();
                this.cloudSystem.getService(SignService.class).loadSigns();
                this.cloudSystem.reload();
            }
        }
    }
}
