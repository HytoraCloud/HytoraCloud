package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInGetLog;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.result.Result;
import de.lystx.cloudsystem.library.result.packets.ResultPacketCloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
import de.lystx.cloudsystem.library.service.util.Value;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;


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

    public int getOnGroup(String groupName) {
        int count = 0;
        try {
            for (Service service : this.cloudAPI.getNetwork().getServices(this.cloudAPI.getNetwork().getServiceGroup(groupName))) {
                count += this.getOnServer(service.getName());
            }
        } catch (NullPointerException e) {}
        return count;
    }

    public int getOnServer(String serverName) {
        try {
            Service service = this.cloudAPI.getNetwork().getService(serverName);
            ServerPinger pinger = new ServerPinger();
            try {
                pinger.pingServer(service.getHost(), service.getPort(), 20);
                return pinger.getPlayers();
            } catch (IOException e) {
                return 0;
            }
        } catch (NullPointerException e) {
            return 0;
        }
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
