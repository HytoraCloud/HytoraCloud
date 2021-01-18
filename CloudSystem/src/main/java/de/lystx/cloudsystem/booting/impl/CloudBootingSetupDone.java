package de.lystx.cloudsystem.booting.impl;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.handler.group.PacketHandlerCoypTemplate;
import de.lystx.cloudsystem.handler.group.PacketHandlerGroupUpdate;
import de.lystx.cloudsystem.handler.managing.*;
import de.lystx.cloudsystem.handler.other.PacketHandlerReload;
import de.lystx.cloudsystem.handler.other.PacketHandlerShutdown;
import de.lystx.cloudsystem.handler.other.PacketHandlerSubChannel;
import de.lystx.cloudsystem.handler.player.PacketHandlerCloudPlayer;
import de.lystx.cloudsystem.handler.player.PacketHandlerCloudPlayerCommunication;
import de.lystx.cloudsystem.handler.services.PacketHandlerRegister;
import de.lystx.cloudsystem.handler.services.PacketHandlerServiceUpdate;
import de.lystx.cloudsystem.handler.services.PacketHandlerStart;
import de.lystx.cloudsystem.handler.services.PacketHandlerStopServer;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;

public class CloudBootingSetupDone {


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
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§9Version §7: §b" + cloudSystem.getVersion());
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§9Developer §7: §bLystx");
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§bLoading §fCloudSystem§9...");
        cloudSystem.getConsole().getLogger().sendMessage("§9-----------------------------------------");

        cloudSystem.cloudServices.add(new CloudNetworkService(cloudSystem, "CloudNetwork", CloudService.Type.NETWORK));
        cloudSystem.cloudServices.add(new ModuleService(cloudSystem, "Modules", CloudService.Type.MANAGING));

        cloudSystem.cloudServices.add(cloudSystem.service = new ServerService(cloudSystem, "Services", CloudService.Type.NETWORK));

        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerRegister(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerStopServer(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudPlayer(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerStart(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerReload(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerShutdown(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerConfig(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerGroupUpdate(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCoypTemplate(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerPermissionPool(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerMessage(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerServiceUpdate(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudSign(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudPlayerCommunication(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerNPC(cloudSystem));
        cloudSystem.getService(CloudNetworkService.class).registerHandler(new PacketHandlerSubChannel(cloudSystem));

        cloudSystem.getService(StatisticsService.class).getStatistics().add("bootedUp");
        cloudSystem.reload("statistics");
    }
}
