package de.lystx.hytoracloud.launcher.cloud.impl.manager.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.other.*;
import de.lystx.hytoracloud.driver.commons.interfaces.Requestable;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverNotifyStart;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverNotifyStop;
import de.lystx.hytoracloud.driver.utils.Action;
import de.lystx.hytoracloud.driver.commons.service.PropertyObject;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;

import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.IServiceManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.ServiceStarter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.ServiceStopper;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import de.lystx.hytoracloud.launcher.receiver.impl.manager.ConfigService;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.response.Response;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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


    private final List<IService> iServices;

    private final List<IService> cloudServers;
    private final List<IService> cloudProxies;
    private final List<IService> lobbies;
    private boolean startUp;

    private final IDService idService;
    private PortService portService;

    private boolean running = true;

    private List<IServiceGroup> serviceGroups;

    public CloudSideServiceManager(List<IServiceGroup> serviceGroups) {
        this.serviceGroups = new LinkedList<>(serviceGroups);
        this.actions = new HashMap<>();
        this.cachedServices = new HashMap<>();
        this.globalIServices = new LinkedList<>();
        this.iServices = new LinkedList<>();

        this.startUp = false;
        this.cloudServers = new LinkedList<>();
        this.lobbies = new LinkedList<>();
        this.cloudProxies = new LinkedList<>();

        this.idService = new IDService();
        this.portService = new PortService(CloudDriver.getInstance().getNetworkConfig().getProxyStartPort(), CloudDriver.getInstance().getNetworkConfig().getServerStartPort());

        CloudDriver.getInstance().registerNetworkHandler(this);
        this.startServices(this.serviceGroups);
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
     * @param service
     */
    public void notifyStart(IService service) {

        if (!this.running) {
            return;
        }

        List<IService> list = this.getCachedServices(service.getGroup());
        if (!list.contains(service)) {
            list.add(service);
            this.cachedServices.put(this.getServiceGroup(service.getGroup().getName()), list);
        }
        if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            return;
        }

        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "§h'§9" + service.getGroup().getReceiver() + "§h' §7queued §b" + service.getName() + " §h[§7Port: §b" + service.getPort() + " §7| §7Mode: §b" + (service.getGroup().isDynamic() ? "DYNAMIC": "STATIC") + "_" + service.getGroup().getType() + "§h]");

    }

    /**
     * Sends stop notify
     * @param service
     */
    public void notifyStop(IService service) {

        if (!this.running) {
            return;
        }

        List<IService> services = this.cachedServices.get(service.getGroup());
        if (services != null) {
            IService remove = this.getCachedObject(service.getName());
            if (services == null) services = new LinkedList<>();
            services.remove(remove);
            this.cachedServices.put(this.getServiceGroup(service.getGroup().getName()), services);
        }

        if (CloudDriver.getInstance().getDriverType() == CloudType.RECEIVER) {
            CloudDriver.getInstance().sendPacket(new PacketReceiverNotifyStop(service));
            return;
        } else {
            if (this.getDriver().getParent().getScreenPrinter().getScreen() != null && this.getDriver().getParent().getScreenPrinter().isInScreen()) {
                return;
            }
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "§h'§9" + service.getGroup().getReceiver() + "§h' §7stopped §b" + service.getName() + "§h!");
        }
    }


    @Override
    public void updateService(IService service) {
        if (!this.running) {
            return;
        }

        List<IService> cachedServices = this.getCachedServices(service.getGroup());

        cachedServices.remove(this.getCachedObject(service.getName()));
        cachedServices.add(service);

        IServiceGroup serviceGroup = this.getServiceGroup(service.getGroup().getName());
        if (serviceGroup == null) {
            serviceGroup = service.getGroup();
        }
        this.cachedServices.put(serviceGroup, cachedServices);

        CloudDriver.getInstance().callEvent(new DriverEventServiceUpdate(service));

    }

    /**
     * Checks if {@link IServiceGroup} is allowed
     * to start on this receiver
     * @param serviceGroup
     * @return
     */
    public boolean isRightReceiver(IServiceGroup serviceGroup) {
        if (serviceGroup.getReceiver() == null) {
            return true;
        }
        if (getDriver().getDriverType().equals(CloudType.RECEIVER)) {
            return serviceGroup.getReceiver().equalsIgnoreCase(Receiver.getInstance().getInstance(ConfigService.class).getReceiver().getName());
        } else if (getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return serviceGroup.getReceiver().equalsIgnoreCase(Utils.INTERNAL_RECEIVER);
        }
        return true;
    }


    @Override
    public void startServices(List<IServiceGroup> serviceGroups) {
        for (IServiceGroup group : serviceGroups) {
            if (!this.isRightReceiver(group)) {
                continue;
            }
            CloudDriver.getInstance().getTemplateManager().createTemplate(group);
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

        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
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
    public IService registerService(String service, HytoraPacket packet) {

        IService iService = this.iServices.stream().filter(s -> s.getName().equalsIgnoreCase(service)).findFirst().orElse(null);

        if (iService == null) {
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
        if (CloudDriver.getInstance().getDriverType() == CloudType.RECEIVER) {
            return iService;
        }
        this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§h'§9" + iService.getGroup().getReceiver() + "§h' §7registered §b" + iService.getName() + " §7in §3" + action.getMS() + "s§h!");

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
    public void unregisterService(IService service) {
        this.iServices.removeIf(service1 -> service1.getName().equalsIgnoreCase(service.getName()));
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

        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            CloudDriver.getInstance().callEvent(new DriverEventServiceQueue(service));
        }
        this.iServices.add(service);

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
                    public void accept(IService iService) {
                        if (CloudDriver.getInstance().getDriverType() == CloudType.RECEIVER) {
                            CloudDriver.getInstance().sendPacket(new PacketReceiverNotifyStart(iService));
                        } else {
                            notifyStart(iService);
                        }
                        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
                            CloudDriver.getInstance().callEvent(new DriverEventServiceStarted(iService));
                        }
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

    public void stopService(IService service, boolean newServices) {

        try {
            this.getDriver().sendPacket(new PacketOutStopServer(service.getName()));
            try {
                this.idService.removeID(service.getGroup().getName(), service.getId());
                this.portService.removeProxyPort(service.getPort());
                this.portService.removePort(service.getPort());
            } catch (NullPointerException e) {
                //Ignoring Ubuntu Error
            }

            try {
                List<IService> services = this.cachedServices.getOrDefault(this.getServiceGroup(service.getGroup().getName()), new LinkedList<>());
                services.remove(this.getCachedObject(service.getName()));
                this.cachedServices.put(this.getServiceGroup(service.getGroup().getName()), services);
            } catch (NullPointerException e) {
                //Receiver exception
            }

            if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
                CloudDriver.getInstance().callEvent(new DriverEventServiceStop(service));
            }
            ServiceStopper serviceStopper = new ServiceStopper(service);
            serviceStopper.stop(new Consumer<IService>() {
                @Override
                public void accept(IService IService) {
                    if (!newServices) {
                        return;
                    }
                    needServices(IService.getGroup());
                }
            });
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
    public List<IService> getCachedObjects(Requestable<IService> request) {
        List<IService> list = new LinkedList<>();
        for (IService service : this.getCachedObjects()) {
            if (request.isRequested(service)) {
                list.add(service);
            }
        }
        return list;
    }


    @Override
    public void shutdownAll() {
        this.running = false;
        List<String> already = new LinkedList<>();
        for (IServiceGroup serviceGroup : new LinkedList<>(this.cachedServices.keySet())) {
            if (serviceGroup == null || serviceGroup.getName() == null) {
                continue;
            }
            try {
                if (this.getDriver().getInstance(GroupService.class) != null && this.getDriver().getInstance(GroupService.class).getGroup(serviceGroup.getName(), new LinkedList<>(this.serviceGroups)) == null) {
                    continue;
                }
                if (!already.contains(serviceGroup.getName())) {
                    already.add(serviceGroup.getName());
                    if (this.getDriver().getParent().getScreenPrinter().getScreen() == null && !this.getDriver().getParent().getScreenPrinter().isInScreen()) {
                        this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Stopping services of the group §3" + serviceGroup.getName() + " §h[§7Services: §3" + this.cachedServices.get(serviceGroup).size() + "§h]");
                    }
                }
            } catch (NullPointerException e) {
                //Ignoring
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
        AtomicInteger count = new AtomicInteger(this.getCachedServices(IServiceGroup).size());
        try {
            for (IService IService : new LinkedList<>(this.getCachedServices(IServiceGroup))) {
                this.stopService(IService, false);
                count.set(count.get() - 1);

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

            if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
                CloudDriver.getInstance().callEvent(new DriverEventServiceRegister(service));
            }
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
