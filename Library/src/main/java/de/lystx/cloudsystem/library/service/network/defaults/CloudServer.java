package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.service.network.netty.NettyServer;
import lombok.Getter;
import lombok.Setter;


@Setter @Getter
public class CloudServer extends NettyServer implements CloudExecutor {

    public CloudServer(String host, int port) {
        super(host, port);
    }

    /**
     * Connecting
     */
    public void connect() {
        try {
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connecting with custom host and port
     * @param host
     * @param port
     */
    public void connect(String host, int port) {
        this.setHost(host);
        this.setPort(port);
        this.connect();
    }

    /**
     * Disconnecting
     */
    public void disconnect() {
        this.getChannel().close();
    }

    /**
     * Registering handler
     * @param packetHandlerAdapter
     */
    public void registerPacketHandler(Object packetHandlerAdapter) {
        this.getPacketAdapter().registerAdapter(packetHandlerAdapter);
    }

}
