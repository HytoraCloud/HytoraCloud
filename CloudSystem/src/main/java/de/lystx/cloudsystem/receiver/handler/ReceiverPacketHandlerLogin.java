package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutVerifyConnection;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLoginResult;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.util.Decision;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReceiverPacketHandlerLogin {

    private final Receiver receiver;


    @PacketHandler
    public void handleVerify(PacketPlayOutVerifyConnection packet) {

    }

    @PacketHandler
    public void handleLogin(PacketReceiverLoginResult packet) {
        if (packet.getReceiverInfo().getName().equalsIgnoreCase(this.receiver.getService(ConfigService.class).getReceiverInfo().getName())) {
            if (packet.getDecision() == null) {
                this.receiver.getConsole().getLogger().sendMessage("NETWORK", "§cThere is already a Receiver with the name §e" + packet.getReceiverInfo().getName() + " §cconnected to the CloudSystem!");
            } else if (packet.getDecision().equals(Decision.TRUE)) {
                this.receiver.getConsole().getLogger().sendMessage("NETWORK", "§aSuccessfully connected to CloudSystem with right key");
                FileService fs = this.receiver.getService(FileService.class);
                this.receiver.cloudServices.add(new ServerService(this.receiver, "Services", CloudServiceType.NETWORK, packet.getServiceGroups()));
            } else if (packet.getDecision().equals(Decision.FALSE)) {
                this.receiver.getConsole().getLogger().sendMessage("NETWORK", "§cThe provided §ekey §cwas §ewrong §cconnection refused!");
            } else if (packet.getDecision().equals(Decision.MAYBE)) {
                this.receiver.getConsole().getLogger().sendMessage("NETWORK", "§cThe CloudSystem you tried to connect to, does not allow Receivers to connect. Enable it in the §econfig.json§c!");
            }
        }
    }
}
