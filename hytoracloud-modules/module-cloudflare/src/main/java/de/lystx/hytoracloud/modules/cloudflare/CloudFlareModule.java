package de.lystx.hytoracloud.modules.cloudflare;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.CloudModule;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.CloudFlareAPI;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.config.CloudFlareAuth;
import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.modules.cloudflare.elements.ModuleConfig;
import lombok.Getter;

@Getter
public class CloudFlareModule extends CloudModule {

    /**
     * The instance
     */
    @Getter
    private static CloudFlareModule instance;

    private ModuleConfig moduleConfig;

    @Override
    public void onLoadConfig() {
        instance = this;
    }

    @Override
    public void onEnable() {

        new CloudFlareAPI(new CloudFlareAuth(this.moduleConfig.getXAuthKey(), this.moduleConfig.getXAuthEmail()), this.moduleConfig.getZoneId());
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onReload() {
        if (this.config.isEmpty()) {
            this.config.append(new ModuleConfig("yourAuthKey", "yourAuthEmail", null, new CloudMap<String, String>().append("Bungee", "sub.yourdomain.com"), "yourZoneId"));
        }
        this.moduleConfig = this.config.getAs(ModuleConfig.class);
        this.config.save();
    }

}
