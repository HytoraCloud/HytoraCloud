package de.lystx.hytoracloud.launcher.cloud.impl.manager.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.other.*;
import de.lystx.hytoracloud.driver.commons.interfaces.Acceptable;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.implementations.ServiceObject;
import de.lystx.hytoracloud.driver.utils.utillity.*;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
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
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.response.Response;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

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
     * @param service
     */
    public void notifyStop(IService service) {

        if (!this.running) {
            return;
        }
        List<IService> IServices = this.cachedServices.get(service.getGroup());
        IService remove = this.getCachedObject(service.getName());
        if (IServices == null) IServices = new LinkedList<>();
        IServices.remove(remove);
        this.cachedServices.put(this.getServiceGroup(service.getGroup().getName()), IServices);
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return;
        }
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "The Wrapper §b" + service.getGroup().getReceiver() + " §7stopped §c" + service.getName() + "§f!");
    }


    @Override
    public void updateService(IService service) {
        if (!this.running) {
            return;
        }

        List<IService> cachedServices = this.getCachedServices(service.getGroup());

        cachedServices.set(cachedServices.indexOf(this.getCachedObject(service.getName())), service.deepCopy());

        IServiceGroup serviceGroup = this.getServiceGroup(service.getGroup().getName());
        if (serviceGroup == null) {
            serviceGroup = service.getGroup();
        }
        this.cachedServices.put(serviceGroup, cachedServices);

        CloudDriver.getInstance().callEvent(new DriverEventServiceUpdate(service));
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


    @Override
    public void startServices(List<IServiceGroup> serviceGroups) {
        if (!this.running) {
            return;
        }
        for (IServiceGroup group : serviceGroups) {
            if (!this.isRightReceiver(group)) {
                continue;
            }
            this.getDriver().getInstance(TemplateService.class).createTemplate(group);
            for (int i = 0; i < group.getMinServer(); i++) {
                int id = this.idService.getFreeID(group.getName());
                int port = group.getType().equals(ServiceType.SPIGOT) ? this.portService.getFreePort() : this.portService.getFreeProxyPort();

                IService service = new ServiceObject(group, id, port);

                if (group.getType().equals(ServiceType.SPIGOT)) {
                    if (group.isLobby()) {
                        this.lobbies.add(service);
                    } else {
                        this.cloudServers.add(service);
                    }
                } else {
                    cloudProxies.add(service);
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


    @Override
    public List<IService> getLobbies() {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getCachedObjects()) {
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


    @Override
    public void startService(IServiceGroup serviceGroup, IService service, PropertyObject properties) {
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

        CloudDriver.getInstance().callEvent(new DriverEventServiceQueue(service));
        this.unverifiedIServices.add(service);

        if (service.getPort() <= 0) {
            int port = service.getGroup().getType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
            int id = this.idService.getFreeID(serviceGroup.getName());
            service = new ServiceObject(serviceGroup, id, port);
        }
        service.setProperties((properties == null ? new PropertyObject() : properties));
        this.globalIServices.add(service);
        List<IService> IServices = this.getCachedServices(serviceGroup);
        IServices.add(service);


        IServiceGroup IServiceGroup1 = this.getServiceGroup(serviceGroup.getName());
        if (IServiceGroup1 == null) {
            this.cachedServices.put(serviceGroup, IServices);
        } else {
            this.cachedServices.put(IServiceGroup1, IServices);
        }

        this.actions.put(service.getName(), new Action());


        ServiceStarter serviceStarter = new ServiceStarter(service, properties);

        if (serviceStarter.checkForSpigot()) {
            try {
                serviceStarter.copyFiles();
                serviceStarter.createProperties();
                serviceStarter.createCloudFiles();
                serviceStarter.start(new Consumer<IService>() {
                    @Override
                    public void accept(IService IService) {
                        notifyStart(IService);
                        CloudDriver.getInstance().callEvent(new DriverEventServiceStarted(IService));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return;
    }


    @Override
    public void startService(IServiceGroup serviceGroup, IService service) {
        if (!this.running) {
            return;
        }
        this.startService(serviceGroup, service, null);
    }


    @Override
    public void startService(IServiceGroup serviceGroup) {
        if (!this.running) {
            return;
        }
        this.startService(serviceGroup, (PropertyObject) null);
    }


    @Override
    public void startService(IServiceGroup serviceGroup, PropertyObject properties) {
        if (!this.running) {
            return;
        }
        int id = this.idService.getFreeID(serviceGroup.getName());
        int port = serviceGroup.getType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
        IService IService = new ServiceObject(serviceGroup, id, port);
        this.startService(serviceGroup, IService, properties);
    }


    @Override
    public void stopService(IService service) {
        this.stopService(service, true);
    }

    public void stopService(IService IService, boolean newServices) {
        if (!this.isRightReceiver(IService.getGroup())) {
            return;
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
            IService remove = this.getCachedObject(IService.getName());
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

    @Override
    public List<IService> getCachedObjects(ServiceState serviceState) {
        return this.getCachedObjects(service -> service.getState().equals(serviceState));
    }

    @Override
    public List<IService> getCachedObjects(ServiceType serviceType) {
        return this.getCachedObjects(service -> service.getGroup().getType().equals(serviceType));
    }

    @Override
    public List<IService> getCachedObjects(Acceptable<IService> request) {
        List<IService> list = new LinkedList<>();
        for (IService service : this.getCachedObjects()) {
            if (request.isAccepted(service)) {
                list.add(service);
            }
        }
        return list;
    }


    @Override
    public void shutdownAll() {
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
            IService IService = this.getCachedObject(globalIService.getName());
            if (IService == null) {
                continue;
            }
            this.stopService(IService);
        }
    }


    @Override
    public void shutdownAll(IServiceGroup serviceGroup) {
        this.shutdownAll(serviceGroup, true);
    }

    @Override
    public List<IService> getServices(IServiceGroup serviceGroup) {
        List<IService> list = new LinkedList<>();
        for (IService allIService : this.getCachedObjects()) {
            if (allIService.getGroup().getName().equalsIgnoreCase(serviceGroup.getName())) {
                list.add(allIService);
            }
        }
        return list;
    }

    public void shutdownAll(IServiceGroup IServiceGroup, boolean newOnes) {
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

    public List<IService> getCachedServices(IServiceGroup serviceGroup) {
        List<IService> list = this.cachedServices.get(this.getServiceGroup(serviceGroup.getName()));
        if (list == null) list = new LinkedList<>();

        return list;
    }

    @Override
    public IService getCachedObject(String name) {
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
    public IService getCachedObject(UUID uniqueId) {
        return this.getCachedObjects().stream().filter(service -> service.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public void getObjectAsync(String name, Consumer<IService> consumer) {
        consumer.accept(getCachedObject(name));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<IService> consumer) {
        consumer.accept(getCachedObject(uniqueId));
    }

    @Override
    public Response<IService> getObjectSync(String name) {
        return new Response<IService>() {
            @Override
            public IService get() {
                return getCachedObject(name);
            }

            @Override
            public Component getComponent() {
                return new Component();
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @Override
    public Response<IService> getObjectSync(UUID uniqueId) {
        return new Response<IService>() {
            @Override
            public IService get() {
                return getCachedObject(uniqueId);
            }

            @Override
            public Component getComponent() {
                return new Component();
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @Override
    public void registerService(IService service) {
        IService service1 = this.getCachedObject(service.getName());

        if (service1 == null) {
            IServiceGroup serviceGroup = this.getServiceGroup(service.getGroup().getName());
            List<IService> cachedServices = this.getCachedServices(serviceGroup);

            cachedServices.add(service);
            this.cachedServices.put(serviceGroup, cachedServices);

            CloudDriver.getInstance().callEvent(new DriverEventServiceRegister(service));
        }
    }

    @Override
    public List<IService> getCachedObjects() {
        List<IService> list = new LinkedList<>();
        for (List<IService> value : new LinkedList<>(this.cachedServices.values())) {
            list.addAll(value);
        }
        return list;
    }

    @Override
    public void setCachedObjects(List<IService> cachedObjects) {

    }

    @Override
    public IServiceGroup getServiceGroup(String name) {
        return this.cachedServices.keySet().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public List<IServiceGroup> getCachedGroups() {
        return new LinkedList<>(this.cachedServices.keySet());
    }

    @Override
    public void reload() {
    }

    @Override
    public void save() {

    }

    @NotNull
    @Override
    public Iterator<IService> iterator() {
        return this.getCachedObjects().iterator();
    }
}
