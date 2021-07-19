package de.lystx.hytoracloud.driver.commons.minecraft.plugin;

import utillity.JsonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PluginInfo {

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
        return JsonEntity.toString(this);
    }
}
