package de.lystx.hytoracloud.driver.service.global.main;


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

    /**
     * Gets the type of this Service
     *
     * @return type of service
     */
    default CloudServiceType getType() {
        return info().type();
    }

    default CloudDriver getDriver() {
        return CloudDriver.getInstance();
    }

    /**
     * Returns the {@link ICloudServiceInfo}
     *
     * @return info or null if not set
     */
    default ICloudServiceInfo info() {
        return getClass().isAnnotationPresent(ICloudServiceInfo.class) ? getClass().getAnnotation(ICloudServiceInfo.class) : null;
    }

}
