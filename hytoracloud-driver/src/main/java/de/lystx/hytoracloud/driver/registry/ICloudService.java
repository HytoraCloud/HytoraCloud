package de.lystx.hytoracloud.driver.registry;


import de.lystx.hytoracloud.driver.CloudDriver;

public interface ICloudService {

    /**
     * Gets the name of this Service
     *
     * @return name
     */
    default String getName() {
        return info().name();
    }

    /**
     * Gets the version of this Service
     *
     * @return version double
     */
    default double getVersion() {
        return info().version();
    }

    /**
     * Gets the description of this Service
     *
     * @return description in string array
     */
    default String[] getDescription() {
        return info().description();
    }

    default CloudDriver getDriver() {
        return CloudDriver.getInstance();
    }

    /**
     * Reloads this service
     */
    void reload();

    /**
     * Saves this service
     */
    void save();

    default void saveAndReload() {
        this.reload();
        this.save();
    }

    /**
     * Returns the {@link CloudServiceInfo}
     *
     * @return info or null if not set
     */
    default CloudServiceInfo info() {
        return getClass().isAnnotationPresent(CloudServiceInfo.class) ? getClass().getAnnotation(CloudServiceInfo.class) : null;
    }

}
