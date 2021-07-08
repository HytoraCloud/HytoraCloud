package net.hytora.networking.elements.other;

import net.hytora.networking.connection.HytoraConnectionBridge;
import lombok.Getter;

import java.util.*;

@Getter
public class UserManager {

    /**
     * All single connected users
     */
    private final Map<String, HytoraConnectionBridge> singleListedUsers;

    /**
     * All connected users with the same name
     */
    private final Map<String, List<HytoraConnectionBridge>> setListedUsers;

    public UserManager() {
        this.singleListedUsers = new HashMap<>();
        this.setListedUsers = new HashMap<>();
    }

    /**
     * Adds a user to this manager
     * if a client with the same name already exists,
     * two clients will exist in the list
     *
     * @param connectionBridge the client
     */
    public void registerUser(HytoraConnectionBridge connectionBridge) {
        HytoraConnectionBridge probably = this.singleListedUsers.get(connectionBridge.getName());
        if (probably == null) {
            this.singleListedUsers.put(connectionBridge.getName(), connectionBridge);
            return;
        }
        List<HytoraConnectionBridge> hytoraConnectionBridgeLinkedList = new LinkedList<>();
        hytoraConnectionBridgeLinkedList.add(probably);
        hytoraConnectionBridgeLinkedList.add(connectionBridge);
        this.setListedUsers.put(connectionBridge.getName(), hytoraConnectionBridgeLinkedList);
        this.singleListedUsers.remove(connectionBridge.getName());
    }

    /**
     * Removes a user from the manager
     *
     * @param connectionBridge the client to remove
     */
    public void unregisterUser(HytoraConnectionBridge connectionBridge) {
        if (connectionBridge.getName() == null) {
            return;
        }
        if (this.singleListedUsers.containsValue(connectionBridge)) {
            this.singleListedUsers.remove(connectionBridge.getName());
        } else if (setListedUsers.containsKey(connectionBridge.getName())) {
            this.setListedUsers.get(connectionBridge.getName()).remove(connectionBridge);
        }
    }

    /**
     * Disconnects all users
     */
    public void disconnect() {
        for (HytoraConnectionBridge bridge : this.singleListedUsers.values()) {
            bridge.disconnect();
        }
        for (List<HytoraConnectionBridge> bridges : this.setListedUsers.values()) {
            for (HytoraConnectionBridge bridge : bridges) {
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
        HytoraConnectionBridge probably;
        if ((probably = this.singleListedUsers.get(name)) != null) {
            return new LinkedList<>(Collections.singletonList(probably));
        } else {
            List<HytoraConnectionBridge> probably2;
            if ((probably2 = this.setListedUsers.get(name)) != null) {
                return probably2;
            }
        }
        return new LinkedList<>();
    }

    /**
     * Creates a {@link List} containing all connected users
     *
     * @return a list
     */
    public List<HytoraConnectionBridge> getConnectedUsers() {
        List<HytoraConnectionBridge> res = new LinkedList<>(singleListedUsers.values());
        this.setListedUsers.values().forEach(res::addAll);
        return res;
    }

    /**
     * Stops this manager
     */
    public void shutdown() {
        this.disconnect();
        this.singleListedUsers.clear();
        this.setListedUsers.clear();
    }
}