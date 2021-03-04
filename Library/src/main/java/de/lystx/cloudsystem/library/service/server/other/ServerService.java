package de.lystx.cloudsystem.library.service.server.other;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.CloudType;
import de.lystx.cloudsystem.library.elements.events.other.ServiceStartEvent;
import de.lystx.cloudsystem.library.elements.events.other.ServiceStopEvent;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStartedServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.manager.IDService;
import de.lystx.cloudsystem.library.service.server.other.manager.PortService;
import de.lystx.cloudsystem.library.service.server.other.process.ServiceProviderStart;
import de.lystx.cloudsystem.library.service.server.other.process.ServiceProviderStop;
import de.lystx.cloudsystem.library.service.util.Action;
import de.lystx.cloudsystem.library.service.util.Value;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Getter @Setter
public class ServerService extends CloudService {

    private final Map<ServiceGroup, List<Service>> services;
    private List<Service> globalServices;
    private final Map<String, Action> actions;

    private final List<Service> cloudServers;
    private final List<Service> cloudProxies;
    private final List<Service> lobbies;
    private boolean startUp;

    private final IDService idService;
    private final PortService portService;

    private final ServiceProviderStart providerStart;
    private final ServiceProviderStop providerStop;

    private List<ServiceGroup> serviceGroups;

    public ServerService(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType, List<ServiceGroup> serviceGroups) {
        super(cloudLibrary, name, cloudType);
        this.serviceGroups = serviceGroups;
        this.actions = new HashMap<>();
        this.services = new HashMap<>();
        this.globalServices = new LinkedList<>();

        this.startUp = false;
        this.cloudServers = new LinkedList<>();
        this.lobbies = new LinkedList<>();
        this.cloudProxies = new LinkedList<>();

        this.idService = new IDService();
        this.portService = new PortService(cloudLibrary.getService(ConfigService.class).getNetworkConfig());

        FileService fs = cloudLibrary.getService(FileService.class);
        this.providerStart = new ServiceProviderStart(cloudLibrary, fs.getTemplatesDirectory(), fs.getDynamicServerDirectory(), fs.getStaticServerDirectory(), fs.getSpigotPluginsDirectory(), fs.getBungeeCordPluginsDirectory(), fs.getGlobalDirectory(), fs.getVersionsDirectory());
        this.providerStop = new ServiceProviderStop(cloudLibrary, this);

        this.startServices();
    }

    /**
     * Updates a group
     * @param group
     * @param newGroup
     */
    public void updateGroup(ServiceGroup group, ServiceGroup newGroup) {
        List<Service> list = this.getServices(this.getGroup(group.getName()));
        this.services.remove(this.getGroup(group.getName()));
        this.services.put(newGroup, list);

        for (Service service : this.getServices(this.getGroup(group.getName()))) {
            service.setServiceGroup(newGroup);
            CloudScreen screen = this.getCloudLibrary().getService(ScreenService.class).getMap().get(service.getName());

            try {
                VsonObject document = new VsonObject(new File(screen.getServerDir(), "CLOUD/connection.json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
                document.putAll(service);
                document.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends notify to console
     * @param service
     */
    public void notifyStart(Service service) {
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            return;
        }
        List<Service> list = this.getServices(service.getServiceGroup());
        if (!list.contains(service)) {
            list.add(service);
            this.services.put(service.getServiceGroup(), list);
        }
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }
        if (this.getCloudLibrary().getScreenPrinter().getScreen() != null && this.getCloudLibrary().getScreenPrinter().isInScreen()) {
            return;
        }
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§7The service §b" + service.getName() + " §7is §equeued §7| §bID " + service.getServiceID() + " §7| §bPort " + service.getPort() + " §7| §bGroup " + service.getServiceGroup().getName() + " §7| §bType " + service.getServiceGroup().getServiceType().name() );

    }

    /**
     * Sends stop notify
     * @param service
     */
    public void notifyStop(Service service) {
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            return;
        }
        List<Service> services = this.services.get(service.getServiceGroup());
        Service remove = this.getService(service.getName());
        if (services == null) services = new LinkedList<>();
        services.remove(remove);
        this.services.put(service.getServiceGroup(), services);
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }

        if (this.getCloudLibrary().getScreenPrinter().getScreen() != null && this.getCloudLibrary().getScreenPrinter().isInScreen()) {
            return;
        }
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§7The service §b" + service.getName() + " §7has §4stopped §7| §bGroup " + service.getServiceGroup().getName() + " §7| §bType " + service.getServiceGroup().getServiceType().name());
    }

    /**
     * Updates service
     * @param service
     * @param state
     */
    public void updateService(Service service, ServiceState state) {
        List<Service> services = this.getServices(service.getServiceGroup());
        services.remove(service);
        Service newService = new Service(service.getName(), service.getUniqueId(), service.getServiceGroup(), service.getServiceID(), service.getPort(), getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) ? getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getPort() : ((NetworkConfig)getCloudLibrary().getCustoms().get("networkConfig")).getPort(), state);
        services.add(newService);
        this.services.put(service.getServiceGroup(), services);
    }

    /**
     * Starts services
     */
    public void startServices() {
        this.startServices(this.serviceGroups);
    }

    /**
     * Starts services from list
     * @param serviceGroups
     */
    public void startServices(List<ServiceGroup> serviceGroups) {
        for (ServiceGroup serviceGroup : serviceGroups) {
            for (int i = 0; i < serviceGroup.getMinServer(); i++) {
                int id = this.idService.getFreeID(serviceGroup.getName());
                int port = serviceGroup.getServiceType().equals(ServiceType.SPIGOT) ? this.portService.getFreePort() : this.portService.getFreeProxyPort();

                Service service = new Service(serviceGroup.getName() + "-" + id, UUID.randomUUID(), serviceGroup, id, port, getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) ? getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getPort() : getCloudLibrary().getService(ConfigService.class).getReceiverInfo().getPort(), ServiceState.LOBBY);

                if (serviceGroup.getServiceType().equals(ServiceType.SPIGOT)) {
                    if (serviceGroup.isLobby()) {
                        this.lobbies.add(service);
                    } else {
                        this.cloudServers.add(service);
                    }
                } else {
                    cloudProxies.add(service);
                }
            }
        }
        this.cloudServers.sort(Comparator.comparingInt(Service::getServiceID));
        this.cloudProxies.sort(Comparator.comparingInt(Service::getServiceID));
        this.lobbies.sort(Comparator.comparingInt(Service::getServiceID));

        for (Service proxy : cloudProxies) {
            this.startService(proxy.getServiceGroup(), proxy);
        }

        this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
            for (Service service : this.lobbies) {
                this.startService(service.getServiceGroup(), service);
            }
            this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
                for (Service service : this.cloudServers) {
                    this.startService(service.getServiceGroup(), service);
                }
            }, 2L);
        }, 3L);
    }

    /**
     * Registers service after packetHandler handled
     * @param service
     */
    public void registerService(Service service) {
        List<Service> list = this.getServices(service.getServiceGroup());
        Service s = this.getService(service.getName());
        if (s == null) {
            list.add(service);
            this.services.put(service.getServiceGroup(), list);
        }
        Action action = this.actions.getOrDefault(service.getName(), new Action());

        this.getCloudLibrary().sendPacket(new PacketPlayOutRegisterServer(service).setAction(action.getMS()));
        this.actions.remove(service.getName());
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            return;
        }
        if (this.getCloudLibrary().getScreenPrinter().getScreen() != null && this.getCloudLibrary().getScreenPrinter().isInScreen()) {
            return;
        }
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§aChannel §7[§a" + service.getName() + "@" + service.getUniqueId() + "§7] §aconnected §7[§2" + action.getMS() + "sec§7]");

    }

    /**
     * Checks if serviceGroup needs services
     * @param serviceGroup
     */
    public void needServices(ServiceGroup serviceGroup) {
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }
        this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
            if (this.getServices(serviceGroup).size() < serviceGroup.getMinServer()) {
                for (int i = this.getServices(serviceGroup).size(); i < serviceGroup.getMinServer(); i++) {
                    int id = idService.getFreeID(serviceGroup.getName());
                    int port = serviceGroup.getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
                    Service service = new Service(
                            serviceGroup.getName() + "-" + id, UUID.randomUUID(), serviceGroup, id, port, getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getPort(), ServiceState.LOBBY);
                    this.startService(serviceGroup, service);
                }
            }
        }, 3L);
    }

    /**
     * Starts service
     * @param serviceGroup
     * @param service
     * @param properties
     * @returns VsonObject response
     */
    public VsonObject startService(ServiceGroup serviceGroup, Service service, SerializableDocument properties) {
        VsonObject document = new VsonObject();
        if (!this.getCloudLibrary().isRunning()) {
            return new VsonObject().append("message", "CloudLibrary isn't running anymore").append("sucess", false);
        }
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            if (this.getCloudLibrary().getService(GroupService.class).getGroup(serviceGroup.getName(), this.serviceGroups) == null) {
                document.append("message", "§cServiceGroup for §e" + serviceGroup.getName() + " §cwasn't found!");
                document.append("sucess", false);
                return document;
            }
        }
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            document.append("sucess", true);
            return document;
        }
        if (serviceGroup.getMaxServer() != -1 && this.getServices(serviceGroup).size() >= serviceGroup.getMaxServer()) {
            document.append("message", "§cCouldn't start any services of §e" + serviceGroup.getName() + " §cbecause the maximum of services of this group is §e" + serviceGroup.getMaxServer() + "§c!");
            document.append("sucess", false);
            this.getCloudLibrary().getConsole().getLogger().sendMessage("INFO", "§cThe service §e" + service.getName() + " §cwasn't started because there are §9[§e" + this.getServices(serviceGroup).size() + "§9/§e" + serviceGroup.getMaxServer() + "§9] §cservices of this group online!");
            return document;
        }
        this.getCloudLibrary().sendPacket(new PacketPlayOutRegisterServer(service));
        if (service.getPort() <= 0) {
            int port = service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
            int id = this.idService.getFreeID(serviceGroup.getName());
            service = new Service(serviceGroup.getName() + "-" + id, service.getUniqueId(), serviceGroup, id, port, ((NetworkConfig) getCloudLibrary().getCustoms().get("networkConfig")).getPort(), service.getServiceState());
        }
        service.setProperties((properties == null ? new SerializableDocument() : properties));
        this.globalServices.add(service);
        List<Service> services = this.getServices(serviceGroup);
        services.add(service);
        this.services.put(serviceGroup, services);

        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            return document;
        }
        if (this.providerStart.autoStartService(service, properties)) {
            this.notifyStart(service);
            this.getCloudLibrary().getService(EventService.class).callEvent(new ServiceStartEvent(service));
            this.getCloudLibrary().sendPacket(new PacketPlayOutStartedServer(service));
            this.actions.put(service.getName(), new Action());

            document.append("message", "§aSuccess!");
            document.append("service", service);
            document.append("sucess", true);
        }
        return document;
    }

    /**
     * Starts service from group
     * @param serviceGroup
     * @param service
     */
    public void startService(ServiceGroup serviceGroup, Service service) {
        this.startService(serviceGroup, service, null);
    }

    /**
     * Starts service with no properties
     * @param serviceGroup
     * @return
     */
    public VsonObject startService(ServiceGroup serviceGroup) {
        return this.startService(serviceGroup, (SerializableDocument) null);
    }

    /**
     * Starts service with properties
     * @param serviceGroup
     * @param properties
     * @return
     */
    public VsonObject startService(ServiceGroup serviceGroup, SerializableDocument properties) {
        int id = this.idService.getFreeID(serviceGroup.getName());
        int port = serviceGroup.getServiceType().equals(ServiceType.PROXY) ? this.portService.getFreeProxyPort() : this.portService.getFreePort();
        Service service = new Service(serviceGroup.getName() + "-" + id, UUID.randomUUID(), serviceGroup, id, port, getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) ? getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getPort() : ((NetworkConfig)getCloudLibrary().getCustoms().get("networkConfig")).getPort(), ServiceState.LOBBY);
        return this.startService(serviceGroup, service, properties);
    }

    /**
     * Stops service
     * @param service
     */
    public void stopService(Service service) {
        this.stopService(service, true);
    }

    /**
     * Stops service
     * @param service
     * @param newServices > Should new services start if needed
     */
    public void stopService(Service service, boolean newServices) {
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            return;
        }
        try {
            this.getCloudLibrary().sendPacket(new PacketPlayOutStopServer(service));
            this.idService.removeID(service.getServiceGroup().getName(), service.getServiceID());
            this.portService.removeProxyPort(service.getPort());
            this.portService.removePort(service.getPort());

            List<Service> services = this.services.get(this.getGroup(service.getServiceGroup().getName()));
            Service remove = this.getService(service.getName());
            if (services == null) services = new LinkedList<>();
            services.remove(remove);
            this.services.put(this.getGroup(service.getServiceGroup().getName()), services);

            if (!this.getCloudLibrary().getService(EventService.class).callEvent(new ServiceStopEvent(service))) {
                this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
                    if (this.providerStop.stopService(service)) {
                        if (!newServices) {
                            return;
                        }
                        this.needServices(service.getServiceGroup());
                    }
                }, 3L);
            }
        } catch (NullPointerException ignored) {
            //Hier ist etwas schief gegangen
        }
    }

    /**
     * Stops all services
     */
    public void stopServices() {
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            return;
        }
        List<String> already = new LinkedList<>();
        for (ServiceGroup serviceGroup : this.services.keySet()) {
            if (this.getCloudLibrary().getService(GroupService.class).getGroup(serviceGroup.getName(), this.serviceGroups) == null) {
                continue;
            }
            if (!already.contains(serviceGroup.getName())) {
                already.add(serviceGroup.getName());
                if (this.getCloudLibrary().getScreenPrinter().getScreen() == null && !this.getCloudLibrary().getScreenPrinter().isInScreen()) {
                    this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§7The services of the group §c" + serviceGroup.getName() + " §7are now §4shutting down §7| §bServices " + this.services.get(serviceGroup).size());
                }
            }
        }

        for (Object s : (this.globalServices == null ? new LinkedList<>() : this.globalServices)) {
            Service globalService = (Service)s;
            Service service = this.getService(globalService.getName());
            if (service == null) {
                continue;
            }
            this.stopService(service);
        }
    }

    /**
     * Stops services from group
     * @param serviceGroup
     */
    public void stopServices(ServiceGroup serviceGroup) {
        this.stopServices(serviceGroup, true);
    }

    /**
     * Stops services from group
     * @param serviceGroup
     * @param newOnes > Should new ones start
     */
    public void stopServices(ServiceGroup serviceGroup, boolean newOnes) {
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            return;
        }
        Value<Integer> count = new Value<>(this.getServices(serviceGroup).size());
        for (Service service : this.getServices(serviceGroup)) {
            this.stopService(service, false);
            count.setValue(count.getValue() - 1);

            if (count.getValue() == 0 && newOnes) {
                this.needServices(serviceGroup);
            }
        }
    }

    /**
     * Returns all services from a group
     * @param serviceGroup
     * @return
     */
    public List<Service> getServices(ServiceGroup serviceGroup) {
        List<Service> list = this.services.get(this.getGroup(serviceGroup.getName()));
        if (list == null) list = new LinkedList<>();

        return list;
    }

    /**
     * Returns services by name
     * @param name
     * @return
     */
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

    /**
     * Returns group by name
     * @param name
     * @return
     */
    public ServiceGroup getGroup(String name) {
        return this.services.keySet().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
