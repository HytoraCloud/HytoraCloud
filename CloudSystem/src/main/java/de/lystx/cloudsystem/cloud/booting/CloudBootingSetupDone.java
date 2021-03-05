package de.lystx.cloudsystem.cloud.booting;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.cloud.handler.group.PacketHandlerCopyTemplate;
import de.lystx.cloudsystem.cloud.handler.group.PacketHandlerCreateTemplate;
import de.lystx.cloudsystem.cloud.handler.group.PacketHandlerGroupUpdate;
import de.lystx.cloudsystem.cloud.handler.managing.*;
import de.lystx.cloudsystem.cloud.handler.other.*;
import de.lystx.cloudsystem.cloud.handler.player.PacketHandlerCloudPlayer;
import de.lystx.cloudsystem.cloud.handler.player.PacketHandlerCloudPlayerCommunication;
import de.lystx.cloudsystem.cloud.handler.receiver.PacketHandlerReceiver;
import de.lystx.cloudsystem.cloud.handler.receiver.PacketHandlerReceiverServer;
import de.lystx.cloudsystem.cloud.handler.result.PacketHandlerResult;
import de.lystx.cloudsystem.cloud.handler.services.PacketHandlerRegister;
import de.lystx.cloudsystem.cloud.handler.services.PacketHandlerServiceUpdate;
import de.lystx.cloudsystem.cloud.handler.services.PacketHandlerStart;
import de.lystx.cloudsystem.cloud.handler.services.PacketHandlerStopServer;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.backup.BackupService;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;

public class CloudBootingSetupDone {

    /**
     * CloudSystem Setup starts
     * > Because Setup was not done
     * @param cloudSystem
     */
    public CloudBootingSetupDone(CloudSystem cloudSystem) {

        cloudSystem.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getConsole().getLogger().sendMessage("§b\n" +
                "    __  __      __                   ________                __\n" +
                "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                "      /____/                                                   \n" +
                "\n");
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§9Version §7: §b" + Updater.getCloudVersion());
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§9Developer §7: §bLystx");
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§bLoading §fCloudSystem§9...");
        cloudSystem.getConsole().getLogger().sendMessage("§9-----------------------------------------");

        cloudSystem.getService(DatabaseService.class).getDatabase().connect();
        cloudSystem.cloudServices.add(new CloudNetworkService(cloudSystem, "CloudNetwork", CloudServiceType.NETWORK));
        cloudSystem.cloudServices.add(new ModuleService(cloudSystem, "Modules", CloudServiceType.MANAGING));
        cloudSystem.cloudServices.add(new BackupService(cloudSystem, "Backups", CloudServiceType.MANAGING));

        Constants.EXECUTOR = cloudSystem.getService(CloudNetworkService.class).getCloudServer();

        FileService fs = cloudSystem.getService(FileService.class);
        cloudSystem.cloudServices.add(cloudSystem.service = new ServerService(cloudSystem, "Services", CloudServiceType.NETWORK, cloudSystem.getService(GroupService.class).getGroups()));

        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerRegister(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerStopServer(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudPlayer(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerStart(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerReload(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerShutdown(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerConfig(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerGroupUpdate(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCopyTemplate(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerPermissionPool(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerMessage(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerServiceUpdate(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudSign(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudPlayerCommunication(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerNPC(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerSubChannel(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCommand(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCreateTemplate(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerLog(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerTPS(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerResult(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerEvent(cloudSystem));

        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerReceiver(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerReceiverServer(cloudSystem));

        cloudSystem.getService(StatisticsService.class).getStatistics().add("bootedUp");
        cloudSystem.getService(StatisticsService.class).getStatistics().add("allCPUUsage", new NetworkInfo().getCPUUsage());
        cloudSystem.reload();
    }
}
