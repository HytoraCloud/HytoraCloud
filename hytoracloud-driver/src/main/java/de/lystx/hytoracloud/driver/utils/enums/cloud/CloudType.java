package de.lystx.hytoracloud.driver.utils.enums.cloud;

public enum CloudType {

    /**
     * This is the "Wrapper"
     */
    RECEIVER,

    /**
     * This is the "Master"
     */
    CLOUDSYSTEM,

    /**
     * Type of module
     */
    MODULE,

    /**
     * The internal driver to manage
     */
    DRIVER,

    /**
     * The spigot or proxy bridge
     */
    BRIDGE,

    /**
     * The manager to manage
     */
    MANAGER,

    /**
     * None of the above
     */
    NONE;

    /**
     * Checks if the current type is
     * either cloudSystem or receiver (Master or Wrapper)
     *
     * @return boolean
     */
    public boolean isInstance() {
        return name().equals(RECEIVER.name()) || name().equalsIgnoreCase(CLOUDSYSTEM.name());
    }
}
