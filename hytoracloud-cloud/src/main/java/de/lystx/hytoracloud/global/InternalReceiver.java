package de.lystx.hytoracloud.global;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.global.process.ServiceStarter;
import de.lystx.hytoracloud.global.process.ServiceStopper;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver.*;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.utils.other.CloudMap;
import de.lystx.hytoracloud.driver.utils.other.Action;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.Getter;
import lombok.Setter;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter @Setter
public class InternalReceiver extends ReceiverObject implements IReceiver {

    private static final long serialVersionUID = -4716682467066200508L;

    private final Map<String, Action> actions;
    private final List<IService> services;

    @Setter @Getter
    private boolean packetVar;


    public InternalReceiver() {
        super("127.0.0.1", CloudDriver.getInstance().getConfigManager().getNetworkConfig().getPort(), Utils.INTERNAL_RECEIVER, UUID.randomUUID(), 1024L, true, new InetSocketAddress("127.0.0.1", CloudDriver.getInstance().getConfigManager().getNetworkConfig().getPort()).getAddress());

        this.authenticated = true;
        this.packetVar = false;

        this.actions = new CloudMap<>();
        this.services = new LinkedList<>();

    }

    @Override
    public IReceiver update() {
        if (this.isPacketVar()) {
            CloudDriver.getInstance().sendPacket(new PacketReceiverUpdate(this));
        }
        //Not required for internal receiver
        return this;
    }

    @Override
    public long getMemory() {
        if (this.isPacketVar()) {
            PacketReceiverMemoryUsage packetReceiverMemoryUsage = new PacketReceiverMemoryUsage(this);
            Component component = packetReceiverMemoryUsage.toReply(CloudDriver.getInstance().getConnection());
            return component == null ? -1L : component.get("memory");
        }
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
    }

    @Override
    public List<ICloudPlayer> getPlayers() {
        return CloudDriver.getInstance().getPlayerManager().getCachedObjects().stream().filter(iCloudPlayer -> iCloudPlayer.getService().getReceiver().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
    }

    @Override
    public void startService(IService service, Consumer<IService> consumer) {
        if (this.isPacketVar()) {
            IService finalService1 = service;
            CloudDriver.getInstance().sendPacket(new PacketReceiverStartService(this, finalService1), component -> consumer.accept(finalService1));
            return;
        }
        IServiceGroup serviceGroup = service.getGroup();

        if (serviceGroup.getMaxServer() != -1 && serviceGroup.getServices().size() >= serviceGroup.getMaxServer()) {
            CloudDriver.getInstance().messageCloud("INFO", "§cThe service §e" + service.getName() + " §cwasn't started because there are to much §cservices of this group online!");
            return;
        }

        if (service.getPort() <= 0) {
            int port = service.getGroup().getEnvironment().equals(ServerEnvironment.PROXY) ? CloudDriver.getInstance().getPortService().getFreeProxyPort() : CloudDriver.getInstance().getPortService().getFreePort();
            int id = CloudDriver.getInstance().getIdService().getFreeID(serviceGroup.getName());
            service = new ServiceObject(serviceGroup, id, port);
        }
        service.setProperties((service.getProperties() == null ? new PropertyObject() : service.getProperties()));

        this.actions.put(service.getName(), new Action());
        this.services.add(service);

        IService finalService = service;
        CloudDriver.getInstance().executeIf(() -> {
            ServiceStarter serviceStarter = new ServiceStarter(finalService);
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
                            if (CloudDriver.getInstance().getScreenManager().getScreen() != null && CloudDriver.getInstance().getScreenManager().isInScreen()) {
                                return;
                            }
                            CloudDriver.getInstance().messageCloud("NETWORK", "§h'§9" + getName() + "§h' §7queued §b" + iService.getName() + " §h[§7Port: §b" + iService.getPort() + " §7| §7Mode: §b" + (iService.getGroup().isDynamic() ? "DYNAMIC" : "STATIC") + "_" + iService.getGroup().getEnvironment() + "§h]");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, () -> CloudDriver.getInstance().getServiceManager() != null);
    }

    @Override
    public void registerService(IService service) {

        if (this.isPacketVar()) {
            CloudDriver.getInstance().getReceiverManager().sendPacket(this, new PacketReceiverRegisterService(this, service));
            return;
        }
        Action action = this.actions.getOrDefault(service.getName(), new Action());
        this.actions.remove(service.getName());

        //If in screen not sending message!
        if (CloudDriver.getInstance().getScreenManager().getScreen() != null && CloudDriver.getInstance().getScreenManager().isInScreen()) {
            return;
        }
        CloudDriver.getInstance().messageCloud("NETWORK", "§h'§9" + getName() + "§h' §7registered §b" + service.getName() + " §7in §3" + action.getMS() + "s§h!");

    }

    @Override
    public void stopService(IService service, Consumer<IService> consumer) {

        if (this.isPacketVar()) {
            CloudDriver.getInstance().sendPacket(new PacketReceiverStopService(this, service), component -> consumer.accept(service));
            return;
        }
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().sendPacket(new PacketOutStopServer(service.getName()));
        }

        CloudDriver.getInstance().getIdService().removeID(service.getGroup().getName(), service.getId());
        CloudDriver.getInstance().getPortService().removeProxyPort(service.getPort());
        CloudDriver.getInstance().getPortService().removePort(service.getPort());

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
        if (this.isPacketVar()) {
            CloudDriver.getInstance().getReceiverManager().sendPacket(this, new PacketReceiverNeedServices(this, serviceGroup));
            return true;
        }
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if (this.getServices(serviceGroup).size() < serviceGroup.getMinServer()) {
                for (int i = this.getServices(serviceGroup).size(); i < serviceGroup.getMinServer(); i++) {
                    int id = CloudDriver.getInstance().getIdService().getFreeID(serviceGroup.getName());
                    int port = serviceGroup.getEnvironment().equals(ServerEnvironment.PROXY) ? CloudDriver.getInstance().getPortService().getFreeProxyPort() : CloudDriver.getInstance().getPortService().getFreePort();
                    this.startService(new ServiceObject(serviceGroup, id, port), service -> {});
                }
            }
        }, 3L);
        return true;
    }
}
