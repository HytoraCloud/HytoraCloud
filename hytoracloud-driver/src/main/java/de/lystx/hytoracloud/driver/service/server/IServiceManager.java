package de.lystx.hytoracloud.driver.service.server;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.enums.ServiceState;

import java.util.List;
import java.util.Map;

public interface IServiceManager {

    /**
     * Updates a {@link ServiceGroup}
     *
     * @param group the old group
     * @param newGroup the new group
     */
    void updateGroup(ServiceGroup group, ServiceGroup newGroup);

    /**
     * Updates a {@link Service} in every cache
     *
     * @param service the service to update
     */
    void updateService(Service service);

    /**
     * Starts {@link Service}s from a given list
     *
     * @param serviceGroups the groups to start
     */
    void startServices(List<ServiceGroup> serviceGroups);

    /**
     * Starts a {@link Service} from a {@link ServiceGroup} with properties as {@link JsonObject}
     *
     * @param serviceGroup the group
     * @param service the service
     * @param properties the properties
     */
    void startService(ServiceGroup serviceGroup, Service service, JsonObject properties);

    /**
     * Starts a {@link Service} from a {@link ServiceGroup} with no properties as {@link JsonObject}
     *
     * @param serviceGroup the group
     * @param service the service
     */
    void startService(ServiceGroup serviceGroup, Service service);

    /**
     * Starts a {@link Service} from a {@link ServiceGroup}
     *
     * @param serviceGroup the group
     */
    void startService(ServiceGroup serviceGroup);

    /**
     * Starts a random {@link Service} from a {@link ServiceGroup} with properties as {@link JsonObject}
     *
     * @param serviceGroup the group
     * @param properties the properties
     */
    void startService(ServiceGroup serviceGroup, JsonObject properties);

    /**
     * Stops a {@link Service}
     *
     * @param service the service to stop
     */
    void stopService(Service service);

    /**
     * Stops all services
     */
    void stopServices();

    /**
     * Notifies about a {@link Service} thats stopping
     *
     * @param service the stopping service
     */
    void notifyStop(Service service);

    /**
     * Stops all {@link Service}s from a {@link ServiceGroup}
     *
     * @param serviceGroup the group
     */
    void stopServices(ServiceGroup serviceGroup);

    /**
     * Gets a list of all {@link Service}s from a {@link ServiceGroup}
     *
     * @param serviceGroup the group
     * @return list of services
     */
    List<Service> getServices(ServiceGroup serviceGroup);

    /**
     * Gets a list of all online {@link Service}s
     *
     * @return list of services
     */
    List<Service> getAllServices();

    /**
     * Gets a list of all online {@link Service}s that match a given {@link ServiceType}
     *
     * @param serviceType the type of services
     * @return list of services
     */
    List<Service> getAllServices(ServiceType serviceType);

    /**
     * Gets a list of all online {@link Service}s that are LobbyServers
     *
     * @return list of services
     */
    List<Service> getLobbies();

    /**
     * Gets a list of all online {@link Service}s that match a given {@link ServiceState}
     *
     * @param serviceState the state of services
     * @return list of services
     */
    List<Service> getAllServices(ServiceState serviceState);

    /**
     * Gets a {@link Service} by its name
     *
     * @param name the name
     * @return service or null if not found
     */
    Service getService(String name);

    /**
     * Gets a {@link Service} as proxy by its port
     * to identify a Proxy you're on right now
     *
     * @param port the port of the connection
     * @return proxy or null
     */
    Service getProxy(Integer port);

    /**
     * Gets a {@link ServiceGroup} by its name
     *
     * @param name the name of the group
     * @return group or null if not found
     */
    ServiceGroup getServiceGroup(String name);

    /**
     * Gets a list of all {@link ServiceGroup}s
     *
     * @return list of groups
     */
    List<ServiceGroup> getServiceGroups();

    /**
     * Gets the cache of the {@link Service}s and {@link ServiceGroup}s
     *
     * @return map cache
     */
    Map<ServiceGroup, List<Service>> getServiceMap();

}
