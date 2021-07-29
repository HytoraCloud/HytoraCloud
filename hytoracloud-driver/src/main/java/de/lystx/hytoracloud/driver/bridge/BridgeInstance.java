package de.lystx.hytoracloud.driver.bridge;

import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;

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
    void sendTabList(UUID uniqueId, ChatComponent header, ChatComponent footer);

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
     * Stops this instance
     */
    void shutdown();

    /**
     * The type of the bridge instance
     *
     * @return type
     */
    ServiceType type();

    default Map<String, Object> loadExtras() {
        return new HashMap<>();
    }
}
