package de.lystx.hytoracloud.driver.command.executor;

import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;

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
     * @deprecated you should use {@link ChatComponent}s
     * @param message the message to send
     */
    @Deprecated
    void sendMessage(String message);

    /**
     * Sends component
     *
     * @param chatComponent the component to send
     */
    void sendMessage(ChatComponent chatComponent);

    /**
     * Message with prefix
     *
     * @param prefix the prefix
     * @param message the message
     */
    void sendMessage(String prefix, String message);
}
