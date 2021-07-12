package de.lystx.hytoracloud.driver.cloudservices.cloud.module;

import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.File;


@Setter @Getter
public abstract class Module {

    private ModuleInfo info;
    private VsonObject config;
    private File moduleDirectory;

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
    public void onReload() {}

}
