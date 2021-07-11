package de.lystx.hytoracloud.launcher.cloud.impl.manager.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.utils.utillity.ReceiverInfo;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStart;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStop;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStartedServer;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.service.global.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.global.main.ICloudService;
import de.lystx.hytoracloud.driver.service.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.service.cloud.server.IServiceManager;
import de.lystx.hytoracloud.driver.service.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.service.cloud.server.impl.TemplateService;
import de.lystx.hytoracloud.driver.service.cloud.server.impl.ServiceStarter;
import de.lystx.hytoracloud.driver.service.cloud.server.impl.ServiceStopper;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.utils.utillity.Action;
import de.lystx.hytoracloud.driver.utils.utillity.Value;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;

/**
 * The {@link DefaultServiceManager} manages the whole network.
 * It starts and stops services and checks for services that
 * should be online as you defined it in your specific group.
 * It will register the Services and look how long it took to boot
 * the service and much more
 */
@Getter @Setter
@ICloudServiceInfo(
        name = "ServiceManager",
        type = CloudServiceType.NETWORK,
        description = {
                "This service is used to start and stop services",
                "You can list services and manage everything here"
        },
        version = 1.5
)
public class DefaultServiceManager implements ICloudService, IServiceManager, NetworkHandler {

    private Map<ServiceGroup, List<Service>> cachedServices;
    private List<Service> globalServices;
    private final Map<String, Action> actions;


    private final List<Service> unverifiedServices;

    private final List<Service> cloudServers;
    private final List<Service> cloudProxies;
    private final List<Service> lobbies;
    private boolean startUp;

    private final IDService idService;
    private PortService portService;

    private boolean running = true;

    private List<ServiceGroup> serviceGroups;

    public DefaultServiceManager(List<ServiceGroup> serviceGroups) {
        this.serviceGroups = serviceGroups;
        this.actions = new HashMap<>();
        this.cachedServices = new HashMap<>();
        this.globalServices = new LinkedList<>();
        this.unverifiedServices = new LinkedList<>();

        this.startUp = false;
        this.cloudServers = new LinkedList<>();
        this.lobbies = new LinkedList<>();
        this.cloudProxies = new LinkedList<>();

        this.idService = new IDService();
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
            this.portService = new PortService(networkConfig.getGlobalProxyConfig().getProxyStartPort(), networkConfig.getGlobalProxyConfig().getServerStartPort());
        } else {
            ReceiverInfo receiverInfo = CloudDriver.getInstance().getImplementedData().getObject("receiverInfo", ReceiverInfo.class);
            this.portService = new PortService(Integer.parseInt((receiverInfo.getValues().get("proxyStartPort") + "").split("\\.")[0]), Integer.parseInt((receiverInfo.getValues().get("serverStartPort") + "").split("\\.")[0]));
        }
        FileService fs = CloudDriver.getInstance().getInstance(FileService.class);

        CloudDriver.getInstance().registerNetworkHandler(this);

        this.startServices();
    }

    /**
     * Updates a group
     * @param group
     * @param newGroup
     */
    public void updateGroup(ServiceGroup group, ServiceGroup newGroup) {
        List<Service> list = this.getCachedServices(this.getServiceGroup(group.getName()));
        this.cachedServices.put(this.getServiceGroup(group.getName()), list);

    }

    /**
     * Sends notify to console
     * @param service
     */
    public void notifyStart(Service service) {

        if (!this.running) {
            return;
        }
        List<Service> list = this.getCachedServices(service.getServiceGroup());
        if (!list.contains(service)) {
            list.add(service);
            this.cachedServices.put(this.getServiceGroup(service.getServiceGroup().getName()), list);
        }
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return;
        }

        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "The Wrapper §b" + service.getServiceGroup().getReceiver() + " §7was told to queue §3" + service.getName() + " §h[§7ID: §b" + service.getServiceID() + " §7| §7Port: §b" + service.getPort() + " §7| §7Mode: §b" + service.getServiceGroup().getServiceType() + " §7| §7Storage: §b" + (service.getServiceGroup().isDynamic() ? "DYNAMIC": "STATIC") + "§h]");

    }

    /**
     * Sends stop notify
     * @param service
     */
    public void notifyStop(Service service) {

        if (!this.running) {
            return;
        }
        List<Service> services = this.cachedServices.get(service.getServiceGroup());
        Service remove = this.getService(service.getName());
        if (services == null) services = new LinkedList<>();
        services.remove(remove);
        this.cachedServices.put(this.getServiceGroup(service.getServiceGroup().getName()), services);
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return;
        }
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "The Wrapper §b" + service.getServiceGroup().getReceiver() + " §7stopped §c" + service.getName() + "§f!");
    }

    /**
     * Updates service
     * @param service
     * @param state
     */
    public void updateService(Service service) {
        if (!this.running) {
            return;
        }

        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerUpdate(service);
        }

        List<Service> services = this.getCachedServices(service.getServiceGroup());

        services.set(services.indexOf(this.getService(service.getName())), service.deepCopy());

        ServiceGroup serviceGroup = this.getServiceGroup(service.getServiceGroup().getName());
        if (serviceGroup == null) {
            serviceGroup = service.getServiceGroup();
        }
        this.cachedServices.put(serviceGroup, services);
    }

    /**
     * Starts services
     */
    public void startServices() {
        this.startServices(this.serviceGroups);
    }

    /**
     * Checks if {@link ServiceGroup} is allowed
     * to start on this receiver
     * @param serviceGroup
     * @return
     */
    public boolean isRightReceiver(ServiceGroup serviceGroup) {
        if (getDriver().getDriverType().equals(CloudType.RECEIVER)) {
            ReceiverInfo info = getDriver().getImplementedData().getObject("receiverInfo", ReceiverInfo.class);
            return serviceGroup.getReceiver().equalsIgnoreCase(info.getName());
        } else if (getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return serviceGroup.getReceiver().equalsIgnoreCase(Utils.INTERNAL_RECEIVER);
        }
        return true;
    }

    /**
     * Starts services from list
     * @param serviceGroups
     */
    public void startServices(List<ServiceGroup> serviceGroups) {
        if (!this.running) {
            return;
        }
        for (ServiceGroup serviceGroup : serviceGroups) {
            if (!this.isRightReceiver(serviceGroup)) {
                continue;
            }
            this.getDriver().getInstance(TemplateService.class).createTemplate(serviceGroup);
            for (int i = 0; i < serviceGroup.getMinServer(); i++) {
                int id = this.idService.getFreeID(serviceGroup.getName());
                int port = serviceGroup.getServiceType().equals(ServiceType.SPIGOT) ? this.portService.getFreePort() : this.portService.getFreeProxyPort();

                Service service = new Service(serviceGroup, id, port);

                if (serviceGroup.getServiceType().equals(ServiceType.SPIGOT)) {
                    if (serviceGroup.isLobby()) {
                        this.lobbies.add(service);
                    } else {
                        this.cloudServers.add(service);
                    }
                } else {
                    cloudProxies.add(service);
                }
            }
        }
        this.cloudServers.sort(Comparator.comparingInt(Service::getServiceID));
        this.cloudProxies.sort(Comparator.comparingInt(Service::getServiceID));
        this.lobbies.sort(Comparator.comparingInt(Service::getServiceID));

        for (Service proxy : cloudProxies) {
            this.startService(proxy.getServiceGroup(), proxy);
        }

        this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> {
            for (Service service : this.lobbies) {
                this.startService(service.getServiceGroup(), service);
            }
            this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> {
                for (Service service : this.cloudServers) {
                    this.startService(service.getServiceGroup(), service);
                }
            }, 2L);
        }, 3L);
    }

    /**
     * Returns all Lobby-Servers
     * @return
     */
    public List<Service> getLobbies() {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getAllServices()) {
            if (service.getServiceGroup().isLobby() && service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                list.add(service);
            }
        };
        return list;
    }

    /**
     * Registers service after packetHandler handled
     * @param service
     */
    public Service registerService(String service) {
        if (!this.running) {
            return null;
        }


        Service safeService = this.unverifiedServices.stream().filter(s -> s.getName().equalsIgnoreCase(service)).findFirst().orElse(null);

        if (safeService == null) {
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("ERROR", "§cTried to register §e" + service + " §calthough it hasn't been started properly!");
            return null;
        }

        //Calling handlers
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerRegister(safeService);
        }

        List<Service> list = this.getCachedServices(safeService.getServiceGroup());
        Service contains = list.stream().filter(service1 -> service1.getName().equalsIgnoreCase(safeService.getName())).findFirst().orElse(null);

        Action action = this.actions.getOrDefault(safeService.getName(), new Action());
        if (contains == null) {
            list.add(safeService);
            this.cachedServices.put(this.getServiceGroup(safeService.getServiceGroup().getName()), list);

            //Sending it was registered
            this.getDriver().sendPacket(new PacketOutRegisterServer(safeService).setAction(action.getMS()));
            this.actions.remove(safeService.getName());
        }

        //If in screen not sending message!
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return safeService;
        }
        this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Service §b" + safeService.getName() + " §7has connected §h[§b" + safeService.getServiceGroup().getReceiver() + "@" + safeService.getHost() + "§h] §7in §b" + action.getMS() + "s§f!");

        return safeService;
    }

    /**
     * Checks if serviceGroup needs services
     * @param serviceGroup
     */
    public void needServices(ServiceGroup serviceGroup) {
        if (!this.isRightReceiver(serviceGroup)) {
            return;
        }
        if (!this.running) {
            return;
        }
        this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> {
            if (this.getCachedServices(serviceGroup).size() < serviceGroup.getMinServer()) {
                for (int i = this.getCachedServices(serviceGroup).size(); i < serviceGroup.getMinServer(); i++) {
                    int id = idService.getFreeID(serviceGroup.getName());
                    int port = serviceGroup.getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
                    Service service = new Service(serviceGroup, id, port);
                    this.startService(serviceGroup, service);
                }
            }
        }, 3L);
    }

    /**
     * Starts service
     * @param serviceGroup
     * @param service
     * @param properties
     */
    public void startService(ServiceGroup serviceGroup, Service service, PropertyObject properties) {
        if (!this.running) {
            return;
        }
        if (!this.isRightReceiver(serviceGroup)) {
            return;
        }
        if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            if (this.getDriver().getInstance(GroupService.class).getGroup(serviceGroup.getName(), this.serviceGroups) == null) {
                return;
            }
        }
        if (serviceGroup.getMaxServer() != -1 && this.getCachedServices(serviceGroup).size() >= serviceGroup.getMaxServer()) {
            this.getDriver().getParent().getConsole().getLogger().sendMessage("INFO", "§cThe service §e" + service.getName() + " §cwasn't started because there are §9[§e" + this.getCachedServices(serviceGroup).size() + "§9/§e" + serviceGroup.getMaxServer() + "§9] §cservices of this group online!");
            return;
        }
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerQueue(service);
        }


        CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().add("startedServices");
        this.unverifiedServices.add(service);

        if (service.getPort() <= 0) {
            int port = service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
            int id = this.idService.getFreeID(serviceGroup.getName());
            service = new Service(serviceGroup, id, port);
        }
        service.setProperties((properties == null ? new PropertyObject() : properties));
        this.globalServices.add(service);
        List<Service> services = this.getCachedServices(serviceGroup);
        services.add(service);


        ServiceGroup serviceGroup1 = this.getServiceGroup(serviceGroup.getName());
        if (serviceGroup1 == null) {
            this.cachedServices.put(serviceGroup, services);
        } else {
            this.cachedServices.put(serviceGroup1, services);
        }

        this.actions.put(service.getName(), new Action());


        ServiceStarter serviceStarter = new ServiceStarter(service, properties);

        if (serviceStarter.checkForSpigot()) {
            try {
                serviceStarter.copyFiles();
                serviceStarter.createProperties();
                serviceStarter.createCloudFiles();
                serviceStarter.start(new Consumer<Service>() {
                    @Override
                    public void accept(Service service) {
                        CloudDriver.getInstance().sendPacket(new PacketOutStartedServer(service.getName()));
                        notifyStart(service);
                        CloudDriver.getInstance().callEvent(new DriverEventServiceStart(service));

                        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
                            networkHandler.onServerStart(service);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return;
    }

    /**
     * Starts service from group
     * @param serviceGroup
     * @param service
     */
    public void startService(ServiceGroup serviceGroup, Service service) {
        if (!this.running) {
            return;
        }
        this.startService(serviceGroup, service, null);
    }

    /**
     * Starts service with no properties
     * @param serviceGroup
     * @return
     */
    public void startService(ServiceGroup serviceGroup) {
        if (!this.running) {
            return;
        }
        this.startService(serviceGroup, (PropertyObject) null);
    }

    /**
     * Starts service with properties
     * @param serviceGroup
     * @param properties
     * @return
     */
    public void startService(ServiceGroup serviceGroup, PropertyObject properties) {
        if (!this.running) {
            return;
        }
        int id = this.idService.getFreeID(serviceGroup.getName());
        int port = serviceGroup.getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
        Service service = new Service(serviceGroup, id, port);
        this.startService(serviceGroup, service, properties);
    }

    /**
     * Stops service
     * @param service
     */
    public void stopService(Service service) {
        this.stopService(service, true);
    }

    /**
     * Stops service
     * @param service
     * @param newServices > Should new services start if needed
     */
    public void stopService(Service service, boolean newServices) {
        if (!this.isRightReceiver(service.getServiceGroup())) {
            return;
        }

        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerStop(service);
        }

        try {
            this.getDriver().sendPacket(new PacketOutStopServer(service.getName()));
            try {
                this.idService.removeID(service.getServiceGroup().getName(), service.getServiceID());
                this.portService.removeProxyPort(service.getPort());
                this.portService.removePort(service.getPort());
            } catch (NullPointerException e) {
                //Ignoring Ubuntu Error
            }

            List<Service> services = this.cachedServices.get(this.getServiceGroup(service.getServiceGroup().getName()));
            Service remove = this.getService(service.getName());
            if (services == null) services = new LinkedList<>();
            services.remove(remove);
            this.cachedServices.put(this.getServiceGroup(service.getServiceGroup().getName()), services);


            ServiceStopper serviceStopper = new ServiceStopper(service);
            if (!CloudDriver.getInstance().callEvent(new DriverEventServiceStop(service))) {
                serviceStopper.stop(new Consumer<Service>() {
                    @Override
                    public void accept(Service service) {
                        if (!newServices) {
                            return;
                        }
                        needServices(service.getServiceGroup());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns all Services with
     * a given {@link ServiceState}
     * @param serviceState
     * @return
     */
    public List<Service> getAllServices(ServiceState serviceState) {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getAllServices()) {
            if (!service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                continue;
            }
            if (service.getServiceState().equals(serviceState)) {
                list.add(service);
            }
        }
        return list;
    }

    /**
     * Returns all Services from a Type
     * @param serviceType
     * @return
     */
    public List<Service> getAllServices(ServiceType serviceType) {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getAllServices()) {
            if (service.getServiceGroup().getServiceType().equals(serviceType)) {
                list.add(service);
            }
        }
        return list;
    }


    /**
     * Stops all services
     */
    public void stopServices() {
        List<String> already = new LinkedList<>();
        for (ServiceGroup serviceGroup : new LinkedList<>(this.cachedServices.keySet())) {
            if (this.getDriver().getInstance(GroupService.class) != null && this.getDriver().getInstance(GroupService.class).getGroup(serviceGroup.getName(), this.serviceGroups) == null) {
                continue;
            }
            if (!already.contains(serviceGroup.getName())) {
                already.add(serviceGroup.getName());
                if (this.getDriver().getParent().getScreenPrinter().getScreen() == null && !this.getDriver().getParent().getScreenPrinter().isInScreen()) {
                    this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Stopping services of the group §3" + serviceGroup.getName() + " §h[§7Services: §3" + this.cachedServices.get(serviceGroup).size() + "§h]");
                }
            }
        }

        for (Object s : (this.globalServices == null ? new LinkedList<>() : this.globalServices)) {
            Service globalService = (Service)s;
            Service service = this.getService(globalService.getName());
            if (service == null) {
                continue;
            }
            this.stopService(service);
        }
    }

    /**
     * Stops services from group
     * @param serviceGroup
     */
    public void stopServices(ServiceGroup serviceGroup) {
        this.stopServices(serviceGroup, true);
    }

    @Override
    public List<Service> getServices(ServiceGroup serviceGroup) {
        List<Service> list = new LinkedList<>();
        for (Service allService : this.getAllServices()) {
            if (allService.getServiceGroup().getName().equalsIgnoreCase(serviceGroup.getName())) {
                list.add(allService);
            }
        }
        return list;
    }

    /**
     * Stops services from group
     * @param serviceGroup
     * @param newOnes > Should new ones start
     */
    public void stopServices(ServiceGroup serviceGroup, boolean newOnes) {
        if (!this.isRightReceiver(serviceGroup)) {
            return;
        }
        Value<Integer> count = new Value<>(this.getCachedServices(serviceGroup).size());
        try {
            for (Service service : new LinkedList<>(this.getCachedServices(serviceGroup))) {
                this.stopService(service, false);
                count.setValue(count.get() - 1);

                if (count.get() == 0 && newOnes) {
                    this.needServices(serviceGroup);
                }
            }
        } catch (ConcurrentModificationException e) {
            //Ignoring just continueing
        }
    }

    /**
     * Returns all services from a group
     * @param serviceGroup
     * @return
     */
    public List<Service> getCachedServices(ServiceGroup serviceGroup) {
        List<Service> list = this.cachedServices.get(this.getServiceGroup(serviceGroup.getName()));
        if (list == null) list = new LinkedList<>();

        return list;
    }

    /**
     * Returns services by name
     * @param name
     * @return
     */
    public Service getService(String name) {
        try {
            for (List<Service> value : new LinkedList<>(this.cachedServices.values())) {
                for (Service service : value) {
                    if (service.getName().equalsIgnoreCase(name)) {
                        return service;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<Service> getAllServices() {
        List<Service> list = new LinkedList<>();
        for (List<Service> value : new LinkedList<>(this.cachedServices.values())) {
            list.addAll(value);
        }
        return list;
    }

    /**
     * Returns group by name
     * @param name
     * @return
     */
    public ServiceGroup getServiceGroup(String name) {
        return this.cachedServices.keySet().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns Proxy ({@link Service}) by port
     * @param port
     * @return
     */
    @Override
    public Service getProxy(Integer port) {
        return this.getAllServices().stream().filter(service -> service.getPort() == port).findFirst().orElse(null);
    }

}
