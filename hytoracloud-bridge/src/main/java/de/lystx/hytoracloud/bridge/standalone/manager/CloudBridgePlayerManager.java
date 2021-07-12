package de.lystx.hytoracloud.bridge.standalone.manager;

import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


@Setter @Getter
public class CloudBridgePlayerManager implements ICloudPlayerManager {

    private List<ICloudPlayer> onlinePlayers;

    public CloudBridgePlayerManager() {
        this.onlinePlayers = new LinkedList<>();
    }

    /**
     * Returns all {@link ICloudPlayer}s from a
     * ServiceGroup by Name
     * @param IServiceGroup the group
     * @return
     */
    public List<ICloudPlayer> getPlayersOnGroup(IServiceGroup IServiceGroup) {
        List<ICloudPlayer> list = new LinkedList<>();
        for (ICloudPlayer ICloudPlayer : this.onlinePlayers) {
            if (ICloudPlayer.getService() == null) {
                continue;
            }
            if (ICloudPlayer.getService().getGroup().getName().equalsIgnoreCase(IServiceGroup.getName())) {
                list.add(ICloudPlayer);
            }
        }
        return list;
    }

    /**
     * Returns {@link ICloudPlayer}s on a {@link IService}
     * @param IService the service
     * @return
     */
    public List<ICloudPlayer> getPlayersOnServer(IService IService) {
       List<ICloudPlayer> list = new LinkedList<>();
        for (ICloudPlayer ICloudPlayer : this.onlinePlayers) {
            if (ICloudPlayer.getService() == null) {
                continue;
            }
            if (ICloudPlayer.getService().getName().equalsIgnoreCase(IService.getName())) {
                list.add(ICloudPlayer);
            }
        }
        return list;
    }

    /**
     * Updates a {@link ICloudPlayer}
     * @param player the player to update
     */
    public void update(ICloudPlayer player) {
        if (player == null) {
            return;
        }
        ICloudPlayer ICloudPlayer = getCachedPlayer(player.getName());
        if (ICloudPlayer == null) {
            this.onlinePlayers.add(player);

            for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
                networkHandler.onPlayerJoin(player);
            }

            return;
        }

        try {
            onlinePlayers.set(onlinePlayers.indexOf(ICloudPlayer), player);
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
     * Returns a cached {@link ICloudPlayer}
     * by Name
     * @param name
     * @return
     */
    public ICloudPlayer getCachedPlayer(String name) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public ICloudPlayer getPlayer(String name) {
        return null;
    }

    public ICloudPlayer getPlayer(UUID uniqueId) {
        return null;
    }


    /**
     * Returns a cached {@link ICloudPlayer}
     * by UUID
     * @param uuid
     * @return
     */
    public ICloudPlayer getCachedPlayer(UUID uuid) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Returns {@link ICloudPlayer} directly
     * from Cloud with {@link CloudDriver#getResponse(Packet)}}
     * by Name
     * x asynchronous x
     * @param name
     */
    public void getAsync(String name, Consumer<ICloudPlayer> consumer) {

    }

    /**
     * Returns {@link ICloudPlayer} directly
     * from Cloud with {@link CloudDriver#getResponse(Packet)}
     * by UUID
     * x asynchronous x
     * @param uuid
     */
    public void getAsync(UUID uuid, Consumer<ICloudPlayer> consumer) {

    }

    @NotNull
    @Override
    public Iterator<ICloudPlayer> iterator() {
        return this.getOnlinePlayers().iterator();
    }

    /**
     * Removes a player from the Cache
     * @param ICloudPlayer
     */
    public void unregisterPlayer(ICloudPlayer ICloudPlayer) {
        if (this.getCachedPlayer(ICloudPlayer.getName()) == null) {
            return;
        }
        this.onlinePlayers.remove(ICloudPlayer);
    }

    /**
     * Registers a Player
     * @param ICloudPlayer
     */
    public void registerPlayer(ICloudPlayer ICloudPlayer) {
        if (this.getCachedPlayer(ICloudPlayer.getName()) == null) {

            this.onlinePlayers.add(ICloudPlayer);


        }
    }
}
