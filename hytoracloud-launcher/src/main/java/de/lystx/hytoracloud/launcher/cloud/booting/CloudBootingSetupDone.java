package de.lystx.hytoracloud.launcher.cloud.booting;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.launcher.cloud.handler.group.PacketHandlerCopyTemplate;
import de.lystx.hytoracloud.launcher.cloud.handler.group.PacketHandlerCreateTemplate;
import de.lystx.hytoracloud.launcher.cloud.handler.group.PacketHandlerGroupUpdate;
import de.lystx.hytoracloud.launcher.cloud.handler.managing.PacketHandlerConfig;
import de.lystx.hytoracloud.launcher.cloud.handler.managing.PacketHandlerMessage;
import de.lystx.hytoracloud.launcher.cloud.handler.managing.PacketHandlerPermissionPool;
import de.lystx.hytoracloud.launcher.cloud.handler.other.*;
import de.lystx.hytoracloud.launcher.cloud.handler.player.PacketHandlerCloudPlayer;
import de.lystx.hytoracloud.launcher.cloud.handler.player.PacketHandlerCloudPlayerCommunication;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.PacketHandlerReceiver;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.PacketHandlerReceiverServer;
import de.lystx.hytoracloud.launcher.cloud.handler.services.PacketHandlerRegister;
import de.lystx.hytoracloud.launcher.cloud.handler.services.PacketHandlerServiceUpdate;
import de.lystx.hytoracloud.launcher.cloud.handler.services.PacketHandlerStart;
import de.lystx.hytoracloud.launcher.cloud.handler.services.PacketHandlerStopServer;
import de.lystx.hytoracloud.driver.cloudservices.other.Updater;
import de.lystx.hytoracloud.driver.cloudservices.global.config.stats.StatsService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.NetworkService;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.DefaultServiceManager;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.utils.minecraft.NetworkInfo;

public class CloudBootingSetupDone {

    /**
     * CloudSystem Setup starts
     * > Because Setup was not done
     * @param cloudSystem
     */
    public CloudBootingSetupDone(CloudSystem cloudSystem) {

        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§f\n" +
                "    __  __      __                   ________                __\n" +
                "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                "      /____/                                                   \n" +
                "\n");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Version §7: §b" + Updater.getCloudVersion());
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Developer §7: §bLystx");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Loading §3CloudSystem§f...");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");

        CloudDriver.getInstance().getDatabaseManager().getDatabase().connect();
        CloudDriver.getInstance().getServiceRegistry().registerService(new NetworkService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new ModuleService());


        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", CloudDriver.getInstance().getInstance(NetworkService.class).getHytoraServer());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "serviceManager", new DefaultServiceManager(CloudDriver.getInstance().getInstance(GroupService.class).getGroups()));

        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerRegister());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerStopServer(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerCloudPlayer());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerStart(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerReload(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerShutdown(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerConfig());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerGroupUpdate(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerCopyTemplate(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerPermissionPool(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerMessage(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerServiceUpdate());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerCloudPlayerCommunication(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerCommand(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerCreateTemplate(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerLog(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerEvent(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerRequest(cloudSystem));

        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerReceiver(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerReceiverServer(cloudSystem));

        CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().add("bootedUp");
        CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().add("allCPUUsage", new NetworkInfo().getCPUUsage());
    }
}
