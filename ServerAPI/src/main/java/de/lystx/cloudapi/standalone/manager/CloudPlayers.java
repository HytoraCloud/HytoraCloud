package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Setter
public class CloudPlayers {

    private final CloudAPI cloudAPI;
    private List<CloudPlayer> cloudPlayers;
    private Map<ServiceGroup, Map<Service, List<CloudPlayer>>> onlinePlayers;

    public CloudPlayers(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.onlinePlayers = new HashMap<>();
        this.cloudPlayers = new LinkedList<>();
    }



    public int getOnGroup(String groupName) {
        int count = 0;
        for (Service service : this.cloudAPI.getNetwork().getServices(this.cloudAPI.getNetwork().getServiceGroup(groupName))) {
            count += this.getOnServer(service.getName());
        }
        return count;
    }

    public int getOnServer(String serverName) {
        Service service = this.cloudAPI.getNetwork().getService(serverName);
        ServerPinger pinger = new ServerPinger();
        try {
            pinger.pingServer(service.getHost(), service.getPort(), 20);
            return pinger.getPlayers();
        } catch (IOException e) {
            return 0;
        }
    }

    /*public List<CloudPlayer> getOnGroup(String groupName) {
        List<CloudPlayer> list = new LinkedList<>();
        for (Service service : this.cloudAPI.getNetwork().getServices(this.cloudAPI.getNetwork().getServiceGroup(groupName))) {
            list.addAll(this.getOnServer(service.getName()));
        }
        return list;
    }

    public List<CloudPlayer> getOnServer(String serverName) {
        Service service = this.getService(serverName);
        if (service == null) {
            this.cloudAPI.messageCloud(this.cloudAPI.getService().getName(), "§cTried getting onlinePlayers on §e" + serverName + " §cbut server wasn't found!");
            return null;
        }
        Map<Service, List<CloudPlayer>> players = this.onlinePlayers.get(this.getGroup(service.getServiceGroup().getName()));
        return players.get(service);
    }

    private ServiceGroup getGroup(String name) {
        for (ServiceGroup serviceGroup : this.onlinePlayers.keySet()) {
            if (serviceGroup.getName().equalsIgnoreCase(name)) {
                return serviceGroup;
            }
        }
        return null;
    }

    private Service getService(String name) {
        for (Map<Service, List<CloudPlayer>> value : this.onlinePlayers.values()) {
            for (Service service : value.keySet()) {
                if (service.getName().equalsIgnoreCase(name)) {
                    return service;
                }
            }
        }
        return null;
    }*/

    public CloudPlayer get(String name) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getName().equalsIgnoreCase(name)) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public List<CloudPlayer> getAll() {
        return cloudPlayers;
    }
}
