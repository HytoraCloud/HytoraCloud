package de.lystx.hytoracloud.driver.service;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.interfaces.ObjectPool;
import de.lystx.hytoracloud.driver.utils.interfaces.Requestable;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;

import java.util.List;

public interface IServiceManager extends ObjectPool<IService> {

    /**
     * The {@link ServerEnvironment} of the current process
     * Comparable with {@link CloudDriver#getDriverType()}
     * to identify the process and determine if allowed
     *
     * @return type
     */
    ServerEnvironment getCurrentEnvironment();

    /**
     * Gets a free id for a {@link IService}
     *
     * @param group the group
     * @return id
     */
    int getFreeId(IServiceGroup group);

    /**
     * Gets a free port for a {@link IService}
     *
     * @param group the group
     * @return id
     */
    int getFreePort(IServiceGroup group);

    /**
     * Updates a {@link IService} in every cache
     *
     * @param service the service to update
     */
    void updateService(IService service);

    /**
     * Starts a {@link IService}
     *
     * @param service the service
     */
    void startService(IService service);

    /**
     * Starts a {@link IService} from a {@link IServiceGroup}
     *
     * @param serviceGroup the group
     */
    void startService(IServiceGroup serviceGroup);

    /**
     * Starts a random {@link IService} from a {@link IServiceGroup} with properties as {@link PropertyObject}
     *
     * @param serviceGroup the group
     * @param properties the properties
     */
    void startService(IServiceGroup serviceGroup, PropertyObject properties);

    /**
     * Stops a {@link IService}
     *
     * @param service the service to stop
     */
    void stopService(IService service);

    /**
     * Stops a {@link IService} but does not
     * start a new services if the minimum services
     * are lower than the online services of the group
     *
     * @param service the service
     */
    void stopServiceForcibly(IService service);

    /**
     * Stops all services
     */
    void shutdownAll(Runnable runnable);

    /**
     * Stops all {@link IService}s from a {@link IServiceGroup}
     *
     * @param serviceGroup the group
     */
    void shutdownAll(IServiceGroup serviceGroup);

    /**
     * Gets a list of all {@link IService}s from a {@link IServiceGroup}
     *
     * @param serviceGroup the group
     * @return list of services
     */
    List<IService> getCachedObjects(IServiceGroup serviceGroup);

    /**
     * Gets a list of all online {@link IService}s that match a given {@link ServerEnvironment}
     *
     * @param serviceType the type of services
     * @return list of services
     */
    List<IService> getCachedObjects(ServerEnvironment serviceType);

    /**
     * Gets a list of all online {@link IService}s if the request returns true
     *
     * @param request the request
     * @return list of services
     */
    List<IService> getCachedObjects(Requestable<IService> request);

    /**
     * Gets a list of all online {@link IService}s that match a given {@link ServiceState}
     *
     * @param serviceState the state of services
     * @return list of services
     */
    List<IService> getCachedObjects(ServiceState serviceState);

    /**
     * Returns the {@link IService} the driver
     * is currently running on
     *
     * @return service or null if not bridge
     */
    IService getThisService();

    /**
     * Updates all {@link IService}s
     * and sets the given {@link IServiceGroup} if needed
     *
     * @param groups the groups
     */
    void sync(List<IServiceGroup> groups);

    /**
     * Gets a list of all online {@link IService}s that are LobbyServers
     *
     * @return list of services
     */
    List<IService> getLobbies();

    /**
     * Registers a {@link IService}
     * if its not already registered
     *
     * @param service the service
     */
    void registerService(IService service);

    /**
     * Unregisters a {@link IService} from cache
     *
     * @param service the service
     */
    void unregisterService(IService service);


}
