package de.lystx.hytoracloud.driver.service.bridge;

import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface BridgeInstance {

    /**
     * Bootstraps this instance
     */
    void bootstrap();

    /**
     * Stops this instance
     */
    void shutdown();

    /**
     * Executes a console command
     *
     * @param command the command
     */
    void flushCommand(String command);

    /**
     * Responds with a list of important
     * information on this instance
     * that might be important or
     * useful to see
     *
     * @return object
     */
    PropertyObject requestProperties();

    /**
     * Loads the current memory usage
     *
     * @return memory as long
     */
    default long loadMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
    }

    /**
     * Loads the ping of a player
     * @param playerUniqueId the uuid of player
     * @return ping as long
     */
    int getPing(UUID playerUniqueId);

    /**
     * The header and the footer
     *
     * @param uniqueId the uuid of the player
     * @param header header
     * @param footer footer
     */
    void sendTabList(UUID uniqueId, String header, String footer);

    /**
     * Sends a message to a player
     *
     * @param uniqueId the uuid of the player
     * @param message the message
     */
    void sendMessage(UUID uniqueId, ChatComponent message);

    /**
     * Loads the current TPS
     * (Ticks per second)
     *
     * @return tps formatted with color
     */
    default String loadTPS() {
        return "§c???";
    }

    /**
     * The type of the bridge instance
     *
     * @return type
     */
    ServerEnvironment type();

    default Map<String, Object> loadExtras() {
        return new HashMap<>();
    }
}
