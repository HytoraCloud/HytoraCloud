package de.lystx.hytoracloud.driver.service;

import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.wrapped.ServiceInfoObject;
import lombok.SneakyThrows;

public interface ServiceInfo {

    @SneakyThrows
    static ServiceInfo builder() {
        return ServiceInfoObject.class.newInstance();
    }

    /**
     * Returns the motd of this service as {@link String}
     */
    String getMotd();

    /**
     * Sets the motd of this builder
     *
     * @param motd the motd
     * @return current info builder
     */
    ServiceInfo motd(String motd);

    /**
     * Returns the maximum players on this service as {@link Integer}
     */
    int getMaxPlayers();

    /**
     * Sets the maxPlayers of this builder
     *
     * @param maxPlayers the maxPlayers
     * @return current info builder
     */
    ServiceInfo maxPlayers(int maxPlayers);

    /**
     * Returns the state of this service as {@link ServiceState}
     */
    ServiceState getState();

    /**
     * Sets the state of this builder
     *
     * @param state the state
     * @return current info builder
     */
    ServiceInfo state(ServiceState state);

}
