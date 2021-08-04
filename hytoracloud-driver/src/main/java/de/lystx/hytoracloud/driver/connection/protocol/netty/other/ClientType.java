package de.lystx.hytoracloud.driver.connection.protocol.netty.other;


public enum ClientType {

    /**
     * Don't know the client type
     */
    UNKNOWN,

    /**
     * A server instance (spigot/bukkit, ...)
     */
    SPIGOT,

    /**
     * A proxy instance (bungee, ...)
     */
    PROXY,

    /**
     * A cloud instance who manages server tasks
     */
    CLOUD,

    /**
     * Includes webinterface and other programs which displays
     * only the information
     */
    INTERFACE,

    /**
     * Any other instance (maybe webserver?)
     */
    CUSTOM;

}
