package de.lystx.hytoracloud.modules.cloudflare.elements;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.config.CloudFlareConfig;
import lombok.Getter;

import java.util.Map;

@Getter
public class ModuleConfig extends CloudFlareConfig {

    /**
     * The proxy group dns entries
     */
    private final Map<String, String> proxyGroups;
    private final String zoneId;

    public ModuleConfig(String xAuthKey, String xAuthEmail, String xAuthToken, Map<String, String> proxyGroups, String zoneId) {
        super(xAuthKey, xAuthEmail, xAuthToken);
        this.proxyGroups = proxyGroups;
        this.zoneId = zoneId;
    }
}
