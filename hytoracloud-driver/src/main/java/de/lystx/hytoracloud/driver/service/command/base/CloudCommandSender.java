package de.lystx.hytoracloud.driver.service.command.base;

import de.lystx.hytoracloud.driver.elements.chat.CloudComponent;
import de.lystx.hytoracloud.driver.elements.interfaces.Identifiable;

public interface CloudCommandSender extends Identifiable {

    /**
     * Checks if has permission
     *
     * @param permission the permission to check
     * @return boolean if the player has the permission
     */
    boolean hasPermission(String permission);

    /**
     * Sends message
     *
     * @param message the message to send
     */
    void sendMessage(Object message);

    /**
     * Sends component
     *
     * @param cloudComponent the component to send
     */
    void sendComponent(CloudComponent cloudComponent);

    /**
     * Message with prefix
     *
     * @param prefix the prefix
     * @param message the message
     */
    void sendMessage(String prefix, String message);
}
