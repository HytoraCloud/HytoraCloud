package de.lystx.cloudsystem.cloud;

import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupDone;
import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupNotDone;
import de.lystx.cloudsystem.cloud.commands.*;
import de.lystx.cloudsystem.cloud.handler.ReceiverManager;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.cloud.commands.PlayerCommand;
import de.lystx.cloudsystem.library.CloudType;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketTransferFile;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCService;
import de.lystx.cloudsystem.library.service.serverselector.sign.SignService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


@Getter @Setter
public class CloudSystem extends CloudInstance {

    @Getter
    private static CloudSystem instance;

    public ServerService service;
    private final ReceiverManager receiverManager;


    public CloudSystem() {
        super(CloudType.CLOUDSYSTEM);
        instance = this;

        this.cloudServices.add(new StatisticsService(this, "Stats", CloudServiceType.UTIL));
        this.cloudServices.add(new GroupService(this, "Groups", CloudServiceType.MANAGING));
        this.cloudServices.add(new TemplateService(this, "Templates", CloudServiceType.MANAGING));
        this.cloudServices.add(new PermissionService(this, "Permissions", CloudServiceType.MANAGING));
        this.cloudServices.add(new SignService(this, "Signs", CloudServiceType.MANAGING));
        this.cloudServices.add(new NPCService(this, "NPCs", CloudServiceType.MANAGING));
        this.cloudServices.add(new DatabaseService(this, "Database", CloudServiceType.MANAGING));
        this.cloudServices.add(new CloudPlayerService(this, "CloudPlayerService", CloudServiceType.MANAGING));

        this.getService(CommandService.class).registerCommand(new EditCommand());
        this.getService(CommandService.class).registerCommand(new ModulesCommand());
        this.getService(CommandService.class).registerCommand(new CreateCommand());
        this.getService(CommandService.class).registerCommand(new DeleteCommand());
        this.getService(CommandService.class).registerCommand(new PermsCommand());
        this.getService(CommandService.class).registerCommand(new PlayerCommand());
        this.getService(CommandService.class).registerCommand(new MaintenanceCommand());

        this.authManager.createKey();

        if (this.autoUpdater()) {
            new CloudBootingSetupDone(this);
        } else {
            new CloudBootingSetupNotDone(this);
        }
        this.receiverManager = new ReceiverManager(this);
    }

    @Override
    public void reload() {
        super.reload();
        this.getService(PermissionService.class).load();
        this.getService(PermissionService.class).loadEntries();
        this.getService(NPCService.class).load();
        this.getService(SignService.class).load();
        this.getService(SignService.class).loadSigns();
        this.getService(StatisticsService.class).getStatistics().add("reloadedCloud");
    }

    public void syncGroupsWithServices() {
        this.getService(GroupService.class).loadGroups();
        for (List<Service> value : this.getService().getServices().values()) {
            for (Service s : value) {
                s.setServiceGroup(this.getService(GroupService.class).getGroup(s.getServiceGroup().getName()));
                CloudScreen cloudScreen = this.getService(ScreenService.class).getMap().get(s.getName());
                if (cloudScreen != null) {
                    File serverDir = cloudScreen.getServerDir();
                    try {
                        VsonObject vsonObject = new VsonObject(new File(serverDir, "CLOUD/connection.json"), VsonSettings.SAFE_TREE_OBJECTS, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
                        vsonObject.clear();
                        vsonObject.putAll(s);
                        vsonObject.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        this.getService(StatisticsService.class).save();
        this.getService(SignService.class).save();
        this.getService(NPCService.class).save();
    }

    @Override
    public void sendPacket(Packet packet) {
        this.getService(CloudNetworkService.class).sendPacket(packet);
    }

    @Override
    public void callEvent(Event event) {
        this.sendPacket(new PacketCallEvent(event).setSendBack(true));
    }
}
