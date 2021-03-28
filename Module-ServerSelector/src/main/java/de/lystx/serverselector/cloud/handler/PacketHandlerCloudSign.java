package de.lystx.serverselector.cloud.handler;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.both.PacketInformation;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.serverselector.cloud.manager.sign.SignService;
import de.lystx.serverselector.cloud.manager.sign.base.CloudSign;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class PacketHandlerCloudSign extends PacketHandlerAdapter {

    private final CloudLibrary cloudLibrary;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInformation) {
            PacketInformation packetInformation = (PacketInformation)packet;
            Map<String, Object> map = packetInformation.getData();
            if (packetInformation.getKey().equalsIgnoreCase("PacketInCreateSign")) {
                CloudSign sign = new CloudSign().deserialize((Map<String, Object>) map.get("sign"), CloudSign.class);
                CloudSign get = this.cloudLibrary.getService(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
                if (get == null) {
                    this.cloudLibrary.getService(SignService.class).getCloudSigns().add(sign);
                    this.cloudLibrary.getService(SignService.class).save();
                    this.cloudLibrary.getService(SignService.class).load();
                    this.cloudLibrary.getService(SignService.class).loadSigns();
                    this.cloudLibrary.reload();
                }
            } else if (packetInformation.getKey().equalsIgnoreCase("PacketInDeleteSign")) {
                CloudSign sign = new CloudSign().deserialize((Map<String, Object>) map.get("sign"), CloudSign.class);
                CloudSign get = this.cloudLibrary.getService(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
                if (get != null) {
                    this.cloudLibrary.getService(SignService.class).getCloudSigns().remove(get);
                    this.cloudLibrary.getService(SignService.class).save();
                    this.cloudLibrary.getService(SignService.class).load();
                    this.cloudLibrary.getService(SignService.class).loadSigns();
                    this.cloudLibrary.reload();
                }
            }

        }
    }
}
