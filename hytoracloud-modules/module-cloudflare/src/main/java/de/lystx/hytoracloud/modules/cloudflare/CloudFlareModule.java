package de.lystx.hytoracloud.modules.cloudflare;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.cloudflare.ICloudFlareManager;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.config.CloudFlareAuth;
import de.lystx.hytoracloud.driver.module.base.ModuleState;
import de.lystx.hytoracloud.driver.module.base.info.ModuleTask;
import de.lystx.hytoracloud.driver.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.utils.other.CloudMap;
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
        ICloudFlareManager cloudFlareManager = CloudDriver.getInstance().getCloudFlareManager();
        cloudFlareManager.setAuth(new CloudFlareAuth(this.moduleConfig.getXAuthKey(), this.moduleConfig.getXAuthEmail()));
        cloudFlareManager.setZoneId(this.moduleConfig.getZoneId());
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
