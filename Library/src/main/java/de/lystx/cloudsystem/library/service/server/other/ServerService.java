package de.lystx.cloudsystem.library.service.server.other;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStartedServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.manager.IDService;
import de.lystx.cloudsystem.library.service.server.other.manager.PortService;
import de.lystx.cloudsystem.library.service.server.other.process.ServiceProviderStart;
import de.lystx.cloudsystem.library.service.server.other.process.ServiceProviderStop;
import de.lystx.cloudsystem.library.utils.Action;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


@Getter @Setter
public class ServerService extends CloudService {

    private final Map<ServiceGroup, List<Service>> services;
    private List<Service> globalServices;
    private final Map<String, Action> actions;

    private final List<Service> cloudServers;
    private final List<Service> cloudProxies;
    private boolean startUp;

    private final IDService idService;
    private final PortService portService;

    private final ServiceProviderStart providerStart;
    private final ServiceProviderStop providerStop;


    public ServerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.actions = new HashMap<>();
        this.services = new HashMap<>();
        this.globalServices = new LinkedList<>();

        this.startUp = false;
        this.cloudServers = new LinkedList<>();
        this.cloudProxies = new LinkedList<>();

        this.idService = new IDService();
        this.portService = new PortService();

        this.providerStart = new ServiceProviderStart(cloudLibrary, this);
        this.providerStop = new ServiceProviderStop(cloudLibrary, this);

        this.startServices();
    }

    public void updateGroup(ServiceGroup group, ServiceGroup newGroup) {
        List<Service> list = this.getServices(group);
        this.services.remove(this.getGroup(group.getName()));
        this.services.put(newGroup, list);
    }

    public void notifyStart(Service service) {
        List<Service> list = this.getServices(service.getServiceGroup());
        if (!list.contains(service)) {
            list.add(service);
            this.services.put(service.getServiceGroup(), list);
        }
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§7The service §b" + service.getName() + " §7has §astarted §7| §bID " + service.getServiceID() + " §7| §bPort " + service.getPort() + " §7| §bGroup " + service.getServiceGroup().getName() + " §7| §bType " + service.getServiceGroup().getServiceType().name() );

    }

    public void notifyStop(Service service) {

        List<Service> services = this.services.get(service.getServiceGroup());
        Service remove = this.getService(service.getName());
        if (services == null) services = new LinkedList<>();
        services.remove(remove);
        this.services.put(service.getServiceGroup(), services);
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§7The service §b" + service.getName() + " §7has §4stopped §7| §bGroup " + service.getServiceGroup().getName() + " §7| §bType " + service.getServiceGroup().getServiceType().name());
    }

    public void updateService(Service service, ServiceState state) {
        List<Service> services = this.getServices(service.getServiceGroup());
        services.remove(service);
        Service newService = new Service(service.getName(), service.getUniqueId(), service.getServiceGroup(), service.getServiceID(), service.getPort(), state);
        services.add(newService);
        this.services.put(service.getServiceGroup(), services);
    }

    public void startServices() {
        for (ServiceGroup serviceGroup : this.getCloudLibrary().getService(GroupService.class).getGroups()) {
            for (int i = 0; i < serviceGroup.getMinServer(); i++) {
                if (serviceGroup.getServiceType().equals(ServiceType.SPIGOT)) {
                    int id = this.idService.getFreeID(serviceGroup.getName());
                    if (id == 0) {
                        id++;
                    }
                    int port = this.portService.getFreePort();
                    cloudServers.add(new Service(serviceGroup.getName() + "-" + id, UUID.randomUUID(), serviceGroup, id, port, ServiceState.LOBBY));
                } else {
                    int id = this.idService.getFreeID(serviceGroup.getName());
                    if (id == 0) {
                        id++;
                    }
                    int port = this.portService.getFreeProxyPort();
                    cloudProxies.add(new Service(serviceGroup.getName() + "-" + id, UUID.randomUUID(), serviceGroup, id, port, ServiceState.LOBBY));
                }
            }
        }
        this.cloudServers.sort(Comparator.comparingInt(Service::getServiceID));
        this.cloudProxies.sort(Comparator.comparingInt(Service::getServiceID));

        for (Service proxy : cloudProxies) {
            this.startService(proxy.getServiceGroup(), proxy);
        }
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§7SpigotServices will §astart §7when the first §eProxyService §7is online§7!");
    }

    public void setStartUp(boolean startUp) {

        if (startUp && !this.startUp) {
            this.startUp = true;
            this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
                for (Service service : this.cloudServers) {
                    this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
                        this.startService(service.getServiceGroup(), service);
                    }, 1L);
                }
            }, 1L);

        }
    }

    public void registerService(Service service) {
        if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            this.setStartUp(true);
        }

        List<Service> list = this.getServices(service.getServiceGroup());
        Service s = this.getService(service.getName());
        if (s == null) {
            list.add(service);
            this.services.put(service.getServiceGroup(), list);
        }
        Action action = this.actions.getOrDefault(service.getName(), new Action());
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§aChannel §7[§a" + service.getName() + "@" + service.getUniqueId() + "§7] §aconnected §7[§2" + action.getMS() + "sec§7]");
        this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutRegisterServer(service));
        this.actions.remove(service.getName());
    }

    public void needServices(ServiceGroup serviceGroup) {
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }
        this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
            if (this.getServices(serviceGroup).size() < serviceGroup.getMinServer()) {
                for (int i = this.getServices(serviceGroup).size(); i < serviceGroup.getMinServer(); i++) {
                    int id = idService.getFreeID(serviceGroup.getName());
                    int port = serviceGroup.getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
                    Service service = new Service(serviceGroup.getName() + "-" + id, UUID.randomUUID(), serviceGroup, id, port, ServiceState.LOBBY);
                    this.startService(serviceGroup, service);
                }
            }
        }, 25L);
    }

    public void startService(ServiceGroup serviceGroup, Service service, Document properties) {
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }

        if (service.getPort() <= 0) {
            int port = service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
            service = new Service(service.getName(), service.getUniqueId(), serviceGroup, this.idService.getFreeID(serviceGroup.getName()), port, service.getServiceState());
        }
        Action action = new Action();
        this.actions.put(service.getName(), action);
        this.globalServices.add(service);
        List<Service> services = this.getServices(serviceGroup);
        services.add(service);

        this.services.put(serviceGroup, services);
        this.providerStart.autoStartService(service, properties, action);
        this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutStartedServer(service));

    }
    public void startService(ServiceGroup serviceGroup, Service service) {
        this.startService(serviceGroup, service, null);
    }

    public void startService(ServiceGroup serviceGroup) {
        int id = this.idService.getFreeID(serviceGroup.getName());
        int port = serviceGroup.getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
        Service service = new Service(serviceGroup.getName() + "-" + id, UUID.randomUUID(), serviceGroup, id, port, ServiceState.LOBBY);
        this.startService(serviceGroup, service);
        this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutRegisterServer(service));
    }


    public void stopService(Service service) {
        this.stopService(service, true);
    }

    public void stopService(Service service, boolean newServices) {
        this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutStopServer(service));
        this
                .idService
                .removeID(
                        service
                                .getServiceGroup()
                                .getName(),
                        service
                                .getServiceID()
                );
        this.portService.removeProxyPort(service.getPort());
        this.portService.removePort(service.getPort());
        this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
            //this.globalServices.remove(service);
            if (this.providerStop.stopService(service)) {
                if (!newServices) {
                    return;
                }
                this.needServices(service.getServiceGroup());
            }
        }, 2L);
    }

    public void stopServices() {
        for (ServiceGroup serviceGroup : this.services.keySet()) {
            this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§7The services of the group §c" + serviceGroup.getName() + " §7are now §4shutting down §7| §bServices " + this.services.get(serviceGroup).size());
        }
        if (this.globalServices == null) {
            this.getCloudLibrary().getConsole().getLogger().sendMessage("ERROR", "§cGlobalserver-List wasnt found!");
            this.globalServices = new LinkedList<>();
            return;
        }
        for (Service globalService : this.globalServices) {
            Service service = this.getService(globalService.getName());
            if (service == null) {
                continue;
            }
            this.stopService(service);
        }
    }

    public void stopServices(ServiceGroup serviceGroup) {
        int count = this.getServices(serviceGroup).size();
        for (Service service : this.getServices(serviceGroup)) {
            this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
                this.stopService(service, false);
            }, 1L);
            count--;
            if (count == 0) {
                this.needServices(serviceGroup);
            }
        }
    }

    public List<Service> getServices(ServiceGroup serviceGroup) {
        List<Service> list = this.services.get(serviceGroup);
        if (list == null) list = new LinkedList<>();

        return list;
    }

    public Service getService(String name) {
        try {
            for (List<Service> value : this.services.values()) {
                for (Service service : value) {
                    if (service.getName().equalsIgnoreCase(name)) {
                        return service;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public ServiceGroup getGroup(String name) {
        for (ServiceGroup serviceGroup : this.services.keySet()) {
            if (serviceGroup.getName().equalsIgnoreCase(name)) {
                return serviceGroup;
            }
        }
        return null;
    }

    public Service getService(int id) {
        for (List<Service> value : this.services.values()) {
            for (Service service : value) {
                if (service.getServiceID() == id) {
                    return service;
                }
            }
        }
        return null;
    }

}
