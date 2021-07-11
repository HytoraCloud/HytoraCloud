package de.lystx.hytoracloud.driver.service.other;

import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;

public interface IBukkit {

    /**
     * Gets the ServiceState of the BukkitServer
     *
     * @return state of service
     */
    ServiceState getServiceState();

    /**
     * Sets the state of the BukkitServer
     *
     * @param serviceState the state to set
     */
    void setServiceState(ServiceState serviceState);

    /**
     * Gets the Motd of the BukkitServer
     *
     * @return motd as String
     */
    String getMotd();

    /**
     * Sets the motd of the BukkitServer
     *
     * @param motd the motd to set
     */
    void setMotd(String motd);

    /**
     * Gets the maximum amount of players
     *
     * @return maxPlayers as int
     */
    int getMaxPlayers();

    /**
     * Sets the maxPlayers-count of the BukkitServer
     *
     * @param players the amount
     */
    void setMaxPlayers(int players);

    /**
     * Updates the Server
     */
    void update();

    /**
     * Gets the Bukkit-Version (1.8 or 1.16 etc...)
     *
     * @return version string
     */
    String getVersion();

    /**
     * Sets the version of Bukkit
     * @param ver the version
     */
    void setVersion(String ver);

    /**
     * Checks if the version is above 1.8
     *
     * @return boolean
     */
    boolean isNewVersion();
}
