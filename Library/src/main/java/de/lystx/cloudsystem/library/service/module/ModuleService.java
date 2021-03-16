package de.lystx.cloudsystem.library.service.module;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.module.loader.ModuleLoader;
import lombok.Getter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ModuleService extends CloudService {

    private final List<Module> modules;
    private final ModuleLoader moduleLoader;
    private final File moduleDir;

    public ModuleService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.moduleDir = cloudLibrary.getService(FileService.class).getModulesDirectory();
        moduleDir.mkdirs();
        this.modules = new LinkedList<>();

        this.moduleLoader = new ModuleLoader(moduleDir, this, cloudLibrary);
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
        this.modules.forEach((module -> module.onEnable(this.getCloudLibrary())));
    }

    /**
     * Disables all modules
     */
    public void shutdown() {
        this.modules.forEach((module -> module.onDisable(this.getCloudLibrary())));
    }
}
