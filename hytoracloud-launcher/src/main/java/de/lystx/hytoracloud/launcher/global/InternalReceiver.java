package de.lystx.hytoracloud.launcher.global;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.ServiceStarter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.ServiceStopper;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceQueue;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStarted;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStop;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.PropertyObject;
import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.utils.Action;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.IDService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.PortService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter @Setter
public class InternalReceiver implements IReceiver {

    private static final long serialVersionUID = -4716682467066200508L;

    private final String host;
    private final int port;
    private InetAddress address;

    private String name;
    private final UUID uniqueId;
    private final long maxMemory;

    private boolean authenticated;

    private final PortService portService;
    private final IDService idService;

    private final Map<String, Action> actions;

    private final List<IService> services;

    @SneakyThrows
    public InternalReceiver() {
        this.host = "127.0.0.1";
        this.port = CloudDriver.getInstance().getNetworkConfig().getPort();
        this.address = InetAddress.getLocalHost();

        this.name = Utils.INTERNAL_RECEIVER;
        this.uniqueId = UUID.randomUUID();
        this.maxMemory = 1024;
        this.authenticated = true;

        this.actions = new CloudMap<>();
        this.services = new LinkedList<>();
        this.portService = new PortService(CloudDriver.getInstance().getNetworkConfig().getProxyStartPort(), CloudDriver.getInstance().getNetworkConfig().getServerStartPort());
        this.idService = new IDService();
    }

    @Override
    public IReceiver update() {
        //Not required for internal receiver
        return this;
    }

    @Override
    public long getMemory() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
    }

    @Override
    public List<ICloudPlayer> getPlayers() {
        return new LinkedList<>();
    }

    @Override
    public void startService(IService service, Consumer<IService> consumer) {
        IServiceGroup serviceGroup = service.getGroup();

        if (serviceGroup.getMaxServer() != -1 && CloudDriver.getInstance().getServiceManager().getCachedObjects(serviceGroup).size() >= serviceGroup.getMaxServer()) {
            CloudDriver.getInstance().messageCloud("INFO", "§cThe service §e" + service.getName() + " §cwasn't started because there are to much §cservices of this group online!");
            return;
        }

        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().callEvent(new DriverEventServiceQueue(service));
        }

        if (service.getPort() <= 0) {
            int port = service.getGroup().getType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
            int id = this.idService.getFreeID(serviceGroup.getName());
            service = new ServiceObject(serviceGroup, id, port);
        }
        service.setProperties((service.getProperties() == null ? new PropertyObject() : service.getProperties()));

        this.actions.put(service.getName(), new Action());
        this.services.add(service);

        ServiceStarter serviceStarter = new ServiceStarter(service);

        if (serviceStarter.checkForSpigot()) {
            try {
                serviceStarter.copyFiles();
                serviceStarter.createProperties();
                serviceStarter.createCloudFiles();
                serviceStarter.start(new Consumer<IService>() {
                    @Override
                    public void accept(IService iService) {
                        consumer.accept(iService);

                        //If in screen not sending message!
                        if (CloudDriver.getInstance().getParent().getScreenPrinter().getScreen() != null && CloudDriver.getInstance().getParent().getScreenPrinter().isInScreen()) {
                            return;
                        }
                        CloudDriver.getInstance().messageCloud("NETWORK", "§h'§9" + getName() + "§h' §7queued §b" + iService.getName() + " §h[§7Port: §b" + iService.getPort() + " §7| §7Mode: §b" + (iService.getGroup().isDynamic() ? "DYNAMIC" : "STATIC") + "_" + iService.getGroup().getType() + "§h]");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void registerService(IService service) {

        Action action = this.actions.getOrDefault(service.getName(), new Action());
        this.actions.remove(service.getName());

        //If in screen not sending message!
        if (CloudDriver.getInstance().getParent().getScreenPrinter().getScreen() != null && CloudDriver.getInstance().getParent().getScreenPrinter().isInScreen()) {
            return;
        }
        CloudDriver.getInstance().messageCloud("NETWORK", "§h'§9" + getName() + "§h' §7registered §b" + service.getName() + " §7in §3" + action.getMS() + "s§h!");

    }

    @Override
    public void stopService(IService service, Consumer<IService> consumer) {

        CloudDriver.getInstance().sendPacket(new PacketOutStopServer(service.getName()));

        this.idService.removeID(service.getGroup().getName(), service.getId());
        this.portService.removeProxyPort(service.getPort());
        this.portService.removePort(service.getPort());

        this.services.removeIf(service1 -> service1.getName().equalsIgnoreCase(service.getName()));

        try {
            new ServiceStopper(service).stop(consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<IService> getServices(IServiceGroup group) {
        return this.services.stream().filter(service -> service.getGroup().getName().equalsIgnoreCase(group.getName())).collect(Collectors.toList());
    }

    @Override
    public boolean needsServices(IServiceGroup serviceGroup) {
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if (this.getServices(serviceGroup).size() < serviceGroup.getMinServer()) {
                for (int i = this.getServices(serviceGroup).size(); i < serviceGroup.getMinServer(); i++) {
                    int id = idService.getFreeID(serviceGroup.getName());
                    int port = serviceGroup.getType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
                    this.startService(new ServiceObject(serviceGroup, id, port), service -> {});
                }
            }
        }, 3L);
        return true;
    }
}
