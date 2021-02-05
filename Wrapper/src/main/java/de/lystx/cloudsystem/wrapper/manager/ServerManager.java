package de.lystx.cloudsystem.wrapper.manager;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.server.other.process.ServiceProviderStart;
import de.lystx.cloudsystem.wrapper.Wrapper;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class ServerManager {

    private final Wrapper wrapper;
    private List<Service> services;
    private final ServiceProviderStart serviceProviderStart;

    public ServerManager(Wrapper wrapper) {
        this.services = new LinkedList<>();
        this.wrapper = wrapper;
        this.serviceProviderStart = new ServiceProviderStart(wrapper, wrapper.getFileManager().getTemplatesDirectory(), wrapper.getFileManager().getDynamicServerDirectory(), wrapper.getFileManager().getStaticServerDirectory(), wrapper.getFileManager().getSpigotPluginsDirectory(), wrapper.getFileManager().getBungeeCordPluginsDirectory(), wrapper.getFileManager().getGlobalDirectory(), wrapper.getFileManager().getVersionsDirectory());
    }

    public void startService(Service service, SerializableDocument document) {
        this.serviceProviderStart.autoStartService(service, document);
    }


    public void setServices(List<Service> services) {
        for (Service service : services) {
            if (this.getService(service.getName()) == null || !this.services.contains(this.getService(service.getName()))) {
                this.startService(service, null);
            }
        }
        this.services = services;
    }

    public Service getService(String name) {
        return this.services.stream().filter(service -> service.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
