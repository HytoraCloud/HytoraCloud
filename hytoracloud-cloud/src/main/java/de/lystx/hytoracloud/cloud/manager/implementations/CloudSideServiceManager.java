package de.lystx.hytoracloud.cloud.manager.implementations;

import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.global.InternalReceiver;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.event.events.other.*;
import de.lystx.hytoracloud.driver.utils.interfaces.Requestable;
import de.lystx.hytoracloud.driver.utils.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;

import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.service.IServiceManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter @Setter
@CloudServiceInfo(
        name = "ServiceManager",
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
     * ALl services that are not starting on this instance
     */
    private final List<String> authenticatedCache;

    /**
     * All updates that couldn't be done because the service was not registered before
     */
    private final List<IService> pendingUpdates;

    /**
     * If the manager is still running
     * or should not be allowed to start new services
     */
    private boolean running;

    public CloudSideServiceManager(List<IServiceGroup> serviceGroups) {
        this.cachedObjects = new LinkedList<>();
        this.authenticatedCache = new LinkedList<>();
        this.pendingUpdates = new LinkedList<>();
        this.otherReceiverCache = new LinkedList<>();
        CloudDriver.getInstance().registerNetworkHandler(this);

        this.running = true;

        //Cache for servers, lobbies and proxies
        List<IService> cloudServers = new LinkedList<>();
        List<IService> cloudProxies = new LinkedList<>();
        List<IService> lobbies = new LinkedList<>();

        for (IServiceGroup group : serviceGroups) {

            if (group == null) {
                continue;
            }

            //Creating template if not existent
            CloudDriver.getInstance().getTemplateManager().createTemplate(group);
            for (int i = 0; i < group.getMinServer(); i++) {
                int id = CloudDriver.getInstance().getIdService().getFreeID(group.getName());
                int port = group.getEnvironment().equals(ServerEnvironment.SPIGOT) ? CloudDriver.getInstance().getPortService().getFreePort() : CloudDriver.getInstance().getPortService().getFreeProxyPort();

                IService service = new ServiceObject(group, id, port);

                if (!group.isProcessRightReceiver()) {
                    this.otherReceiverCache.add(service);
                    continue;
                }
                if (group.getEnvironment().equals(ServerEnvironment.SPIGOT) && group.isLobby()) {
                    lobbies.add(service);
                } else if (group.getEnvironment() == ServerEnvironment.PROXY){
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
    public int getFreeId(IServiceGroup group) {
        return CloudDriver.getInstance().getIdService().getFreeID(group.getName());
    }

    @Override
    public int getFreePort(IServiceGroup group) {
        return group.getEnvironment() == ServerEnvironment.PROXY  ? CloudDriver.getInstance().getPortService().getFreeProxyPort() : CloudDriver.getInstance().getPortService().getFreePort();
    }

    @Override
    public void updateService(IService service) {

        IService cachedObject = this.getCachedObject(service.getName());
        IService receiverCache = this.otherReceiverCache.stream().filter(s -> s.getName().equalsIgnoreCase(service.getName())).findFirst().orElse(null);
        if (cachedObject == null) {
            IService pendingService = this.pendingUpdates.stream().filter(s -> s.getName().equalsIgnoreCase(service.getName())).findFirst().orElse(null);
            if (pendingService == null) {
                this.pendingUpdates.add(service);
            } else {
                int index = this.pendingUpdates.indexOf(pendingService);
                this.pendingUpdates.set(index, service);
            }
            return;
        }

        if (authenticatedCache.contains(service.getName())) {
            service.setCachedAuthenticated(true);
        } else {
            if (service.isAuthenticated()) {
                authenticatedCache.add(service.getName());
            }
        }

        try {
            int index = this.cachedObjects.indexOf(cachedObject);

            if (index == -1) {
                this.cachedObjects.removeIf(s -> s.getName().equalsIgnoreCase(service.getName()));
                this.cachedObjects.add(service);
            } else {
                this.cachedObjects.set(index, service);
            }
            if (receiverCache != null) {
                int index2 = this.otherReceiverCache.indexOf(receiverCache);
                this.otherReceiverCache.set(index2, service);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        CloudDriver.getInstance().getEventManager().callEvent(new DriverEventServiceUpdate(service));
    }

    @Override
    public List<IService> getLobbies() {
        List<IService> list = new LinkedList<>();
        for (IService service : this.getCachedObjects()) {
            if (service.getGroup().isLobby() && service.getGroup().getEnvironment().equals(ServerEnvironment.SPIGOT)) {
                list.add(service);
            }
        };
        return list;
    }

    @Override
    public void registerService(IService service) {
        if (service == null || !this.running) {
            return;
        }

        if (this.getCachedObject(service.getName()) == null) {
            this.cachedObjects.add(service);
        }

        for (IService pendingUpdate : this.pendingUpdates) {
            if (pendingUpdate.getName().equalsIgnoreCase(service.getName())) {
                this.updateService(pendingUpdate);
            }
        }

        IReceiver receiver = service.getReceiver();
        if (receiver == null) {
            return;
        }
        receiver.registerService(service);

        CloudDriver.getInstance().getEventManager().callEvent(new DriverEventServiceRegister(service));
    }

    @Override
    public void unregisterService(IService service) {
        this.authenticatedCache.remove(service.getName());
        try {
            this.cachedObjects.removeIf(service1 -> service1.getName().equalsIgnoreCase(service.getName()));
            this.otherReceiverCache.removeIf(service1 -> service1.getName().equalsIgnoreCase(service.getName()));

            CloudSideScreenService instance = (CloudSideScreenService) CloudDriver.getInstance().getScreenManager();
            instance.getCachedLines().remove(service.getName());
            instance.getMap().remove(service.getName());
        } catch (Exception e) {
            //IGnoring on shutdown
        }
    }

    @Override
    public IService getThisService() {
        return null;
    }

    @Override
    public void sync(List<IServiceGroup> groups) {
        if (this.cachedObjects == null) {
            this.cachedObjects = new LinkedList<>();
        }
        for (IServiceGroup group : groups) {
            for (IService cachedObject : this.cachedObjects) {
                int index = this.cachedObjects.indexOf(cachedObject);
                int indexReceiver = this.otherReceiverCache.indexOf(cachedObject);
                if (cachedObject.getGroup().getName().equalsIgnoreCase(group.getName())) {
                    cachedObject.setGroup(group);

                    if (index != -1) {
                        this.cachedObjects.set(index, cachedObject);
                    }
                    if (indexReceiver != -1) {
                        this.otherReceiverCache.set(indexReceiver, cachedObject);
                    }
                }
            }
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

        CloudDriver.getInstance().getEventManager().callEvent(new DriverEventServiceQueue(service));

        IReceiver receiver = service.getReceiver();
        if (receiver == null) {
            CloudDriver.getInstance().log("ERROR", "§cCouldn't start §e" + service.getName() + " §cbecause no suitable Receiver could be found!");
            return;
        }

        this.cachedObjects.add(service);
        receiver.startService(service, service1 -> {
            CloudDriver.getInstance().getEventManager().callEvent(new DriverEventServiceStarted(service));
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
        int port = serviceGroup.getEnvironment().equals(ServerEnvironment.PROXY) ? CloudDriver.getInstance().getPortService().getFreeProxyPort() : CloudDriver.getInstance().getPortService().getFreePort();
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
            CloudDriver.getInstance().getParent().getConsole().sendMessage("ERROR", "§cNo Receiver was found to stop §e" + service.getName() + " §c! Using §eInternalReceiver§c!");
            receiver = new InternalReceiver();
            ((InternalReceiver)receiver).setPacketVar(false);
        }

        CloudDriver.getInstance().getIdService().removeID(service.getGroup().getName(), service.getId());
        CloudDriver.getInstance().getPortService().removePort(service.getPort());
        CloudDriver.getInstance().getPortService().removeProxyPort(service.getPort());

        CloudDriver.getInstance().getEventManager().callEvent(new DriverEventServiceStop(service));

        IReceiver finalReceiver = receiver;
        receiver.stopService(service, s -> {
            finalReceiver.needsServices(s.getGroup());
            this.unregisterService(service);
            if (CloudDriver.getInstance().getScreenManager().getScreen() != null && CloudDriver.getInstance().getScreenManager().isInScreen()) {
                return;
            }
            CloudDriver.getInstance().getParent().getConsole().sendMessage("NETWORK", "§h'§9" + finalReceiver.getName() + "§h' §7stopped §b" + service.getName() + "§h!");
            CloudDriver.getInstance().reload();
        });
    }

    public void stopServiceForcibly(IService service, Runnable runnable) {
        IReceiver receiver = service.getReceiver();
        if (receiver == null) {
            CloudDriver.getInstance().getParent().getConsole().sendMessage("ERROR", "§cNo Receiver was found to stop §e" + service.getName() + " §c! Using §eInternalReceiver§c!");
            receiver = new InternalReceiver();
        }

        CloudDriver.getInstance().getIdService().removeID(service.getGroup().getName(), service.getId());
        CloudDriver.getInstance().getPortService().removePort(service.getPort());
        CloudDriver.getInstance().getPortService().removeProxyPort(service.getPort());
        CloudDriver.getInstance().getEventManager().callEvent(new DriverEventServiceStop(service));

        IReceiver finalReceiver = receiver;
        receiver.stopService(service, s -> {
            this.unregisterService(service);
            runnable.run();
            if (!running || CloudDriver.getInstance().getScreenManager().getScreen() != null && CloudDriver.getInstance().getScreenManager().isInScreen()) {
                return;
            }
            CloudDriver.getInstance().getParent().getConsole().sendMessage("NETWORK", "§h'§9" + finalReceiver.getName() + "§h' §7stopped §b" + service.getName() + "§h!");
            CloudDriver.getInstance().reload();
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
    public List<IService> getCachedObjects(ServerEnvironment serviceType) {
        return this.getCachedObjects(service -> service.getGroup().getEnvironment().equals(serviceType));
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

        AtomicInteger count = new AtomicInteger(CloudDriver.getInstance().getGroupManager().getCachedObjects().size());
        List<String> already = new LinkedList<>();
        for (IServiceGroup cachedGroup : CloudDriver.getInstance().getGroupManager().getCachedObjects()) {
            if (this.getCachedObjects(cachedGroup).size() != 0) {
                if (!already.contains(cachedGroup.getName())) {
                    already.add(cachedGroup.getName());
                    if (this.getDriver().getScreenManager().getScreen() == null && !this.getDriver().getScreenManager().isInScreen()) {
                        this.getDriver().getParent().getConsole().sendMessage("NETWORK", "§7Stopping services of the group §3" + cachedGroup.getName() + " §h[§7Services: §3" + this.getCachedObjects(cachedGroup).size() + "§h]");
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
        return new LinkedList<>(this.cachedObjects).stream().filter(service -> service.getGroup().getName().equalsIgnoreCase(serviceGroup.getName())).collect(Collectors.toList());
    }

    @Override
    public IService getCachedObject(String name) {
        return new LinkedList<>(this.cachedObjects).stream().filter(service -> service.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IService getCachedObject(UUID uniqueId) {
        return new LinkedList<>(this.cachedObjects).stream().filter(service -> service.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public void getObjectAsync(String name, Consumer<IService> consumer) {
        consumer.accept(this.getCachedObject(name));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<IService> consumer) {
        consumer.accept(this.getCachedObject(uniqueId));
    }

    @Override
    public DriverQuery<IService> getObjectSync(String name) {
        return DriverQuery.dummy("SERVICE_GET_SYNC_NAME", this.getCachedObject(name));
    }

    @Override
    public DriverQuery<IService> getObjectSync(UUID uniqueId) {
        return DriverQuery.dummy("SERVICE_GET_SYNC_UUID", this.getCachedObject(uniqueId));
    }

    @Override
    public void setCachedObjects(List<IService> cachedObjects) {
        this.cachedObjects = cachedObjects;
    }

    @Override
    public ServerEnvironment getCurrentEnvironment() {
        return ServerEnvironment.CLOUD;
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
