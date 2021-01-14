package de.lystx.cloudsystem.library.service.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutOnlinOnServices;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.utils.Document;

import java.io.File;
import java.util.*;

public class CloudPlayerService extends CloudService {

    private final List<CloudPlayer> cloudPlayers;
    private final File dir;
    private final Map<ServiceGroup, Map<Service, List<CloudPlayer>>> onlinePlayers;

    public CloudPlayerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.cloudPlayers = new LinkedList<>();
        this.onlinePlayers = new HashMap<>();
        this.dir = cloudLibrary.getService(FileService.class).getCloudPlayerDirectory();
    }

    public void registerPlayer(CloudPlayer cloudPlayer) {
        this.cloudPlayers.add(cloudPlayer);

        File file = new File(dir, cloudPlayer.getUuid() + ".json");
        if (!file.exists()) {
            Document document = new Document(file);
            document.appendAll(new CloudPlayerData(
                    cloudPlayer.getUuid(),
                    cloudPlayer.getName(),
                    "Player",
                    "Player",
                    "",
                    new LinkedList<>(),
                    cloudPlayer.getIpAddress(),
                    true));
            document.save();
        } else {
            Document document = new Document(file);
            CloudPlayerData cloudPlayerData = document.getObject(document.getJsonObject(), CloudPlayerData.class);
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName(), cloudPlayerData.getPermissionGroup(), cloudPlayerData.getTempPermissionGroup(), cloudPlayerData.getValidadilityTime(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart());
            this.setPlayerData(cloudPlayer.getUuid(), newData);
        }
    }

    public void setOnlinePlayerState(Service service, CloudPlayer player, boolean add) {


        Service safeService = this.getCloudLibrary().getService(ServerService.class).getService(service.getName());

        Map<Service, List<CloudPlayer>> players = this.onlinePlayers.get(safeService.getServiceGroup());
        if (players == null) {
            players = new HashMap<>();
        }
        List<CloudPlayer> cloudPlayers = players.get(safeService);
        if (cloudPlayers == null) {
            cloudPlayers = new LinkedList<>();
        }
        if (add) {
            cloudPlayers.add(this.getOnlinePlayer(player.getName()));
        } else {
            cloudPlayers.remove(this.getOnlinePlayer(player.getName()));
        }
        players.put(safeService, cloudPlayers);
        this.onlinePlayers.put(safeService.getServiceGroup(), players);
    }

    public void reloadOnlinePlayers() {
        //this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutOnlinOnServices(this.onlinePlayers));
    }

    public void clearGroup(String name) {
        ServiceGroup group;
        for (ServiceGroup serviceGroup : this.onlinePlayers.keySet()) {
            if (serviceGroup.getName().equalsIgnoreCase(name)) {
                group = serviceGroup;
                this.onlinePlayers.put(group, new HashMap<>());
            }
        }

    }



    public CloudPlayerData getPlayerData(UUID uuid) {
        File file = new File(dir, uuid + ".json");
        Document document = new Document(file);
        return document.getObject(document.getJsonObject(), CloudPlayerData.class);
    }

    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        File file = new File(dir, uuid + ".json");
        Document document = new Document(file);
        document.clear();
        document.appendAll(data);
        document.save();
    }

    public void removePlayer(CloudPlayer cloudPlayer) {
        this.cloudPlayers.remove(this.getOnlinePlayer(cloudPlayer.getName()));
    }

    public CloudPlayer getOnlinePlayer(String name) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getName().equalsIgnoreCase(name)) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public CloudPlayer getOnlinePlayer(UUID uuid) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getUuid().equals(uuid)) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public List<CloudPlayer> getOnlinePlayers() {
        return this.cloudPlayers;
    }

}

