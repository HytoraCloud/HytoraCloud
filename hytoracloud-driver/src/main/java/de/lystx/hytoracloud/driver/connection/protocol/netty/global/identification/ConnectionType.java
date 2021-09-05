package de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification;


public enum ConnectionType {

    /**
     * Don't know the client type
     */
    UNKNOWN,

    /**
     * A bridge instance (bungee, spigot...)
     */
    CLOUD_BRIDGE,

    /**
     * A cloud instance who manages server tasks
     */
    CLOUD_INSTANCE,

    /**
     * A cloud instance who manages server tasks
     */
    CLOUD_RECEIVER,

    /**
     * This is just a random socket-server instance
     */
    JUST_SERVER,

    /**
     * This is just a random socket-client instance
     */
    JUST_CLIENT,

}
