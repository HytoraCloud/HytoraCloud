package de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
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

    private final List<DriverModule> driverModules;
    private final ModuleLoader moduleLoader;
    private final File moduleDir;

    public ModuleService(File moduleDir) {
        this.moduleDir = moduleDir;
        moduleDir.mkdirs();
        this.driverModules = new LinkedList<>();

        this.moduleLoader = new ModuleLoader(moduleDir, this, CloudDriver.getInstance());
        this.load();
    }

    /**
     * Returns module by name
     * @param name
     * @return
     */
    public DriverModule getModule(String name) {
        return this.driverModules.stream().filter((module -> module.getBase().getName().equalsIgnoreCase(name))).findFirst().orElse(null);
    }

    /**
     * Enables all modules
     */
    public void load() {
        this.moduleLoader.loadModules();
        this.driverModules.forEach(driverModule -> {
            CloudDriver.getInstance().executeIf(() -> {
                if (Arrays.asList(driverModule.info().allowedTypes()).contains(CloudDriver.getInstance().getServiceType())) {
                    driverModule.onEnable();
                }
            }, () -> CloudDriver.getInstance().getServiceType() != ServiceType.NONE);
        });
        if (CloudDriver.getInstance().getParent() != null) {
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");
        }
    }

    /**
     * Disables all modules
     */
    public void shutdown() {
        this.driverModules.forEach(driverModule -> {

            CloudDriver.getInstance().executeIf(() -> {
                if (Arrays.asList(driverModule.info().allowedTypes()).contains(CloudDriver.getInstance().getServiceType())) {
                    driverModule.onDisable();
                }
            }, () -> CloudDriver.getInstance().getServiceType() != ServiceType.NONE);
        });
    }

    @Override
    public void reload() {
        this.driverModules.forEach(driverModule -> {
            CloudDriver.getInstance().executeIf(() -> {
                if (Arrays.asList(driverModule.info().allowedTypes()).contains(CloudDriver.getInstance().getServiceType())) {
                    driverModule.onReload();
                }
            }, () -> CloudDriver.getInstance().getServiceType() != ServiceType.NONE);
        });
    }

    @Override
    public void save() {

    }
}
