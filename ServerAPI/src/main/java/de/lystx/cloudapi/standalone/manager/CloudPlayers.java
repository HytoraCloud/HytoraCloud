package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInGetLog;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.packets.result.player.ResultPacketCloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Value;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


@Setter
public class CloudPlayers implements Iterable<CloudPlayer> {

    private final CloudAPI cloudAPI;
    private List<CloudPlayer> cloudPlayers;

    public CloudPlayers(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.cloudPlayers = new LinkedList<>();
    }

    /**
     * Sends the log from a
     * {@link Service} to a {@link CloudPlayer}
     *
     * @param cloudPlayer
     * @param service
     */
    public void sendLog(CloudPlayer cloudPlayer, Service service) {
        new PacketInGetLog(service, cloudPlayer.getName()).unsafe().async().send(this.cloudAPI);
    }

    /**
     * Returns all {@link CloudPlayer}s from a
     * ServiceGroup by Name
     * @param group
     * @return
     */
    public List<CloudPlayer> getPlayersOnGroup(String group) {
       List<CloudPlayer> list = new LinkedList<>();
       for (CloudPlayer cp : this.cloudPlayers) {
           if (cp.getServerGroup().equalsIgnoreCase(group)) {
              list.add(cp);
           }
        }
        return list;
    }

    /**
     * Returns {@link CloudPlayer}s on a {@link Service}
     * @param server
     * @return
     */
    public List<CloudPlayer> getPlayersOnServer(String server) {
       List<CloudPlayer> list = new LinkedList<>();
       for (CloudPlayer cp : this.cloudPlayers) {
           if (cp.getServer().equalsIgnoreCase(server)) {
              list.add(cp);
           }
        }
        return list;
    }

    /**
     * Returns Integer of players on Group
     * @param groupName
     * @return
     */
    public int getOnGroup(String groupName) {
        return this.getPlayersOnGroup(groupName).size();
    }

    /**
     * Returns Intger of players on Group
     * @param serverName
     * @return
     */
    public int getOnServer(String serverName) {
         return this.getPlayersOnServer(serverName).size();
    }

    /**
     * Updates a {@link CloudPlayer}
     * @param newPlayer
     */
    public void update(CloudPlayer newPlayer) {
        CloudPlayer cloudPlayer = this.get(newPlayer.getName());
        this.cloudPlayers.set(this.cloudPlayers.indexOf(cloudPlayer), newPlayer);
    }

    /**
     * Returns a cached {@link CloudPlayer}
     * by Name
     * @param name
     * @return
     */
    public CloudPlayer get(String name) {
        return this.cloudPlayers.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns a cached {@link CloudPlayer}
     * by UUID
     * @param uuid
     * @return
     */
    public CloudPlayer get(UUID uuid) {
        return this.cloudPlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Returns {@link CloudPlayer} directly
     * from Cloud with {@link CloudAPI#sendQuery(ResultPacket)}
     * by Name
     * @param name
     * @return
     */
    public CloudPlayer getByQuery(String name) {
        return this.cloudAPI.sendQuery(new ResultPacketCloudPlayer(name)).getResult().getObject("cloudPlayer", CloudPlayer.class);
    }

    /**
     * Returns {@link CloudPlayer} directly
     * from Cloud with {@link CloudAPI#sendQuery(ResultPacket)}
     * by UUID
     * @param uuid
     * @return
     */
    public CloudPlayer getByQuery(UUID uuid) {
        return this.cloudAPI.sendQuery(new ResultPacketCloudPlayer(uuid)).getResult().getObject("cloudPlayer", CloudPlayer.class);
    }

    /**
     * Returns all {@link CloudPlayer}s
     * @return
     */
    public List<CloudPlayer> getAll() {
        return cloudPlayers;
    }

    @NotNull
    @Override
    public Iterator<CloudPlayer> iterator() {
        return this.getAll().iterator();
    }

    @Override
    public void forEach(Consumer<? super CloudPlayer> action) {
        for (CloudPlayer cloudPlayer : this.getAll()) {
            action.accept(cloudPlayer);
        }
    }

    @Override
    public Spliterator<CloudPlayer> spliterator() {
        return this.getAll().spliterator();
    }
}
