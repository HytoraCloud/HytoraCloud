package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.service.ServiceInfo;
import lombok.Getter;

@Getter
public class ServiceInfoObject implements ServiceInfo {

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

}
