package de.lystx.hytoracloud.driver.elements.world;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class MinecraftWorld {

    /**
     * The name of this world
     */
    private final String name;

    /**
     * The uuid of this world
     */
    private final UUID uniqueId;

    /**
     * Gets all {@link CloudPlayer}s from this world
     * This is very laggy and not recommended because it works
     * with Query and {@link io.thunder.packet.impl.response.IResponse}
     * thats getting send between client and server
     *
     * @return list of cloudPlayers on this world
     */
    public List<CloudPlayer> getPlayers() {
        List<CloudPlayer> onlinePlayers = CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers();
        onlinePlayers.removeIf(cloudPlayer -> !cloudPlayer.getWorld().get().getName().equalsIgnoreCase(this.name));
        return onlinePlayers;
    }
}
