package de.lystx.hytoracloud.modules.cloudflare;

import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.CloudModule;
import lombok.Getter;

@Getter
public class CloudFlareModule extends CloudModule {

    /**
     * The instance
     */
    @Getter
    private static CloudFlareModule instance;


    @Override
    public void onLoadConfig() {
        instance = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onReload() {
        this.config.save();
    }

}
