package de.lystx.hytoracloud.modules.cloudflare;

import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.ModuleState;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.ModuleTask;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.CloudFlareAPI;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.config.CloudFlareAuth;
import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.modules.cloudflare.elements.ModuleConfig;
import lombok.Getter;

@Getter
public class CloudFlareModule extends DriverModule {

    private static final long serialVersionUID = -6209203568313830637L;
    /**
     * The instance
     */
    @Getter
    private static CloudFlareModule instance;

    /**
     * The config for this module
     */
    private ModuleConfig moduleConfig;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void loadConfig() {
        instance = this;
    }

    @ModuleTask(id = 2, state = ModuleState.STARTING)
    public void startModule() {

        new CloudFlareAPI(new CloudFlareAuth(this.moduleConfig.getXAuthKey(), this.moduleConfig.getXAuthEmail()), this.moduleConfig.getZoneId());
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void reloadModule() {
        if (this.config.isEmpty()) {
            this.config.append(new ModuleConfig("yourAuthKey", "yourAuthEmail", null, new CloudMap<String, String>().append("Bungee", "sub.yourdomain.com"), "yourZoneId"));
        }
        this.moduleConfig = this.config.getAs(ModuleConfig.class);
        this.config.save();
    }

}
