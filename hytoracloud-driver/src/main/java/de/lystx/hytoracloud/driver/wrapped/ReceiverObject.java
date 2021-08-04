package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.packets.receiver.*;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


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
    protected final String host;

    /**
     * The port
     */
    protected final int port;

    /**
     * The name
     */
    protected String name;

    /**
     * The uuid of it
     */
    protected final UUID uniqueId;

    /**
     * The maximum memory usage
     */
    protected final long maxMemory;

    /**
     * If this receiver is authenticated
     */
    protected boolean authenticated;

    @Setter
    protected InetAddress address;

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

        DriverRequest<Long> request = DriverRequest.create("RECEIVER_MEMORY_USAGE", "CLOUD", Long.class);
        request.append("name", this.getName());
        return request.execute().setTimeOut(30, -1L).pullValue();
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
