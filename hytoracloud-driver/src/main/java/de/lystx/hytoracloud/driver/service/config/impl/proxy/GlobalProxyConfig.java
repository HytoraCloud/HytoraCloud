package de.lystx.hytoracloud.driver.service.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor @Getter @Setter
public class GlobalProxyConfig implements Serializable {

    private Integer proxyStartPort;
    private Integer serverStartPort;
    private boolean proxyProtocol;
    private boolean maintenance;
    private boolean hubCommand;
    private List<String> whitelistedPlayers;

}
