package de.lystx.hytoracloud.driver.service.cloud.module;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.global.main.ICloudService;
import de.lystx.hytoracloud.driver.service.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.cloud.module.loader.ModuleLoader;
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

    private final List<Module> modules;
    private final ModuleLoader moduleLoader;
    private final File moduleDir;

    public ModuleService() {
        this.moduleDir = CloudDriver.getInstance().getInstance(FileService.class).getModulesDirectory();
        moduleDir.mkdirs();
        this.modules = new LinkedList<>();

        this.moduleLoader = new ModuleLoader(moduleDir, this, CloudDriver.getInstance());
        this.load();
    }

    /**
     * Returns module by name
     * @param name
     * @return
     */
    public Module getModule(String name) {
        return this.modules.stream().filter((module -> module.getInfo().getName().equalsIgnoreCase(name))).findFirst().orElse(null);
    }

    /**
     * Enables all modules
     */
    public void load() {
        this.moduleLoader.loadModules();
        this.modules.forEach((Module::onEnable));
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");
    }

    /**
     * Disables all modules
     */
    public void shutdown() {
        this.modules.forEach((Module::onDisable));
    }
}
