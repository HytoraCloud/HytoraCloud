package de.lystx.hytoracloud.launcher.cloud;

import de.lystx.hytoracloud.driver.cloudservices.cloud.NetworkService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCService;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.SignService;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutServerSelector;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.launcher.cloud.booting.CloudBootingSetupDone;
import de.lystx.hytoracloud.launcher.cloud.booting.CloudBootingSetupNotDone;
import de.lystx.hytoracloud.launcher.cloud.commands.*;
import de.lystx.hytoracloud.launcher.cloud.handler.ReceiverManager;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;

import de.lystx.hytoracloud.launcher.cloud.impl.manager.CloudSideDatabaseManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.PermissionService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.CloudSidePlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.launcher.global.commands.DeleteCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import utillity.JsonEntity;


@Getter @Setter
public class CloudSystem extends CloudProcess {

    @Getter
    private static CloudSystem instance;

    public CloudSideServiceManager service;
    private final ReceiverManager receiverManager;


    public CloudSystem() {
        super(CloudType.CLOUDSYSTEM);
        instance = this;

        CloudDriver.getInstance().getServiceRegistry().registerService(new PermissionService());

        CloudDriver.getInstance().getServiceRegistry().registerService(new SignService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new NPCService());

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "databaseManager", new CloudSideDatabaseManager());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "playerManager", new CloudSidePlayerManager());


        this.getInstance(CommandService.class).registerCommand(new ModulesCommand());
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


        SignService service = CloudDriver.getInstance().getInstance(SignService.class);
        NPCService npcService = CloudDriver.getInstance().getInstance(NPCService.class);

        if (service == null || npcService == null) {
            return;
        }

        CloudDriver.getInstance().sendPacket(new PacketOutServerSelector(service.getCloudSigns(), service.getSignLayOut().getDocument(), npcService.getNPCConfig(), npcService.toMetas()));

        try {

            //Sending config and permission pool
            CloudDriver.getInstance().getNetworkConfig().update();
            CloudDriver.getInstance().getPermissionPool().update();

            //Sending network config and services and groups
            CloudDriver.getInstance().sendPacket(new PacketOutGlobalInfo(
                    CloudDriver.getInstance().getNetworkConfig(),
                    CloudDriver.getInstance().getInstance(GroupService.class).getGroups(),
                    CloudDriver.getInstance().getServiceManager().getCachedObjects()
            ));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Updating webserver
        CloudDriver.getInstance().getParent().getWebServer().update("players", new JsonEntity().append("players", CloudDriver.getInstance().getPlayerManager().getCachedObjects()));
        CloudDriver.getInstance().getParent().getWebServer().update("services", new JsonEntity().append("services", CloudDriver.getInstance().getServiceManager().getCachedObjects()));

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

        ((CloudSideServiceManager) CloudDriver.getInstance().getServiceManager()).setRunning(false);
        this.getInstance(Scheduler.class).scheduleDelayedTask(() -> this.getInstance(NetworkService.class).shutdown(), 60L);
    }

}
