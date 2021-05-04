package de.lystx.cloudsystem.library.network.connection.server;

import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public final class HostWhitelist {

    /**
     * The hosts inside the whitelist as string
     */
    @Getter
    private List<String> hosts = new ArrayList<>();

    /**
     * Is the whitelist active
     */
    private boolean active;

    /**
     * The netty server
     */
    private NetworkServer netServer;

    public HostWhitelist(NetworkServer netServer) {
        this.netServer = netServer;
    }

    /**
     * Loads the whitelist
     */
    public void load() {
        try {
            this.hosts = netServer.getConfig().getList("netty.whitelist.ips");
            this.active = netServer.getConfig().getBoolean("netty.whitelist.activated");
        } catch(Exception e) {
            netServer.getLogger().info("Couldn't load whitelist. Deactivating it ..");
            active = false;
        }
    }

    /**
     * Checks if given address is allowed
     *
     * @param address The address
     * @return The result
     */
    public boolean allowed(InetSocketAddress address) {
        return !active || hosts.contains(address.getAddress().getHostAddress());
    }

}
