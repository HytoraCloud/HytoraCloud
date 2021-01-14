package de.lystx.cloudsystem.library.service.server.other.manager;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class PortService {

    private final List<String> portlist;
    private final List<String> proxyPortList;

    public PortService() {
        this.portlist = new LinkedList<>();
        this.proxyPortList = new LinkedList<>();
    }

    public void removePort(Integer port) {
        this.portlist.remove(String.valueOf(port));
    }

    public int getFreePort() {
        for (int i = 30000; i < 700000;) {
            if (this.portlist.contains(String.valueOf(i))) {
                i++;
                continue;
            }
            this.portlist.add(String.valueOf(i));
            return i;
        }
        return 404;
    }

    public int getFreeProxyPort() {
        for (int i = 25565; i < 700000;) {
            if (this.proxyPortList.contains(String.valueOf(i))) {
                i++;
                continue;
            }
            this.proxyPortList.add(String.valueOf(i));
            return i;
        }
        return 404;
    }

    public void removeProxyPort(Integer port) {
        this.proxyPortList.remove(String.valueOf(port));
    }

}
