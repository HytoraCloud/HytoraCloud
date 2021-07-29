package de.lystx.hytoracloud.driver.cloudservices.cloud.server;

import de.lystx.hytoracloud.driver.commons.interfaces.IPool;
import de.lystx.hytoracloud.driver.commons.interfaces.Requestable;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;

import java.util.List;

public interface IServiceManager extends IPool<IService> {

    /**
     * Updates a {@link IServiceGroup}
     *
     * @param group the old group
     */
    void updateGroup(IServiceGroup group);

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
     * Gets a list of all online {@link IService}s that match a given {@link ServiceType}
     *
     * @param serviceType the type of services
     * @return list of services
     */
    List<IService> getCachedObjects(ServiceType serviceType);

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

    /**
     * Gets a {@link IServiceGroup} by its name
     *
     * @param name the name of the group
     * @return group or null if not found
     */
    IServiceGroup getServiceGroup(String name);

    /**
     * Gets a list of all {@link IServiceGroup}s
     *
     * @return list of groups
     */
    List<IServiceGroup> getCachedGroups();

}
