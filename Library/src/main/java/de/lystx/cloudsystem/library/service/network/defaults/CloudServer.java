package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.netty.NettyServer;
import lombok.Getter;
import lombok.Setter;


@Setter @Getter
public class CloudServer extends NettyServer implements CloudExecutor {


    public CloudServer(String host, int port) {
        super(host, port);
    }

    public void connect() {
        this.start();
    }

    public void connect(String host, int port) {
        this.setHost(host);
        this.setPort(port);
        this.connect();
    }

    public void disconnect() {
        this.getServerChannel().close();
    }

    public void registerPacketHandler(Object packetHandlerAdapter) {
        this.getPacketAdapter().registerAdapter(packetHandlerAdapter);
    }

}
