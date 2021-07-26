package de.lystx.hytoracloud.driver.commons.service;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ServiceBuilder {

    /**
     * The group of the service
     */
    private final IServiceGroup group;

    /**
     * The id of the service
     */
    private int id;

    /**
     * The port of the service
     */
    private int port;

    /**
     * The properties of the service
     */
    private PropertyObject properties;

    /**
     * The memory of the service
     */
    private int memory;

    /**
     * The maxPlayers of the service
     */
    private int maxPlayers;

    /**
     * The template of the service
     */
    private String template;

    /**
     * If the service should stop if
     * its empty for a given time
     */
    private int timeOutIfNoPlayers;

    public ServiceBuilder id(int id) {
        this.id = id;
        return this;
    }

    public ServiceBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ServiceBuilder properties(PropertyObject properties) {
        this.properties = properties;
        return this;
    }

    public ServiceBuilder memory(int mb) {
        this.memory = mb;
        return this;
    }

    public ServiceBuilder maxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public ServiceBuilder template(String template) {
        this.template = template;
        return this;
    }

    public ServiceBuilder timeOutIfNoPlayers(int seconds) {
        this.timeOutIfNoPlayers = seconds;
        return this;
    }

    /**
     * Starts the {@link IService}
     *
     * @return started service
     */
    public IService startService() {

        if (this.id == 0) {
            this.id = CloudDriver.getInstance().getServiceManager().getFreeId(this.group);
        }

        if (this.port == 0) {
            this.port = CloudDriver.getInstance().getServiceManager().getFreePort(this.group);
        }

        IService service = new ServiceObject(this.group, this.id, this.port);

        if (this.properties == null) {
            this.properties = new PropertyObject();
        }

        this.properties.append("_serviceBuilder",
                new PropertyObject()
                    .append("maxPlayers", this.maxPlayers == 0 ? this.group.getMaxPlayers() : this.maxPlayers)
                    .append("memory", this.memory == 0 ? this.group.getMemory() : this.memory)
                    .append("template", this.template == null ? this.group.getCurrentTemplate().getName() : this.template)
                    .append("timeOut", this.timeOutIfNoPlayers)
        );
        service.setProperties(this.properties);

        service.bootstrap();
        return service;
    }
}