package de.lystx.cloudsystem.library.service.network;

import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class ConnectionBuilder {

    private String host;
    private int port;
    private boolean server;


    public ConnectionBuilder() {

    }

    public ConnectionBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public ConnectionBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public ConnectionBuilder asInstance() {
        this.server = true;
        return this;
    }

    public void build(CloudExecutor cloudExecutor, Consumer<CloudExecutor> consumer) {

    }


    public InetAddress asAddress() {
        return new InetSocketAddress(this.host, this.port).getAddress();
    }
}
