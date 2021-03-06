package de.lystx.hytoracloud.launcher.cloud;

import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.launcher.cloud.booting.CloudBootingSetupDone;
import de.lystx.hytoracloud.launcher.cloud.booting.CloudBootingSetupNotDone;
import de.lystx.hytoracloud.launcher.cloud.commands.*;
import de.lystx.hytoracloud.launcher.cloud.handler.ReceiverManager;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;

import de.lystx.hytoracloud.launcher.cloud.impl.manager.CloudSideDatabaseManager;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.PermissionService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.CloudSidePlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;


@Getter @Setter
public class CloudSystem extends CloudProcess {

    @Getter
    private static CloudSystem instance;

    public CloudSideServiceManager service;
    private final ReceiverManager receiverManager;


    public CloudSystem() {
        super(CloudType.CLOUDSYSTEM);
        instance = this;

        CloudDriver.getInstance().getServiceRegistry().registerService(new GroupService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new PermissionService());

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "databaseManager", new CloudSideDatabaseManager());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "cloudPlayerManager", new CloudSidePlayerManager());


        this.getInstance(CommandService.class).registerCommand(new EditCommand());
        this.getInstance(CommandService.class).registerCommand(new ModulesCommand());
        this.getInstance(CommandService.class).registerCommand(new CreateCommand());
        this.getInstance(CommandService.class).registerCommand(new DeleteCommand());
        this.getInstance(CommandService.class).registerCommand(new PermsCommand());
        this.getInstance(CommandService.class).registerCommand(new PlayerCommand());
        this.getInstance(CommandService.class).registerCommand(new MaintenanceCommand());

        this.authManager.createKey();

        this.receiverManager = new ReceiverManager(this);
        this.bootstrap();

    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public void bootstrap() {
        if (this.getInstance(ConfigService.class).getNetworkConfig().isSetupDone()) {
            new CloudBootingSetupDone(this);
        } else {
            new CloudBootingSetupNotDone(this);
        }
    }

    @Override @SneakyThrows
    public void shutdown() {
        super.shutdown();

        for (File file : this.getInstance(FileService.class).getTempDirectory().listFiles()) {
            if (file.getName().startsWith("[!]")) {
                FileUtils.forceDelete(file);
            }
        }
    }

}
