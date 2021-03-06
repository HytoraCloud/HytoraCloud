package de.lystx.hytoracloud.module.serverselector.cloud.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.SignService;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class PacketHandlerCloudSign implements PacketHandler {

    private final CloudDriver cloudDriver;

    
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketInformation) {
            PacketInformation packetInformation = (PacketInformation)packet;
            Map<String, Object> map = packetInformation.getObjectMap();
            if (packetInformation.getKey().equalsIgnoreCase("PacketInCreateSign")) {
                CloudSign sign = new CloudSign().deserialize((Map<String, Object>) map.get("sign"), CloudSign.class);
                CloudSign get = this.cloudDriver.getInstance(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
                if (get == null) {
                    this.cloudDriver.getInstance(SignService.class).getCloudSigns().add(sign);
                    this.cloudDriver.getInstance(SignService.class).saveAndReload();
                    this.cloudDriver.reload();
                }
            } else if (packetInformation.getKey().equalsIgnoreCase("PacketInDeleteSign")) {
                CloudSign sign = new CloudSign().deserialize((Map<String, Object>) map.get("sign"), CloudSign.class);
                CloudSign get = this.cloudDriver.getInstance(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
                if (get != null) {
                    this.cloudDriver.getInstance(SignService.class).getCloudSigns().remove(get);
                    this.cloudDriver.getInstance(SignService.class).saveAndReload();
                    this.cloudDriver.reload();
                }
            }

        }
    }
}
