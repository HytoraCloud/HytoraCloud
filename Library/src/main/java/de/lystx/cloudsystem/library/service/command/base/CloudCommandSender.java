package de.lystx.cloudsystem.library.service.command.base;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;

import java.util.UUID;

public interface CloudCommandSender {

    /**
     * Returns name of sender
     * @return
     */
    String getName();

    /**
     * Returns UUID of sender
     * @return
     */
    UUID getUniqueId();

    /**
     * Checks if has permission
     * @param permission
     * @return
     */
    boolean hasPermission(String permission);

    /**
     * Kicks for reason
     * @param reason
     */
    void kick(String reason);

    /**
     * Sends to server
     * @param server
     */
    void connect(String server);

    /**
     * Fallbacks
     */
    void fallback();

    /**
     * Updates data
     */
    void update();

    /**
     * Sends message
     * @param message
     */
    void sendMessage(String message);

    /**
     * Sends component
     * @param cloudComponent
     */
    void sendComponent(CloudComponent cloudComponent);

    /**
     * Message with prefix
     * @param prefix
     * @param message
     */
    void sendMessage(String prefix, String message);
}
