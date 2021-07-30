package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.SignService;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCloudSignCreate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCloudSignDelete;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CloudHandlerSignSystem implements PacketHandler {

    private final CloudDriver cloudDriver;

    @Override
    public void handle(Packet packet) {

        if (packet instanceof PacketInCloudSignCreate) {

            PacketInCloudSignCreate packetInCloudSignCreate = (PacketInCloudSignCreate)packet;
            CloudSign sign = packetInCloudSignCreate.getCloudSign();

            CloudSign get = this.cloudDriver.getInstance(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
            if (get == null) {
                this.cloudDriver.getInstance(SignService.class).getCloudSigns().add(sign);
                this.cloudDriver.getInstance(SignService.class).save();
                this.cloudDriver.reload();
            }

        } else if (packet instanceof PacketInCloudSignDelete) {

            PacketInCloudSignDelete packetInCloudSignDelete = (PacketInCloudSignDelete)packet;
            CloudSign sign = packetInCloudSignDelete.getCloudSign();

            CloudSign get = this.cloudDriver.getInstance(SignService.class).getCloudSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
            if (get != null) {
                this.cloudDriver.getInstance(SignService.class).getCloudSigns().remove(get);
                this.cloudDriver.getInstance(SignService.class).saveAndReload();
                this.cloudDriver.reload();
            }
        }

    }
}
