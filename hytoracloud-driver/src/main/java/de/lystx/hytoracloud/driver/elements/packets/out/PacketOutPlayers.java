package de.lystx.hytoracloud.driver.elements.packets.out;

import de.lystx.hytoracloud.driver.service.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor @Getter
public class PacketOutPlayers extends HytoraPacket {

    private List<CloudPlayer> cloudPlayers;

    public PacketOutPlayers(ICloudPlayerManager playerManager) {
        this.cloudPlayers = playerManager.getOnlinePlayers();
    }


    @Override
    public void write(Component component) {
        component.put("ps", cloudPlayers);
    }

    @Override
    public void read(Component component) {
        cloudPlayers = (List<CloudPlayer>) component.get("ps");
    }
}
