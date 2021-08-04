package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.service.ServiceInfo;
import lombok.Getter;

@Getter
public class ServiceInfoObject extends WrappedObject<ServiceInfo, ServiceInfoObject> implements ServiceInfo {

    /**
     * The motd
     */
    private String motd = "A default Hytora Service";

    /**
     * The maxmimum players
     */
    private int maxPlayers = 20;

    /**
     * The state
     */
    private ServiceState state = ServiceState.BOOTING;

    @Override
    public ServiceInfo motd(String motd) {
        this.motd = motd;
        return this;
    }

    @Override
    public ServiceInfo maxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    @Override
    public ServiceInfo state(ServiceState state) {
        this.state = state;
        return this;
    }

    @Override
    Class<ServiceInfoObject> getWrapperClass() {
        return ServiceInfoObject.class;
    }

    @Override
    Class<ServiceInfo> getInterface() {
        return ServiceInfo.class;
    }
}
