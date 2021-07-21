package de.lystx.hytoracloud.driver.cloudservices.cloud.module.base;

import java.util.List;

public interface IModuleManager {

    /**
     * Gets a list of all {@link IModule}s that are loaded
     *
     * @return list of modules
     */
    List<IModule> getModules();

    /**
     * Searches for a {@link IModule} by name
     *
     * @param name the name
     * @return module or null if not found
     */
    IModule getModule(String name);

}
