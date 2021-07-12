package de.lystx.hytoracloud.launcher.cloud.impl.manager.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.implementations.ServiceObject;
import de.lystx.hytoracloud.driver.utils.utillity.*;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStart;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStop;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;

import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.IServiceManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.TemplateService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.ServiceStarter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.ServiceStopper;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;

/**
 * The {@link CloudSideServiceManager} manages the whole network.
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
public class CloudSideServiceManager implements ICloudService, IServiceManager, NetworkHandler {

    private Map<IServiceGroup, List<IService>> cachedServices;
    private List<IService> globalIServices;
    private final Map<String, Action> actions;


    private final List<IService> unverifiedIServices;

    private final List<IService> cloudServers;
    private final List<IService> cloudProxies;
    private final List<IService> lobbies;
    private boolean startUp;

    private final IDService idService;
    private PortService portService;

    private boolean running = true;

    private List<IServiceGroup> serviceGroups;

    public CloudSideServiceManager(List<IServiceGroup> serviceGroups) {
        this.serviceGroups = serviceGroups;
        this.actions = new HashMap<>();
        this.cachedServices = new HashMap<>();
        this.globalIServices = new LinkedList<>();
        this.unverifiedIServices = new LinkedList<>();

        this.startUp = false;
        this.cloudServers = new LinkedList<>();
        this.lobbies = new LinkedList<>();
        this.cloudProxies = new LinkedList<>();

        this.idService = new IDService();
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
            this.portService = new PortService(networkConfig.getProxyStartPort(), networkConfig.getServerStartPort());
        } else {
            ReceiverInfo receiverInfo = CloudDriver.getInstance().getImplementedData().getObject("receiverInfo", ReceiverInfo.class);
            this.portService = new PortService(Integer.parseInt((receiverInfo.getValues().get("proxyStartPort") + "").split("\\.")[0]), Integer.parseInt((receiverInfo.getValues().get("serverStartPort") + "").split("\\.")[0]));
        }
        FileService fs = CloudDriver.getInstance().getInstance(FileService.class);

        CloudDriver.getInstance().registerNetworkHandler(this);

        this.startServices();
    }

    @Override
    public void updateGroup(IServiceGroup group) {
        IServiceGroup serviceGroup = this.getServiceGroup(group.getName());

        List<IService> list = this.getCachedServices(serviceGroup);
        this.cachedServices.remove(serviceGroup);
        this.cachedServices.put(group, list);
    }

    /**
     * Sends notify to console
     * @param IService
     */
    public void notifyStart(IService IService) {

        if (!this.running) {
            return;
        }
        List<IService> list = this.getCachedServices(IService.getGroup());
        if (!list.contains(IService)) {
            list.add(IService);
            this.cachedServices.put(this.getServiceGroup(IService.getGroup().getName()), list);
        }
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return;
        }

        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "The Wrapper §b" + IService.getGroup().getReceiver() + " §7was told to queue §3" + IService.getName() + " §h[§7ID: §b" + IService.getId() + " §7| §7Port: §b" + IService.getPort() + " §7| §7Mode: §b" + IService.getGroup().getType() + " §7| §7Storage: §b" + (IService.getGroup().isDynamic() ? "DYNAMIC": "STATIC") + "§h]");

    }

    /**
     * Sends stop notify
     * @param IService
     */
    public void notifyStop(IService IService) {

        if (!this.running) {
            return;
        }
        List<IService> IServices = this.cachedServices.get(IService.getGroup());
        IService remove = this.getService(IService.getName());
        if (IServices == null) IServices = new LinkedList<>();
        IServices.remove(remove);
        this.cachedServices.put(this.getServiceGroup(IService.getGroup().getName()), IServices);
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return;
        }
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "The Wrapper §b" + IService.getGroup().getReceiver() + " §7stopped §c" + IService.getName() + "§f!");
    }

    /**
     * Updates service
     * @param IService
     * @param state
     */
    public void updateService(IService IService) {
        if (!this.running) {
            return;
        }

        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerUpdate(IService);
        }

        List<IService> IServices = this.getCachedServices(IService.getGroup());

        IServices.set(IServices.indexOf(this.getService(IService.getName())), IService.deepCopy());

        IServiceGroup IServiceGroup = this.getServiceGroup(IService.getGroup().getName());
        if (IServiceGroup == null) {
            IServiceGroup = IService.getGroup();
        }
        this.cachedServices.put(IServiceGroup, IServices);
    }

    /**
     * Starts services
     */
    public void startServices() {
        this.startServices(this.serviceGroups);
    }

    /**
     * Checks if {@link IServiceGroup} is allowed
     * to start on this receiver
     * @param IServiceGroup
     * @return
     */
    public boolean isRightReceiver(IServiceGroup IServiceGroup) {
        if (getDriver().getDriverType().equals(CloudType.RECEIVER)) {
            ReceiverInfo info = getDriver().getImplementedData().getObject("receiverInfo", ReceiverInfo.class);
            return IServiceGroup.getReceiver().equalsIgnoreCase(info.getName());
        } else if (getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return IServiceGroup.getReceiver().equalsIgnoreCase(Utils.INTERNAL_RECEIVER);
        }
        return true;
    }

    /**
     * Starts services from list
     * @param IServiceGroups
     */
    public void startServices(List<IServiceGroup> IServiceGroups) {
        if (!this.running) {
            return;
        }
        for (IServiceGroup IServiceGroup : IServiceGroups) {
            if (!this.isRightReceiver(IServiceGroup)) {
                continue;
            }
            this.getDriver().getInstance(TemplateService.class).createTemplate(IServiceGroup);
            for (int i = 0; i < IServiceGroup.getMinServer(); i++) {
                int id = this.idService.getFreeID(IServiceGroup.getName());
                int port = IServiceGroup.getType().equals(ServiceType.SPIGOT) ? this.portService.getFreePort() : this.portService.getFreeProxyPort();

                IService IService = new ServiceObject(IServiceGroup, id, port);

                if (IServiceGroup.getType().equals(ServiceType.SPIGOT)) {
                    if (IServiceGroup.isLobby()) {
                        this.lobbies.add(IService);
                    } else {
                        this.cloudServers.add(IService);
                    }
                } else {
                    cloudProxies.add(IService);
                }
            }
        }
        this.cloudServers.sort(Comparator.comparingInt(IService::getId));
        this.cloudProxies.sort(Comparator.comparingInt(IService::getId));
        this.lobbies.sort(Comparator.comparingInt(IService::getId));

        for (IService proxy : cloudProxies) {
            this.startService(proxy.getGroup(), proxy);
        }

        this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> {
            for (IService IService : this.lobbies) {
                this.startService(IService.getGroup(), IService);
            }
            this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> {
                for (IService IService : this.cloudServers) {
                    this.startService(IService.getGroup(), IService);
                }
            }, 2L);
        }, 3L);
    }

    /**
     * Returns all Lobby-Servers
     * @return
     */
    public List<IService> getLobbies() {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getAllServices()) {
            if (IService.getGroup().isLobby() && IService.getGroup().getType().equals(ServiceType.SPIGOT)) {
                list.add(IService);
            }
        };
        return list;
    }

    /**
     * Registers service after packetHandler handled
     * @param service
     */
    public IService registerService(String service) {

        IService iService = this.unverifiedIServices.stream().filter(s -> s.getName().equalsIgnoreCase(service)).findFirst().orElse(null);

        if (iService == null) {
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("ERROR", "§cTried to register §e" + service + " §calthough it hasn't been started properly!");
            return null;
        }

        //Calling handlers
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerRegister(iService);
        }

        List<IService> list = this.getCachedServices(iService.getGroup());

        IService checkGet = list.stream().filter(s -> s.getName().equalsIgnoreCase(service)).findFirst().orElse(null);
        if (checkGet == null) {
            list.add(iService);
            this.cachedServices.put(this.getServiceGroup(iService.getGroup().getName()), list);
        }

        Action action = this.actions.getOrDefault(iService.getName(), new Action());
        this.actions.remove(iService.getName());

        //If in screen not sending message!
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return iService;
        }
        this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Service §b" + iService.getName() + " §7has connected §h[§b" + iService.getGroup().getReceiver() + "@" + iService.getHost() + "§h] §7in §b" + action.getMS() + "s§f!");

        return iService;
    }

    /**
     * Checks if serviceGroup needs services
     * @param IServiceGroup
     */
    public void needServices(IServiceGroup IServiceGroup) {
        if (!this.isRightReceiver(IServiceGroup)) {
            return;
        }
        if (!this.running) {
            return;
        }
        this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> {
            if (this.getCachedServices(IServiceGroup).size() < IServiceGroup.getMinServer()) {
                for (int i = this.getCachedServices(IServiceGroup).size(); i < IServiceGroup.getMinServer(); i++) {
                    int id = idService.getFreeID(IServiceGroup.getName());
                    int port = IServiceGroup.getType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
                    IService IService = new ServiceObject(IServiceGroup, id, port);
                    this.startService(IServiceGroup, IService);
                }
            }
        }, 3L);
    }

    /**
     * Starts service
     * @param IServiceGroup
     * @param IService
     * @param properties
     */
    public void startService(IServiceGroup IServiceGroup, IService IService, PropertyObject properties) {
        if (!this.running) {
            return;
        }
        if (!this.isRightReceiver(IServiceGroup)) {
            return;
        }
        if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            if (this.getDriver().getInstance(GroupService.class).getGroup(IServiceGroup.getName(), this.serviceGroups) == null) {
                return;
            }
        }
        if (IServiceGroup.getMaxServer() != -1 && this.getCachedServices(IServiceGroup).size() >= IServiceGroup.getMaxServer()) {
            this.getDriver().getParent().getConsole().getLogger().sendMessage("INFO", "§cThe service §e" + IService.getName() + " §cwasn't started because there are §9[§e" + this.getCachedServices(IServiceGroup).size() + "§9/§e" + IServiceGroup.getMaxServer() + "§9] §cservices of this group online!");
            return;
        }
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerQueue(IService);
        }

        this.unverifiedIServices.add(IService);

        if (IService.getPort() <= 0) {
            int port = IService.getGroup().getType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
            int id = this.idService.getFreeID(IServiceGroup.getName());
            IService = new ServiceObject(IServiceGroup, id, port);
        }
        IService.setProperties((properties == null ? new PropertyObject() : properties));
        this.globalIServices.add(IService);
        List<IService> IServices = this.getCachedServices(IServiceGroup);
        IServices.add(IService);


        IServiceGroup IServiceGroup1 = this.getServiceGroup(IServiceGroup.getName());
        if (IServiceGroup1 == null) {
            this.cachedServices.put(IServiceGroup, IServices);
        } else {
            this.cachedServices.put(IServiceGroup1, IServices);
        }

        this.actions.put(IService.getName(), new Action());


        ServiceStarter serviceStarter = new ServiceStarter(IService, properties);

        if (serviceStarter.checkForSpigot()) {
            try {
                serviceStarter.copyFiles();
                serviceStarter.createProperties();
                serviceStarter.createCloudFiles();
                serviceStarter.start(new Consumer<IService>() {
                    @Override
                    public void accept(IService IService) {
                        notifyStart(IService);
                        CloudDriver.getInstance().callEvent(new DriverEventServiceStart(IService));

                        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
                            networkHandler.onServerStart(IService);
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
     * @param IServiceGroup
     * @param IService
     */
    public void startService(IServiceGroup IServiceGroup, IService IService) {
        if (!this.running) {
            return;
        }
        this.startService(IServiceGroup, IService, null);
    }

    /**
     * Starts service with no properties
     * @param IServiceGroup
     * @return
     */
    public void startService(IServiceGroup IServiceGroup) {
        if (!this.running) {
            return;
        }
        this.startService(IServiceGroup, (PropertyObject) null);
    }

    /**
     * Starts service with properties
     * @param IServiceGroup
     * @param properties
     * @return
     */
    public void startService(IServiceGroup IServiceGroup, PropertyObject properties) {
        if (!this.running) {
            return;
        }
        int id = this.idService.getFreeID(IServiceGroup.getName());
        int port = IServiceGroup.getType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
        IService IService = new ServiceObject(IServiceGroup, id, port);
        this.startService(IServiceGroup, IService, properties);
    }

    /**
     * Stops service
     * @param IService
     */
    public void stopService(IService IService) {
        this.stopService(IService, true);
    }

    /**
     * Stops service
     * @param IService
     * @param newServices > Should new services start if needed
     */
    public void stopService(IService IService, boolean newServices) {
        if (!this.isRightReceiver(IService.getGroup())) {
            return;
        }

        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerStop(IService);
        }

        try {
            this.getDriver().sendPacket(new PacketOutStopServer(IService.getName()));
            try {
                this.idService.removeID(IService.getGroup().getName(), IService.getId());
                this.portService.removeProxyPort(IService.getPort());
                this.portService.removePort(IService.getPort());
            } catch (NullPointerException e) {
                //Ignoring Ubuntu Error
            }

            List<IService> IServices = this.cachedServices.get(this.getServiceGroup(IService.getGroup().getName()));
            IService remove = this.getService(IService.getName());
            if (IServices == null) IServices = new LinkedList<>();
            IServices.remove(remove);
            this.cachedServices.put(this.getServiceGroup(IService.getGroup().getName()), IServices);


            ServiceStopper serviceStopper = new ServiceStopper(IService);
            if (!CloudDriver.getInstance().callEvent(new DriverEventServiceStop(IService))) {
                serviceStopper.stop(new Consumer<IService>() {
                    @Override
                    public void accept(IService IService) {
                        if (!newServices) {
                            return;
                        }
                        needServices(IService.getGroup());
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
    public List<IService> getAllServices(ServiceState serviceState) {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getAllServices()) {
            if (!IService.getGroup().getType().equals(ServiceType.SPIGOT)) {
                continue;
            }
            if (IService.getState().equals(serviceState)) {
                list.add(IService);
            }
        }
        return list;
    }

    /**
     * Returns all Services from a Type
     * @param serviceType
     * @return
     */
    public List<IService> getAllServices(ServiceType serviceType) {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getAllServices()) {
            if (IService.getGroup().getType().equals(serviceType)) {
                list.add(IService);
            }
        }
        return list;
    }


    /**
     * Stops all services
     */
    public void stopServices() {
        List<String> already = new LinkedList<>();
        for (IServiceGroup IServiceGroup : new LinkedList<>(this.cachedServices.keySet())) {
            if (this.getDriver().getInstance(GroupService.class) != null && this.getDriver().getInstance(GroupService.class).getGroup(IServiceGroup.getName(), this.serviceGroups) == null) {
                continue;
            }
            if (!already.contains(IServiceGroup.getName())) {
                already.add(IServiceGroup.getName());
                if (this.getDriver().getParent().getScreenPrinter().getScreen() == null && !this.getDriver().getParent().getScreenPrinter().isInScreen()) {
                    this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Stopping services of the group §3" + IServiceGroup.getName() + " §h[§7Services: §3" + this.cachedServices.get(IServiceGroup).size() + "§h]");
                }
            }
        }

        for (Object s : (this.globalIServices == null ? new LinkedList<>() : this.globalIServices)) {
            IService globalIService = (IService)s;
            IService IService = this.getService(globalIService.getName());
            if (IService == null) {
                continue;
            }
            this.stopService(IService);
        }
    }

    /**
     * Stops services from group
     * @param IServiceGroup
     */
    public void stopServices(IServiceGroup IServiceGroup) {
        this.stopServices(IServiceGroup, true);
    }

    @Override
    public List<IService> getServices(IServiceGroup IServiceGroup) {
        List<IService> list = new LinkedList<>();
        for (IService allIService : this.getAllServices()) {
            if (allIService.getGroup().getName().equalsIgnoreCase(IServiceGroup.getName())) {
                list.add(allIService);
            }
        }
        return list;
    }

    /**
     * Stops services from group
     * @param IServiceGroup
     * @param newOnes > Should new ones start
     */
    public void stopServices(IServiceGroup IServiceGroup, boolean newOnes) {
        if (!this.isRightReceiver(IServiceGroup)) {
            return;
        }
        Value<Integer> count = new Value<>(this.getCachedServices(IServiceGroup).size());
        try {
            for (IService IService : new LinkedList<>(this.getCachedServices(IServiceGroup))) {
                this.stopService(IService, false);
                count.setValue(count.get() - 1);

                if (count.get() == 0 && newOnes) {
                    this.needServices(IServiceGroup);
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
    public List<IService> getCachedServices(IServiceGroup serviceGroup) {
        List<IService> list = this.cachedServices.get(this.getServiceGroup(serviceGroup.getName()));
        if (list == null) list = new LinkedList<>();

        return list;
    }

    /**
     * Returns services by name
     * @param name
     * @return
     */
    public IService getService(String name) {
        try {
            for (List<IService> value : new LinkedList<>(this.cachedServices.values())) {
                for (IService IService : value) {
                    if (IService.getName().equalsIgnoreCase(name)) {
                        return IService;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public void registerService(IService service) {
        IService service1 = this.getService(service.getName());

        if (service1 == null) {
            IServiceGroup serviceGroup = this.getServiceGroup(service.getGroup().getName());
            List<IService> cachedServices = this.getCachedServices(serviceGroup);

            cachedServices.add(service);
            this.cachedServices.put(serviceGroup, cachedServices);
        }
    }

    public List<IService> getAllServices() {
        List<IService> list = new LinkedList<>();
        for (List<IService> value : new LinkedList<>(this.cachedServices.values())) {
            list.addAll(value);
        }
        return list;
    }

    /**
     * Returns group by name
     * @param name
     * @return
     */
    public IServiceGroup getServiceGroup(String name) {
        return this.cachedServices.keySet().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public List<IServiceGroup> getServiceGroups() {
        return new LinkedList<>(this.cachedServices.keySet());
    }

    /**
     * Returns Proxy ({@link IService}) by port
     * @param port
     * @return
     */
    @Override
    public IService getProxy(Integer port) {
        return this.getAllServices().stream().filter(service -> service.getPort() == port).findFirst().orElse(null);
    }

    @Override
    public void reload() {
    }

    @Override
    public void save() {

    }

}
