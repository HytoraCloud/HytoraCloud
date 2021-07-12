package de.lystx.hytoracloud.driver.cloudservices.cloud.server;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;

import java.util.List;
import java.util.Map;

public interface IServiceManager {

    /**
     * Updates a {@link IServiceGroup}
     *
     * @param group the old group
     */
    void updateGroup(IServiceGroup group);

    /**
     * Updates a {@link IService} in every cache
     *
     * @param IService the service to update
     */
    void updateService(IService IService);

    /**
     * Starts {@link IService}s from a given list
     *
     * @param IServiceGroups the groups to start
     */
    void startServices(List<IServiceGroup> IServiceGroups);

    /**
     * Starts a {@link IService} from a {@link IServiceGroup} with properties as {@link JsonObject}
     *
     * @param IServiceGroup the group
     * @param IService the service
     * @param properties the properties
     */
    void startService(IServiceGroup IServiceGroup, IService IService, PropertyObject properties);

    /**
     * Starts a {@link IService} from a {@link IServiceGroup} with no properties as {@link PropertyObject}
     *
     * @param IServiceGroup the group
     * @param IService the service
     */
    void startService(IServiceGroup IServiceGroup, IService IService);

    /**
     * Starts a {@link IService} from a {@link IServiceGroup}
     *
     * @param IServiceGroup the group
     */
    void startService(IServiceGroup IServiceGroup);

    /**
     * Starts a random {@link IService} from a {@link IServiceGroup} with properties as {@link PropertyObject}
     *
     * @param IServiceGroup the group
     * @param properties the properties
     */
    void startService(IServiceGroup IServiceGroup, PropertyObject properties);

    /**
     * Stops a {@link IService}
     *
     * @param IService the service to stop
     */
    void stopService(IService IService);

    /**
     * Updates the cached {@link IService}s
     *
     * @param cachedServices the cached services and its groups
     */
    void setCachedServices(Map<IServiceGroup, List<IService>> cachedServices);

    /**
     * Stops all services
     */
    void stopServices();

    /**
     * Notifies about a {@link IService} thats stopping
     *
     * @param IService the stopping service
     */
    void notifyStop(IService IService);

    /**
     * Stops all {@link IService}s from a {@link IServiceGroup}
     *
     * @param IServiceGroup the group
     */
    void stopServices(IServiceGroup IServiceGroup);

    /**
     * Gets a list of all {@link IService}s from a {@link IServiceGroup}
     *
     * @param IServiceGroup the group
     * @return list of services
     */
    List<IService> getServices(IServiceGroup IServiceGroup);

    /**
     * Gets a list of all online {@link IService}s
     *
     * @return list of services
     */
    List<IService> getAllServices();

    /**
     * Gets a list of all online {@link IService}s that match a given {@link ServiceType}
     *
     * @param serviceType the type of services
     * @return list of services
     */
    List<IService> getAllServices(ServiceType serviceType);

    /**
     * Gets a list of all online {@link IService}s that are LobbyServers
     *
     * @return list of services
     */
    List<IService> getLobbies();

    /**
     * Gets a list of all online {@link IService}s that match a given {@link ServiceState}
     *
     * @param serviceState the state of services
     * @return list of services
     */
    List<IService> getAllServices(ServiceState serviceState);

    /**
     * Gets a {@link IService} by its name
     *
     * @param name the name
     * @return service or null if not found
     */
    IService getService(String name);

    /**
     * Registers a {@link IService}
     * if its not already registered
     *
     * @param service the service
     */
    void registerService(IService service);

    /**
     * Gets a {@link IService} as proxy by its port
     * to identify a Proxy you're on right now
     *
     * @param port the port of the connection
     * @return proxy or null
     */
    IService getProxy(Integer port);

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
    List<IServiceGroup> getServiceGroups();

    /**
     * Gets the cache of the {@link IService}s and {@link IServiceGroup}s
     *
     * @return map cache
     */
    Map<IServiceGroup, List<IService>> getCachedServices();

}
