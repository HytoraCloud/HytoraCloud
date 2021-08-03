package de.lystx.hytoracloud.driver.service.util;


import de.lystx.hytoracloud.driver.service.ServiceInfo;

public interface IBukkit {

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

    /**
     * Checks if the ChatSystem is activated
     *
     * @return boolean
     */
    boolean shouldUseChat();

    /**
     * Enables the chat System
     */
    void enableChatSystem();

    /**
     * Disables the chat System
     */
    void disableChatSystem();

    /**
     * Checks if the nametag System is activated
     *
     * @return boolean
     */
    boolean shouldUseNameTags();

    /**
     * Enables the nametag System
     */
    void enableNameTags();

    /**
     * Disables the nametag System
     */
    void disableNameTags();

    /**
     * The chat format if chat is enabled
     *
     * @return format
     */
    String getChatFormat();

    /**
     * Sets the chat format
     *
     * @param format the format
     */
    void setChatFormat(String format);

    /**
     * Updates the motd
     *
     * @param motd the motd
     */
    void updateMotd(String motd);

    /**
     * Updates the maxPlayers amount
     *
     * @param maxPlayers the maxPlayers
     */
    void updateMaxPlayers(int maxPlayers);

    /**
     * Updates all values
     *
     * @param serviceInfo the info
     */
    void updateInfo(ServiceInfo serviceInfo);
}
