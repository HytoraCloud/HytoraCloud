package net.hytora.networking.elements.other;

import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.connection.HytoraConnectionBridge;
import lombok.Getter;
import net.hytora.networking.connection.server.HytoraServer;

import java.util.*;

@Getter
public class UserManager {

    /**
     * All connected users with the same name
     */
    private final Map<String, List<HytoraConnectionBridge>> connectedUsers;

    /**
     * The connection for this manager
     */
    private final HytoraConnection hytoraConnection;

    public UserManager(HytoraConnection hytoraConnection) {
        this.connectedUsers = new HashMap<>();
        this.hytoraConnection = hytoraConnection;
    }

    /**
     * Adds a user to this manager
     * if a client with the same name already exists,
     * two clients will exist in the list
     *
     * @param connectionBridge the client
     */
    public void registerUser(HytoraConnectionBridge connectionBridge) {
        List<HytoraConnectionBridge> bridges = new ArrayList<>(this.connectedUsers.getOrDefault(connectionBridge.getName(), new LinkedList<>()));

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
    public void unregisterUser(HytoraConnectionBridge bridge) {

        if (this.hytoraConnection instanceof HytoraServer) {

            HytoraServer hytoraServer = (HytoraServer)hytoraConnection;
            hytoraServer.getAcceptedNames().remove(bridge.getName());

            if (!this.connectedUsers.containsKey(bridge.getName())) {
                return;
            }
            List<HytoraConnectionBridge> bridges = new ArrayList<>(this.connectedUsers.get(bridge.getName()));

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

        for (List<HytoraConnectionBridge> bridges : new ArrayList<>(this.connectedUsers.values())) {
            for (HytoraConnectionBridge bridge : new LinkedList<>(bridges)) {
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
    public HytoraConnectionBridge getUser(String name) {
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
    public List<HytoraConnectionBridge> getUsers(String name) {
        return this.connectedUsers.getOrDefault(name, new LinkedList<>());
    }

    /**
     * Creates a {@link List} containing all connected users
     *
     * @return a list
     */
    public List<HytoraConnectionBridge> getConnectedUsers() {

        List<HytoraConnectionBridge> connections = new LinkedList<>();

        for (List<HytoraConnectionBridge> value : connectedUsers.values()) {
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