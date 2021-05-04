package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.list.Filter;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInGetLog;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.packets.result.player.ResultPacketCloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.Cloud;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;


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
        cloudAPI.sendPacket(new PacketInGetLog(service, cloudPlayer.getName()));
    }

    /**
     * Returns all {@link CloudPlayer}s from a
     * ServiceGroup by Name
     * @param group
     * @return
     */
    public List<CloudPlayer> getPlayersOnGroup(String group) {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getService().getServiceGroup().getName().equalsIgnoreCase(group)) {
                list.add(cloudPlayer);
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
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getService().getName().equalsIgnoreCase(server)) {
                list.add(cloudPlayer);
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
        if (cloudPlayer == null) {
            cloudPlayers.add(newPlayer);
            return;
        }
        try {
            this.cloudPlayers.set(this.cloudPlayers.indexOf(cloudPlayer), newPlayer);
        } catch (IndexOutOfBoundsException ignored) {
            //Ignoring on Server change
        }
        Cloud.getInstance().setCloudPlayerFilter(new Filter<>(CloudAPI.getInstance().getCloudPlayers().getAll()));
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

    public CloudPlayer getQuery(String name) {
        return this.cloudAPI.sendQuery(new ResultPacketCloudPlayer(name)).getResult().getObject("cloudPlayer", CloudPlayer.class);
    }

    public CloudPlayer getQuery(UUID uniqueId) {
        return this.cloudAPI.sendQuery(new ResultPacketCloudPlayer(uniqueId)).getResult().getObject("cloudPlayer", CloudPlayer.class);
    }

    /**
     * Returns a Stream {@link CloudPlayer}
     * @param name
     * @return
     */
    public Stream<CloudPlayer> getAsStream(String name) {
        return this.cloudPlayers.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name));
    }

    /**
     * Returns a Stream {@link CloudPlayer}
     * @param uuid
     * @return
     */
    public Stream<CloudPlayer> getAsStream(UUID uuid) {
        return this.cloudPlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId().equals(uuid));
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
     * x asynchronous x
     * @param name
     */
    public void getAsync(String name, Consumer<CloudPlayer> consumer) {
        try {
            this.cloudAPI.sendQuery(
                    new ResultPacketCloudPlayer(name),
                    vsonObjectResult -> {
                        try {
                            consumer.accept(
                                    vsonObjectResult
                                            .getResult()
                                            .getObject("cloudPlayer", CloudPlayer.class)
                            );
                        } catch (NullPointerException e) {
                            consumer.accept(null);
                        }
                    }
            );
        } catch (NullPointerException e) {
            consumer.accept(null);
        }
    }

    /**
     * Returns {@link CloudPlayer} directly
     * from Cloud with {@link CloudAPI#sendQuery(ResultPacket)}
     * by UUID
     * x asynchronous x
     * @param uuid
     */
    public void getAsync(UUID uuid, Consumer<CloudPlayer> consumer) {
        try {
            this.cloudAPI.sendQuery(
                    new ResultPacketCloudPlayer(uuid),
                    vsonObjectResult -> {
                        try {
                            consumer.accept(
                                    vsonObjectResult
                                            .getResult()
                                            .getObject("cloudPlayer", CloudPlayer.class)
                            );
                        } catch (NullPointerException e) {
                            consumer.accept(null);
                        }
                    }
            );
        } catch (NullPointerException e) {
            consumer.accept(null);
        }
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
        this.getAll().forEach(action);
    }

    @Override
    public Spliterator<CloudPlayer> spliterator() {
        return this.getAll().spliterator();
    }

    /**
     * Removes a player from the Cache
     * @param cloudPlayer
     */
    public void remove(CloudPlayer cloudPlayer) {
        this.cloudPlayers.remove(cloudPlayer);
        Cloud.getInstance().setCloudPlayerFilter(new Filter<>(CloudAPI.getInstance().getCloudPlayers().getAll()));
    }

    public void add(CloudPlayer cloudPlayer) {
        this.cloudPlayers.add(cloudPlayer);
        Cloud.getInstance().setCloudPlayerFilter(new Filter<>(CloudAPI.getInstance().getCloudPlayers().getAll()));
    }
}
