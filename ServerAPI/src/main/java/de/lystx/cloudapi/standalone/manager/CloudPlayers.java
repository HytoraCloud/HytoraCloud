package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInGetLog;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.packets.result.player.ResultPacketCloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Value;
import lombok.Setter;

import java.util.*;


@Setter
public class CloudPlayers {

    private final CloudAPI cloudAPI;
    private List<CloudPlayer> cloudPlayers;

    public CloudPlayers(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.cloudPlayers = new LinkedList<>();
    }


    public void sendLog(CloudPlayer cloudPlayer, Service service) {
        this.cloudAPI.sendPacket(new PacketPlayInGetLog(service, cloudPlayer.getName()));
    }

    public List<CloudPlayer> getPlayersOnGroup(String group) {
       List<CloudPlayer> list = new LinkedList<>();
       for (CloudPlayer cp : this.cloudPlayers) {
           if (cp.getGroup().equalsIgnoreCase(group)) {
              list.add(cp);
           }
        }
        return list;
    }

    public List<CloudPlayer> getPlayersOnServer(String server) {
       List<CloudPlayer> list = new LinkedList<>();
       for (CloudPlayer cp : this.cloudPlayers) {
           if (cp.getServer().equalsIgnoreCase(server)) {
              list.add(cp);
           }
        }
        return list;
    }

    public int getOnGroup(String groupName) {
        return this.getPlayersOnGroup(groupName).size();
    }

    public int getOnServer(String serverName) {
         return this.getPlayersOnServer(serverName).size();
    }

    public void update(String name, CloudPlayer newPlayer) {
        CloudPlayer cloudPlayer = this.get(name);
        this.cloudPlayers.set(this.cloudPlayers.indexOf(cloudPlayer), newPlayer);
    }

    public CloudPlayer get(String name) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getName().equalsIgnoreCase(name)) {
                return cloudPlayer;
            }
        }
        return null;
    }


    public CloudPlayer get(UUID uuid) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getUuid() == uuid) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public CloudPlayer getByQuery(String name) {
        Value<CloudPlayer> value = new Value<>(null);
        this.cloudAPI.sendQuery(new ResultPacketCloudPlayer(name)).onResultSet(result -> {
            value.set(result.getResultAs(CloudPlayer.class));
        });
        return value.get();
    }

    public CloudPlayer getByQuery(UUID uuid) {
        Value<CloudPlayer> value = new Value<>(null);
        this.cloudAPI.sendQuery(new ResultPacketCloudPlayer(uuid)).onResultSet(result -> {
            value.set(result.getResultAs(CloudPlayer.class));
        });
        return value.get();
    }

    public List<CloudPlayer> getAll() {
        return cloudPlayers;
    }
}
