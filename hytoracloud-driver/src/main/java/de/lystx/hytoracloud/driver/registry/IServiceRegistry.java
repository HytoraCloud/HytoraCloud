package de.lystx.hytoracloud.driver.registry;

import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;

import java.util.List;

/**
 * This class is used to manage all {@link ICloudService}s
 */
public interface IServiceRegistry {

    /**
     * Registers a {@link ICloudService}
     *
     * @param iCloudService  the cloudService to register
     * @return the current instance of this registry
     */
    IServiceRegistry registerService(ICloudService iCloudService);

    /**
     * This unregisters a {@link ICloudService}
     *
     * @param iCloudService the service to unregister
     * @return current instance of this registry
     */
    IServiceRegistry unregisterService(ICloudService iCloudService);

    /**
     * This Method iterates through all registered
     * {@link ICloudService}s and checks if the given class
     * matches the parameter class
     *
     * @param tClass the class to get the service of
     * @return Service searched by class
     */
    <T extends ICloudService> T getInstance(Class<T> tClass);

    /**
     * Unregisters all {@link ICloudService}s
     */
    void unregisterAll();

    /**
     * Gets the list of registered {@link ICloudService}s
     *
     * @return list of services
     */
    List<ICloudService> getRegisteredServices();

    /**
     * Gets a list of all {@link ICloudService}s that are forbidden
     * to access (maybe your not on the right {@link CloudType})
     *
     * @return list with classes
     */
    List<Class<? extends ICloudService>> getDeniedToAccessServices();

    /**
     * Denies to access a {@link ICloudService}
     *
     * @param _class the class of the service
     */
    void denyService(Class<? extends ICloudService> _class);
}
