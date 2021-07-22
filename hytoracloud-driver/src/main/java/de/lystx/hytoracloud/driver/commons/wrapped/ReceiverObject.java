package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter @RequiredArgsConstructor
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
    private final String name;

    /**
     * The uuid of it
     */
    private final UUID uniqueId;

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
    public void startService(IService service) {

    }

    @Override
    public void stopService(IService service) {

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
