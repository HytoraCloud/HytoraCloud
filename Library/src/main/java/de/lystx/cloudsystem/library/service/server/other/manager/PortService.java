package de.lystx.cloudsystem.library.service.server.other.manager;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class PortService {

    private final List<String> portlist;
    private final List<String> proxyPortList;
    private final int proxyPort, serverPort;

    public PortService(int proxyPort, int serverPort) {
        this.proxyPort = proxyPort;
        this.serverPort = serverPort;
        this.portlist = new LinkedList<>();
        this.proxyPortList = new LinkedList<>();
    }

    /**
     * Marks port as unused
     * @param port
     */
    public void removePort(Integer port) {
        this.portlist.remove(String.valueOf(port));
    }

    /**
     * Returns free port for server
     * @return
     */
    public int getFreePort() {
        for (int i = this.serverPort; i < (this.serverPort + 300000);) {
            if (this.portlist.contains(String.valueOf(i))) {
                i++;
                continue;
            }
            this.portlist.add(String.valueOf(i));
            return i;
        }
        return 404;
    }

    /**
     * Returns free port for proxy
     * @return
     */
    public int getFreeProxyPort() {
        for (int i = this.proxyPort; i < (this.proxyPort + 300000);) {
            if (this.proxyPortList.contains(String.valueOf(i))) {
                i++;
                continue;
            }
            this.proxyPortList.add(String.valueOf(i));
            return i;
        }
        return 404;
    }

    /**
     * Marks port as unused
     * @param port
     */
    public void removeProxyPort(Integer port) {
        this.proxyPortList.remove(String.valueOf(port));
    }

}
