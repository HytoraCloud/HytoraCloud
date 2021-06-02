package de.lystx.hytoracloud.module.serverselector.cloud.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.SignService;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import io.thunder.packet.handler.PacketHandler;

import io.thunder.packet.Packet;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class PacketHandlerCloudSign implements PacketHandler {

    private final CloudDriver cloudDriver;

    
    public void handle(Packet packet) {
        if (packet instanceof PacketInformation) {
            PacketInformation packetInformation = (PacketInformation)packet;
            Map<String, Object> map = packetInformation.getObjectMap();
            if (packetInformation.getKey().equalsIgnoreCase("PacketInCreateSign")) {
                CloudSign sign = new CloudSign().deserialize((Map<String, Object>) map.get("sign"), CloudSign.class);
                CloudSign get = this.cloudDriver.getInstance(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
                if (get == null) {
                    this.cloudDriver.getInstance(SignService.class).getCloudSigns().add(sign);
                    this.cloudDriver.getInstance(SignService.class).save();
                    this.cloudDriver.getInstance(SignService.class).load();
                    this.cloudDriver.getInstance(SignService.class).loadSigns();
                    this.cloudDriver.reload();
                }
            } else if (packetInformation.getKey().equalsIgnoreCase("PacketInDeleteSign")) {
                CloudSign sign = new CloudSign().deserialize((Map<String, Object>) map.get("sign"), CloudSign.class);
                CloudSign get = this.cloudDriver.getInstance(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
                if (get != null) {
                    this.cloudDriver.getInstance(SignService.class).getCloudSigns().remove(get);
                    this.cloudDriver.getInstance(SignService.class).save();
                    this.cloudDriver.getInstance(SignService.class).load();
                    this.cloudDriver.getInstance(SignService.class).loadSigns();
                    this.cloudDriver.reload();
                }
            }

        }
    }
}
