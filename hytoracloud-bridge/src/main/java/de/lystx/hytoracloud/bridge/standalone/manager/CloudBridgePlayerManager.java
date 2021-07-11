package de.lystx.hytoracloud.bridge.standalone.manager;

import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerInformation;

import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


@Setter @Getter
public class CloudBridgePlayerManager implements ICloudPlayerManager {

    private List<CloudPlayer> onlinePlayers;

    public CloudBridgePlayerManager() {
        this.onlinePlayers = new LinkedList<>();
    }

    /**
     * Returns all {@link CloudPlayer}s from a
     * ServiceGroup by Name
     * @param serviceGroup the group
     * @return
     */
    public List<CloudPlayer> getPlayersOnGroup(ServiceGroup serviceGroup) {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer cloudPlayer : this.onlinePlayers) {
            if (cloudPlayer.getService() == null) {
                continue;
            }
            if (cloudPlayer.getService().getServiceGroup().getName().equalsIgnoreCase(serviceGroup.getName())) {
                list.add(cloudPlayer);
            }
        }
        return list;
    }

    /**
     * Returns {@link CloudPlayer}s on a {@link Service}
     * @param service the service
     * @return
     */
    public List<CloudPlayer> getPlayersOnServer(Service service) {
       List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer cloudPlayer : this.onlinePlayers) {
            if (cloudPlayer.getService() == null) {
                continue;
            }
            if (cloudPlayer.getService().getName().equalsIgnoreCase(service.getName())) {
                list.add(cloudPlayer);
            }
        }
        return list;
    }

    /**
     * Updates a {@link CloudPlayer}
     * @param player the player to update
     */
    public void update(CloudPlayer player) {
        if (player == null) {
            return;
        }
        CloudPlayer cloudPlayer = getCachedPlayer(player.getName());
        if (cloudPlayer == null) {
            this.onlinePlayers.add(player);

            for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
                networkHandler.onPlayerJoin(player);
            }

            return;
        }

        try {
            onlinePlayers.set(onlinePlayers.indexOf(cloudPlayer), player);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("[CloudAPI] Oops @" + player.getName() + "....");
            //Ignoring on Server change
        }

    }


    @Override
    public List<PlayerInformation> getOfflinePlayers() {
        return CloudDriver.getInstance().getPermissionPool().getCachedCloudPlayers();
    }

    @Override
    public PlayerInformation getOfflinePlayer(String name) {
        return getOfflinePlayers().stream().filter(cloudPlayerData -> cloudPlayerData.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public PlayerInformation getOfflinePlayer(UUID uniqueId) {
        return getOfflinePlayers().stream().filter(cloudPlayerData -> cloudPlayerData.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    /**
     * Returns a cached {@link CloudPlayer}
     * by Name
     * @param name
     * @return
     */
    public CloudPlayer getCachedPlayer(String name) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public CloudPlayer getPlayer(String name) {
        return null;
    }

    public CloudPlayer getPlayer(UUID uniqueId) {
        return null;
    }


    /**
     * Returns a cached {@link CloudPlayer}
     * by UUID
     * @param uuid
     * @return
     */
    public CloudPlayer getCachedPlayer(UUID uuid) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Returns {@link CloudPlayer} directly
     * from Cloud with {@link CloudDriver#getResponse(Packet)}}
     * by Name
     * x asynchronous x
     * @param name
     */
    public void getAsync(String name, Consumer<CloudPlayer> consumer) {

    }

    /**
     * Returns {@link CloudPlayer} directly
     * from Cloud with {@link CloudDriver#getResponse(Packet)}
     * by UUID
     * x asynchronous x
     * @param uuid
     */
    public void getAsync(UUID uuid, Consumer<CloudPlayer> consumer) {

    }

    @NotNull
    @Override
    public Iterator<CloudPlayer> iterator() {
        return this.getOnlinePlayers().iterator();
    }

    /**
     * Removes a player from the Cache
     * @param cloudPlayer
     */
    public void unregisterPlayer(CloudPlayer cloudPlayer) {
        if (this.getCachedPlayer(cloudPlayer.getName()) == null) {
            return;
        }
        this.onlinePlayers.remove(cloudPlayer);
    }

    /**
     * Registers a Player
     * @param cloudPlayer
     */
    public void registerPlayer(CloudPlayer cloudPlayer) {
        if (this.getCachedPlayer(cloudPlayer.getName()) == null) {

            this.onlinePlayers.add(cloudPlayer);


        }
    }
}
