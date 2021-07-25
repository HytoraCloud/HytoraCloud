package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.packets.receiver.*;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter @AllArgsConstructor
@Setter
public class ReceiverObject extends WrappedObject<IReceiver, ReceiverObject> implements IReceiver {

    private static final long serialVersionUID = -645032346931445287L;

    /**
     * The host
     */
    private final String host;

    /**
     * The port
     */
    private final int port;

    /**
     * The name
     */
    private String name;

    /**
     * The uuid of it
     */
    private final UUID uniqueId;

    /**
     * The maximum memory usage
     */
    private final long maxMemory;

    /**
     * If this receiver is authenticated
     */
    private boolean authenticated;

    @Setter
    private InetAddress address;

    @Override
    public List<IService> getServices() {
        return CloudDriver.getInstance().getServiceManager().getCachedObjects(service -> service.getGroup().getReceiver().equalsIgnoreCase(getName()));
    }

    @Override
    public List<ICloudPlayer> getPlayers() {
        return CloudDriver.getInstance().getPlayerManager().getCachedObjects().stream().filter(players -> players.getService().getGroup().getReceiver().equalsIgnoreCase(getName())).collect(Collectors.toList());
    }

    @Override
    public void startService(IService service, Consumer<IService> consumer) {
        CloudDriver.getInstance().sendPacket(new PacketReceiverStartService(this, service), component -> consumer.accept(service));
    }

    @Override
    public void registerService(IService service) {
        CloudDriver.getInstance().getReceiverManager().sendPacket(this, new PacketReceiverRegisterService(this, service));
    }

    @Override
    public void stopService(IService service, Consumer<IService> consumer) {
        CloudDriver.getInstance().sendPacket(new PacketReceiverStopService(this, service), component -> consumer.accept(service));
    }

    @Override
    public boolean needsServices(IServiceGroup serviceGroup) {
        CloudDriver.getInstance().getReceiverManager().sendPacket(this, new PacketReceiverNeedServices(this, serviceGroup));
        return true;
    }

    @Override
    public IReceiver update() {
        CloudDriver.getInstance().sendPacket(new PacketReceiverUpdate(this));
        return this;
    }

    @Override
    public long getMemory() {
        PacketReceiverMemoryUsage packetReceiverMemoryUsage = new PacketReceiverMemoryUsage(this);
        Component component = packetReceiverMemoryUsage.toReply(CloudDriver.getInstance().getConnection());
        return component == null ? -1L : component.get("memory");
    }

    @Override
    public Class<ReceiverObject> getWrapperClass() {
        return ReceiverObject.class;
    }

    @Override
    Class<IReceiver> getInterface() {
        return IReceiver.class;
    }
}
