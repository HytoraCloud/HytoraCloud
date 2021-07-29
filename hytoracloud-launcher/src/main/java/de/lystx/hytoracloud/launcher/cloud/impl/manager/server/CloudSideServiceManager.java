package de.lystx.hytoracloud.launcher.cloud.impl.manager.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.commons.events.other.*;
import de.lystx.hytoracloud.driver.commons.interfaces.Requestable;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.interfaces.ScheduledForVersion;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.*;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;

import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.IServiceManager;
import de.lystx.hytoracloud.launcher.global.InternalReceiver;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.packet.response.Response;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    /**
     * All cached {@link IService}s
     */
    private List<IService> cachedObjects;

    /**
     * ALl services that are not starting on this instance
     */
    private final List<IService> otherReceiverCache;

    /**
     * If the manager is still running
     * or should not be allowed to start new services
     */
    private boolean running;

    public CloudSideServiceManager(List<IServiceGroup> serviceGroups) {
        this.cachedObjects = new LinkedList<>();
        this.otherReceiverCache = new LinkedList<>();
        CloudDriver.getInstance().registerNetworkHandler(this);

        this.running = true;


        //Cache for servers, lobbies and proxies
        List<IService> cloudServers = new LinkedList<>();
        List<IService> cloudProxies = new LinkedList<>();
        List<IService> lobbies = new LinkedList<>();

        for (IServiceGroup group : serviceGroups) {

            //Creating template if not existent
            CloudDriver.getInstance().getTemplateManager().createTemplate(group);
            for (int i = 0; i < group.getMinServer(); i++) {
                int id = CloudDriver.getInstance().getIdService().getFreeID(group.getName());
                int port = group.getType().equals(ServiceType.SPIGOT) ? CloudDriver.getInstance().getPortService().getFreePort() : CloudDriver.getInstance().getPortService().getFreeProxyPort();

                IService service = new ServiceObject(group, id, port);

                if (!group.isProcessRightReceiver()) {
                    this.otherReceiverCache.add(service);
                    continue;
                }
                if (group.getType().equals(ServiceType.SPIGOT) && group.isLobby()) {
                    lobbies.add(service);
                } else if (group.getType() == ServiceType.PROXY){
                    cloudProxies.add(service);
                } else {
                    cloudServers.add(service);
                }
            }
        }

        //Sorting services by id
        cloudServers.sort(Comparator.comparingInt(IService::getId));
        cloudProxies.sort(Comparator.comparingInt(IService::getId));
        lobbies.sort(Comparator.comparingInt(IService::getId));

        //Starting proxy first
        for (IService proxy : cloudProxies) {
            this.startService(proxy);
        }

        //Starting lobbies after
        for (IService lobby : lobbies) {
            this.startService(lobby);
        }

        //Starting non-lobbies last
        for (IService server : cloudServers) {
            this.startService(server);
        }
    }

    @Override
    public void updateGroup(IServiceGroup group) {
        IServiceGroup serviceGroup = this.getServiceGroup(group.getName());

        List<IService> list = this.getCachedObjects(serviceGroup);

        for (IService service : this.cachedObjects) {
            if (service.getGroup().getName().equalsIgnoreCase(group.getName())) {
                int i = cachedObjects.indexOf(service);
                service.setGroup(serviceGroup);
                this.cachedObjects.set(i, service);
            }
        }

    }

    @Override
    public int getFreeId(IServiceGroup group) {
        return CloudDriver.getInstance().getIdService().getFreeID(group.getName());
    }

    @Override
    public int getFreePort(IServiceGroup group) {
        return group.getType() == ServiceType.PROXY  ? CloudDriver.getInstance().getPortService().getFreeProxyPort() : CloudDriver.getInstance().getPortService().getFreePort();
    }

    @Override
    public void updateService(IService service) {

        this.cachedObjects = new LinkedList<>(this.cachedObjects);

        try {
            this.cachedObjects.removeIf(s -> s.getName().equalsIgnoreCase(service.getName()));
            this.otherReceiverCache.removeIf(s -> s.getName().equalsIgnoreCase(service.getName()));
            this.otherReceiverCache.add(service);
            this.cachedObjects.add(service);
        } catch (ConcurrentModificationException e) {
            //Ignoring ex
        }


        CloudDriver.getInstance().callEvent(new DriverEventServiceUpdate(service));
    }

    @Override
    public List<IService> getLobbies() {
        List<IService> list = new LinkedList<>();
        for (IService service : this.getCachedObjects()) {
            if (service.getGroup().isLobby() && service.getGroup().getType().equals(ServiceType.SPIGOT)) {
                list.add(service);
            }
        };
        return list;
    }

    @Override
    public void registerService(IService service) {
        if (this.getCachedObject(service.getName()) == null) {
            this.cachedObjects.add(service);
        }
        if (service == null || !this.running) {
            return;
        }

        IReceiver receiver = service.getReceiver();
        if (receiver == null) {
            return;
        }
        receiver.registerService(service);
    }

    @Override
    public void unregisterService(IService service) {
        try {
            this.cachedObjects.removeIf(service1 -> service1.getName().equalsIgnoreCase(service.getName()));
            this.otherReceiverCache.removeIf(service1 -> service1.getName().equalsIgnoreCase(service.getName()));

            ServiceOutputService instance = CloudDriver.getInstance().getInstance(ServiceOutputService.class);
            instance.getCachedLines().remove(service.getName());
            instance.getMap().remove(service.getName());
        } catch (Exception e) {
            //IGnoring on shutdown
        }
    }

    @Override
    public void startService(IService service) {
        if (!this.running) {
            return;
        }

        if (this.getCachedObject(service.getName()) != null) {
            return;
        }

        IReceiver receiver = service.getReceiver();
        if (receiver == null) {
            CloudDriver.getInstance().log("ERROR", "§cCouldn't start §e" + service.getName() + " §cbecause no suitable Receiver could be found!");
            return;
        }

        this.cachedObjects.add(service);
        receiver.startService(service, service1 -> {
            CloudDriver.getInstance().callEvent(new DriverEventServiceStarted(service));
        });
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

        int id = CloudDriver.getInstance().getIdService().getFreeID(serviceGroup.getName());
        int port = serviceGroup.getType().equals(ServiceType.PROXY) ? CloudDriver.getInstance().getPortService().getFreeProxyPort() : CloudDriver.getInstance().getPortService().getFreePort();
        IService service = new ServiceObject(serviceGroup, id, port);
        service.setProperties(properties);
        this.startService(service);
    }


    @Override
    public void stopService(IService service) {
        if (service == null) {
            return;
        }
        IReceiver receiver = service.getReceiver();
        if (receiver == null) {
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("ERROR", "§cNo Receiver was found to stop §e" + service.getName() + " §c! Using §eInternalReceiver§c!");
            receiver = new InternalReceiver();
        }

        CloudDriver.getInstance().getIdService().removeID(service.getGroup().getName(), service.getId());
        CloudDriver.getInstance().getPortService().removePort(service.getPort());
        CloudDriver.getInstance().getPortService().removeProxyPort(service.getPort());
        CloudDriver.getInstance().callEvent(new DriverEventServiceStop(service));

        IReceiver finalReceiver = receiver;
        receiver.stopService(service, s -> {
            finalReceiver.needsServices(s.getGroup());
            this.unregisterService(service);
            if (CloudDriver.getInstance().getParent().getScreenPrinter().getScreen() != null && CloudDriver.getInstance().getParent().getScreenPrinter().isInScreen()) {
                return;
            }
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "§h'§9" + finalReceiver.getName() + "§h' §7stopped §b" + service.getName() + "§h!");

        });
    }

    public void stopServiceForcibly(IService service, Runnable runnable) {
        IReceiver receiver = service.getReceiver();
        if (receiver == null) {
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("ERROR", "§cNo Receiver was found to stop §e" + service.getName() + " §c! Using §eInternalReceiver§c!");
            receiver = new InternalReceiver();
        }

        CloudDriver.getInstance().getIdService().removeID(service.getGroup().getName(), service.getId());
        CloudDriver.getInstance().getPortService().removePort(service.getPort());
        CloudDriver.getInstance().getPortService().removeProxyPort(service.getPort());
        CloudDriver.getInstance().callEvent(new DriverEventServiceStop(service));

        IReceiver finalReceiver = receiver;
        receiver.stopService(service, s -> {
            this.unregisterService(service);
            runnable.run();
            if (!running || CloudDriver.getInstance().getParent().getScreenPrinter().getScreen() != null && CloudDriver.getInstance().getParent().getScreenPrinter().isInScreen()) {
                return;
            }
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "§h'§9" + finalReceiver.getName() + "§h' §7stopped §b" + service.getName() + "§h!");
        });
    }
    @Override
    public void stopServiceForcibly(IService service) {
        this.stopServiceForcibly(service, () -> {});
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
    public void shutdownAll(Runnable runnable) {

        this.running = false;

        AtomicInteger count = new AtomicInteger(this.getCachedGroups().size());
        List<String> already = new LinkedList<>();
        for (IServiceGroup cachedGroup : this.getCachedGroups()) {
            if (this.getCachedObjects(cachedGroup).size() != 0) {
                if (!already.contains(cachedGroup.getName())) {
                    already.add(cachedGroup.getName());
                    if (this.getDriver().getParent().getScreenPrinter().getScreen() == null && !this.getDriver().getParent().getScreenPrinter().isInScreen()) {
                        this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Stopping services of the group §3" + cachedGroup.getName() + " §h[§7Services: §3" + this.getCachedObjects(cachedGroup).size() + "§h]");
                    }
                }
            }
            this.shutdownAll(cachedGroup, false, () -> {
                count.getAndDecrement();
                if (count.get() <= 0) {
                    runnable.run();
                }
            });
        }
    }


    @Override
    public void shutdownAll(IServiceGroup serviceGroup) {
        this.shutdownAll(serviceGroup, true, () -> {});
    }

    public void shutdownAll(IServiceGroup serviceGroup, boolean newOnes, Runnable runnable) {
        AtomicInteger count = new AtomicInteger();
        for (IService service : this.getCachedObjects(serviceGroup)) {
            this.stopServiceForcibly(service, () -> {
                count.getAndDecrement();
                if (count.get() <= 0) {
                    runnable.run();
                    if (newOnes) {
                        service.getReceiver().needsServices(serviceGroup);
                    }
                }
            });

        }
    }

    @Override
    public List<IService> getCachedObjects(IServiceGroup serviceGroup) {
        return this.cachedObjects.stream().filter(service -> service.getGroup().getName().equalsIgnoreCase(serviceGroup.getName())).collect(Collectors.toList());
    }

    @Override
    public IService getCachedObject(String name) {
        return this.cachedObjects.stream().filter(service -> service.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IService getCachedObject(UUID uniqueId) {
        return this.cachedObjects.stream().filter(service -> service.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override @ScheduledForVersion("1.9")
    public void getObjectAsync(String name, Consumer<IService> consumer) {
    }

    @Override @ScheduledForVersion("1.9")
    public void getObjectAsync(UUID uniqueId, Consumer<IService> consumer) {
    }

    @Override @ScheduledForVersion("1.9")
    public Response<IService> getObjectSync(String name) {
        return null;
    }

    @Override @ScheduledForVersion("1.9")
    public Response<IService> getObjectSync(UUID uniqueId) {
        return null;
    }

    @Override
    public void setCachedObjects(List<IService> cachedObjects) {
        this.cachedObjects = cachedObjects;
    }

    @Override
    public IServiceGroup getServiceGroup(String name) {
        for (IService cachedObject : this.cachedObjects) {
            if (cachedObject.getGroup().getName().equalsIgnoreCase(name)) {
                return cachedObject.getGroup();
            }
        }
        return null;
    }

    @Override
    public List<IServiceGroup> getCachedGroups() {
        List<IServiceGroup> list = new LinkedList<>();
        for (IService cachedObject : this.cachedObjects) {
            list.add(cachedObject.getGroup());
        }
        return list;
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
