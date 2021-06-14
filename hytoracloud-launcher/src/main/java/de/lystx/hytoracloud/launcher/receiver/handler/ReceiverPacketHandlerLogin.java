package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.elements.packets.receiver.PacketReceiverLoginResult;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.DefaultServiceManager;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.config.ConfigService;

import de.lystx.hytoracloud.driver.enums.Decision;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReceiverPacketHandlerLogin implements PacketHandler {

    private final Receiver receiver;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketReceiverLoginResult) {
            PacketReceiverLoginResult packetReceiverLoginResult = (PacketReceiverLoginResult)packet;
            if (packetReceiverLoginResult.getReceiverInfo().getName().equalsIgnoreCase(this.receiver.getInstance(ConfigService.class).getReceiverInfo().getName())) {
                if (packetReceiverLoginResult.getDecision() == null) {
                    this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§cThere is already a Receiver with the name §e" + packetReceiverLoginResult.getReceiverInfo().getName() + " §cconnected to the CloudSystem!");
                } else if (packetReceiverLoginResult.getDecision().equals(Decision.TRUE)) {
                    this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§aSuccessfully connected to CloudSystem with right key");
                    this.receiver.getServiceRegistry().registerService(new DefaultServiceManager(packetReceiverLoginResult.getServiceGroups()));
                } else if (packetReceiverLoginResult.getDecision().equals(Decision.FALSE)) {
                    this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§cThe provided §ekey §cwas §ewrong §cconnection refused!");
                } else if (packetReceiverLoginResult.getDecision().equals(Decision.MAYBE)) {
                    this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§cThe CloudSystem you tried to connect to, does not allow Receivers to connect. Enable it in the §econfig.json§c!");
                }
            }
        }
    }
}
