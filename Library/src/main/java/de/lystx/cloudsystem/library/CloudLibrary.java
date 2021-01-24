package de.lystx.cloudsystem.library;

import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Identifier;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Provider;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.network.defaults.CloudServer;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CloudLibrary implements Serializable {


    public final List<CloudService> cloudServices;

    protected final String host;
    protected final Integer port;
    protected final Provider provider;
    protected final Identifier identifier;
    protected final NetworkChannel networkChannel;

    protected final CloudServer cloudServer;
    protected final CloudClient cloudClient;

    @Setter
    protected boolean running;

    protected CloudConsole console;
    protected CloudScreenPrinter screenPrinter;


    public CloudLibrary() {
        this.cloudServices = new LinkedList<>();
        this.host = "127.0.0.1";
        this.port = 2131;
        this.running = true;
        this.identifier = new Identifier("thecloud_cloudsystem");
        this.provider = new Provider("network_communication_bridge");
        this.networkChannel = new NetworkChannel(this.identifier, this.provider);

        this.cloudServer = new CloudServer(this.host, this.port, this.networkChannel, new AdapterHandler(), new PacketHandler());
        this.cloudClient = new CloudClient(this.host, this.port, this.networkChannel, new AdapterHandler());

        this.cloudServices.add(new Scheduler(this, "Scheduler", CloudService.Type.UTIL));
    }

    public <T> T getService(Class<T> tClass) {
        for (CloudService cloudService : this.cloudServices) {
            if (cloudService.getClass() == tClass) {
                return (T) cloudService;
            }
        }
        return null;
    }

}
