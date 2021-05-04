package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInUpdateServiceGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.Cloud;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ServiceGroup implements Serializable, Objectable<ServiceGroup> {

    private final UUID uniqueId;
    private String name;
    private String template;
    private final ServiceType serviceType;
    private String receiver;
    private int maxServer;
    private int minServer;
    private int maxRam;
    private int minRam;
    private int maxPlayers;
    private int newServerPercent;
    private boolean maintenance;
    private boolean lobby;
    private boolean dynamic;
    private SerializableDocument values;

    public ServiceGroup(UUID uniqueId, String name, String template, ServiceType serviceType, String receiver, int maxServer, int minServer, int maxRam, int minRam, int maxPlayers, int newServerPercent, boolean maintenance, boolean lobby, boolean dynamic) {
        this(uniqueId, name, template, serviceType, receiver, maxServer, minServer, maxRam, minRam, maxPlayers, newServerPercent, maintenance, lobby, dynamic, new SerializableDocument());
    }

    /**
     * Updates the {@link ServiceGroup} on all
     * CloudInstances and syncs it's values all
     * over the CloudNetwork
     */
    public void update() {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketInUpdateServiceGroup(this));
    }

    /**
     * Returns the {@link CloudPlayer}s on this
     * ServiceGroup (for example "Lobby")
     *
     * @return List with CloudPlayers on this Group
     */
    public List<CloudPlayer> getOnlinePlayers() {
        return new LinkedList<>(Cloud.getInstance().getCloudPlayerFilter().find(cloudPlayer -> cloudPlayer.getService().getServiceGroup().getName().equalsIgnoreCase(this.getName())).findAll());
    }

    /**
     * Returns a List with all the
     * Services online on this group
     * @return
     */
    public List<Service> getServices() {
        return new LinkedList<>(Cloud.getInstance().getServiceFilter().find(service -> service.getServiceGroup().getName().equalsIgnoreCase(this.name)).findAll());
    }

    public String toString() {
        return this.name;
    }

}
