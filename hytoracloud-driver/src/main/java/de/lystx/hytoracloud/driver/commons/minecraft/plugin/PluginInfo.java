package de.lystx.hytoracloud.driver.commons.minecraft.plugin;

import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PluginInfo implements Serializable {

    private static final long serialVersionUID = 6271866535405981573L;

    /**
     * Name of the plugin
     */
    private final String name;

    /**
     * The author(s) of the plugin
     */
    private final String[] authors;

    /**
     * The version of the plugin
     */
    private final String version;

    /**
     * The main-class of the plugin
     */
    private final String main;

    /**
     * The website for this plugin
     */
    private final String website;

    /**
     * Registered commands for this plugin
     */
    private final String[] commands;

    /**
     * The description of this plugin
     */
    private final String description;

    /**
     * Dependencies for this plugin
     */
    private final String[] dependencies;

    /**
     * SoftDependencies for this plugin
     */
    private final String[] softDependencies;


    @Override
    public String toString() {
        return JsonDocument.toString(this);
    }
}
