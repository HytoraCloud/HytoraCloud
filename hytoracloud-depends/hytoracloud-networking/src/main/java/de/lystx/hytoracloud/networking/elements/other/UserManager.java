package de.lystx.hytoracloud.networking.elements.other;

import de.lystx.hytoracloud.networking.connection.NetworkConnection;
import de.lystx.hytoracloud.networking.connection.NetworkBridge;
import lombok.Getter;
import de.lystx.hytoracloud.networking.connection.server.NetworkServer;

import java.util.*;

@Getter
public class UserManager {

    /**
     * All connected users with the same name
     */
    private final Map<String, List<NetworkBridge>> connectedUsers;

    /**
     * The connection for this manager
     */
    private final NetworkConnection networkConnection;

    public UserManager(NetworkConnection networkConnection) {
        this.connectedUsers = new HashMap<>();
        this.networkConnection = networkConnection;
    }

    /**
     * Adds a user to this manager
     * if a client with the same name already exists,
     * two clients will exist in the list
     *
     * @param connectionBridge the client
     */
    public void registerUser(NetworkBridge connectionBridge) {
        List<NetworkBridge> bridges = new ArrayList<>(this.connectedUsers.getOrDefault(connectionBridge.getName(), new LinkedList<>()));

        if (bridges.isEmpty()) {
            this.connectedUsers.put(connectionBridge.getName(), Collections.singletonList(connectionBridge));
        } else {
            bridges.add(connectionBridge);
            this.connectedUsers.put(connectionBridge.getName(), bridges);
        }


    }

    /**
     * Removes a user from the manager
     *
     * @param bridge the client to remove
     */
    public void unregisterUser(NetworkBridge bridge) {

        if (this.networkConnection instanceof NetworkServer) {

            NetworkServer hytoraServer = (NetworkServer) networkConnection;
            hytoraServer.getAcceptedNames().remove(bridge.getName());

            if (!this.connectedUsers.containsKey(bridge.getName())) {
                return;
            }
            List<NetworkBridge> bridges = new ArrayList<>(this.connectedUsers.get(bridge.getName()));

            bridges.remove(bridge); //Removing bridge from the list of bridges with the same name

            //If list is empty its removed from the hash map
            if (bridges.isEmpty()) {
                this.connectedUsers.remove(bridge.getName());
            } else {
                this.connectedUsers.put(bridge.getName(), bridges);
            }
        }

    }

    /**
     * Disconnects all users
     */
    public void disconnect() {

        for (List<NetworkBridge> bridges : new ArrayList<>(this.connectedUsers.values())) {
            for (NetworkBridge bridge : new LinkedList<>(bridges)) {
                bridge.disconnect();
            }
        }
    }

    /**
     * Gets a user by name
     *
     * @param name the name
     * @return user or null if none found
     */
    public NetworkBridge getUser(String name) {
        try {
            return this.getUsers(name).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Allows to retrieve all the references of a logged in user
     *
     * @param name User name.
     * @return The list containing the active references of the searched user.
     */
    public List<NetworkBridge> getUsers(String name) {
        List<NetworkBridge> connectionBridges = new LinkedList<>();
        for (List<NetworkBridge> value : new ArrayList<>(this.connectedUsers.values())) {
            for (NetworkBridge bridge : value) {
                if (bridge.getName().equalsIgnoreCase(name)) {
                    connectionBridges.add(bridge);
                }
            }
        }
        return connectionBridges;
    }

    /**
     * Creates a {@link List} containing all connected users
     *
     * @return a list
     */
    public List<NetworkBridge> getConnectedUsers() {

        List<NetworkBridge> connections = new LinkedList<>();

        for (List<NetworkBridge> value : connectedUsers.values()) {
            connections.addAll(value);
        }

        return connections;
    }

    /**
     * Stops this manager
     */
    public void shutdown() {
        this.disconnect();
        this.connectedUsers.clear();
    }
}