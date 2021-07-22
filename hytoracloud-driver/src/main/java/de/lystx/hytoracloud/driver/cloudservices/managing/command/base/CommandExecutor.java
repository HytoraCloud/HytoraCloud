package de.lystx.hytoracloud.driver.cloudservices.managing.command.base;

import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;

public interface CommandExecutor extends Identifiable {

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
     * @param chatComponent the component to send
     */
    void sendComponent(ChatComponent chatComponent);

    /**
     * Message with prefix
     *
     * @param prefix the prefix
     * @param message the message
     */
    void sendMessage(String prefix, String message);
}
