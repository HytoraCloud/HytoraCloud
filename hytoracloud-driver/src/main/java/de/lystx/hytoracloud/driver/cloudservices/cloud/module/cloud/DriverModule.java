package de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud;

import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.IFileModule;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.ModuleInfo;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;


@Setter @Getter
public abstract class DriverModule {

    /**
     * The base of this module
     */
    protected IFileModule base;

    /**
     * The config
     */
    protected JsonDocument config;

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

    public ModuleInfo info() {
        return this.getClass().getAnnotation(ModuleInfo.class);
    }
}
