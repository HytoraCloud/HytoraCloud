package de.lystx.hytoracloud.launcher.receiver;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.elements.packets.receiver.PacketReceiverShutdown;
import io.thunder.Thunder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.launcher.receiver.booting.ReceiverBootingSetupDone;
import de.lystx.hytoracloud.launcher.receiver.booting.ReceiverBootingSetupNotDone;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.elements.packet.HytoraPacket;


@Getter @Setter
public class Receiver extends CloudProcess {

    @Getter
    private static Receiver instance;

    private HytoraClient cloudClient;

    /**
     * Boots up the Receiver
     */
    public Receiver() {
        super(CloudType.RECEIVER);
        instance = this;

        if (this.getInstance(ConfigService.class).getReceiverInfo().isEstablished()) {
            new ReceiverBootingSetupDone(this);
        } else {
            new ReceiverBootingSetupNotDone(this);
        }
     }


    @Override
    public void reload() {
    }

    @Override
    public void shutdown() {
        this.sendPacket(new PacketReceiverShutdown(this.getInstance(ConfigService.class).getReceiverInfo()));
        super.shutdown();
    }

    @Override
    public void sendPacket(HytoraPacket packet) {
        this.cloudClient.sendPacket(packet);
    }

}
