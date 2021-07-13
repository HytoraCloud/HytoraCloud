package de.lystx.hytoracloud.launcher.cloud.booting;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.launcher.cloud.handler.group.CloudHandlerTemplateCopy;
import de.lystx.hytoracloud.launcher.cloud.handler.group.CloudHandlerTemplateCreate;
import de.lystx.hytoracloud.launcher.cloud.handler.group.CloudHandlerGroupUpdate;
import de.lystx.hytoracloud.launcher.cloud.handler.managing.*;
import de.lystx.hytoracloud.launcher.cloud.handler.other.*;
import de.lystx.hytoracloud.launcher.cloud.handler.player.CloudHandlerPlayer;
import de.lystx.hytoracloud.launcher.cloud.handler.other.CloudHandlerCommunication;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.CloudHandlerReceiver;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.CloudHandlerReceiverServer;
import de.lystx.hytoracloud.launcher.cloud.handler.services.*;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.NetworkService;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.utils.Utils;

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
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Version §7: §b" + CloudDriver.getInstance().getVersion());
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Developer §7: §bLystx");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Loading §3CloudSystem§f...");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");

        CloudDriver.getInstance().getDatabaseManager().getDatabase().connect();
        CloudDriver.getInstance().getServiceRegistry().registerService(new NetworkService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new ModuleService());


        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", CloudDriver.getInstance().getInstance(NetworkService.class).getHytoraServer());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "serviceManager", new CloudSideServiceManager(CloudDriver.getInstance().getInstance(GroupService.class).getGroups()));

        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerRegister());
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerStop(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerPlayer());
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerStart(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReload(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerShutdown(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerConfig());
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerGroupUpdate(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerTemplateCopy(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerPerms(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerMessage(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerUpdate());
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerCommunication(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerCommand(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerTemplateCreate(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerLog(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerEvent(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerRequest(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerMemoryUsage(cloudSystem));

        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerSignSystem(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerNPC(cloudSystem));

        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiver(cloudSystem));
        CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverServer(cloudSystem));

    }
}
