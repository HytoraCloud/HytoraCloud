package de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud;

import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.IFileModule;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.Getter;
import lombok.Setter;


@Setter @Getter
public abstract class CloudModule {

    /**
     * The base of this module
     */
    private IFileModule base;

    /**
     * The config
     */
    private JsonDocument config;

    /**
     * Called first
     */
    public abstract void onLoadConfig();

    /**
     * Called after loading config
     */
    public abstract void onEnable();

    /**
     * Called on shutdown
     */
    public abstract void onDisable();

    /**
     * Called when Module is reloaded
     */
    public abstract void onReload();

}
