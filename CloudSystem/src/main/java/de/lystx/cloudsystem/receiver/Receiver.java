package de.lystx.cloudsystem.receiver;

import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.elements.enums.CloudType;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverShutdown;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.receiver.booting.ReceiverBootingSetupDone;
import de.lystx.cloudsystem.receiver.booting.ReceiverBootingSetupNotDone;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Receiver extends CloudInstance {

    @Getter
    private static Receiver instance;

    /**
     * Boots up the Receiver
     */
    public Receiver() {
        super(CloudType.RECEIVER);
        instance = this;

        if (this.getService(ConfigService.class).getReceiverInfo().isEstablished()) {
            new ReceiverBootingSetupDone(this);
        } else {
            new ReceiverBootingSetupNotDone(this);
        }
     }


    @Override
    public void reload() {}

    @Override
    public void shutdown() {
        this.sendPacket(new PacketReceiverShutdown(this.getService(ConfigService.class).getReceiverInfo()));
        super.shutdown();
    }

    @Override
    public void sendPacket(Packet packet) {
        this.cloudClient.sendPacket(packet);
    }

}
