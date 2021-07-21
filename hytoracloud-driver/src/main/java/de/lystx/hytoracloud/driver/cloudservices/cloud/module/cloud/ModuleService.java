package de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import lombok.Getter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter

@ICloudServiceInfo(
        name = "ModuleService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class loads and stores all the CloudModules"
        },
        version = 1.0
)
public class ModuleService implements ICloudService {

    private final List<CloudModule> cloudModules;
    private final ModuleLoader moduleLoader;
    private final File moduleDir;

    public ModuleService() {
        this.moduleDir = CloudDriver.getInstance().getInstance(FileService.class).getModulesDirectory();
        moduleDir.mkdirs();
        this.cloudModules = new LinkedList<>();

        this.moduleLoader = new ModuleLoader(moduleDir, this, CloudDriver.getInstance());
        this.load();
    }

    /**
     * Returns module by name
     * @param name
     * @return
     */
    public CloudModule getModule(String name) {
        return this.cloudModules.stream().filter((module -> module.getBase().getName().equalsIgnoreCase(name))).findFirst().orElse(null);
    }

    /**
     * Enables all modules
     */
    public void load() {
        this.moduleLoader.loadModules();
        this.cloudModules.forEach((CloudModule::onEnable));
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");
    }

    /**
     * Disables all modules
     */
    public void shutdown() {
        this.cloudModules.forEach((CloudModule::onDisable));
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
