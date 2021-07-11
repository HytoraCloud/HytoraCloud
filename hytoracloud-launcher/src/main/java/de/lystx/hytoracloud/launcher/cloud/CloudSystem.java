package de.lystx.hytoracloud.launcher.cloud;

import de.lystx.hytoracloud.launcher.cloud.booting.CloudBootingSetupDone;
import de.lystx.hytoracloud.launcher.cloud.booting.CloudBootingSetupNotDone;
import de.lystx.hytoracloud.launcher.cloud.commands.*;
import de.lystx.hytoracloud.launcher.cloud.handler.ReceiverManager;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.launcher.global.impl.DefaultChannelMessenger;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.service.managing.command.CommandService;
import de.lystx.hytoracloud.driver.service.global.config.stats.StatsService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.DefaultDatabaseService;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.managing.permission.PermissionService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.DefaultCloudPlayerManager;
import de.lystx.hytoracloud.driver.service.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.DefaultServiceManager;
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

    public DefaultServiceManager service;
    private final ReceiverManager receiverManager;


    public CloudSystem() {
        super(CloudType.CLOUDSYSTEM);
        instance = this;

        CloudDriver.getInstance().getServiceRegistry().registerService(new StatsService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new GroupService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new PermissionService());

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "databaseManager", new DefaultDatabaseService());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "cloudPlayerManager", new DefaultCloudPlayerManager());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "channelMessenger", new DefaultChannelMessenger());


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
        this.getInstance(StatsService.class).getStatistics().add("reloadedCloud");
        this.getInstance(PermissionService.class).reload();
        CloudDriver.getInstance().getPermissionPool().update();
    }

    @Override
    public void bootstrap() {
        if (this.autoUpdater()) {
            new CloudBootingSetupDone(this);
        } else {
            new CloudBootingSetupNotDone(this);
        }
    }

    @Override @SneakyThrows
    public void shutdown() {
        super.shutdown();
        this.getInstance(StatsService.class).save();

        for (File file : this.getInstance(FileService.class).getTempDirectory().listFiles()) {
            if (file.getName().startsWith("[!]")) {
                FileUtils.forceDelete(file);
            }
        }
    }

}
