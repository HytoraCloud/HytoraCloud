package de.lystx.cloudsystem.cloud;

import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupDone;
import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupNotDone;
import de.lystx.cloudsystem.cloud.commands.*;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.cloud.commands.PlayerCommand;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketTransferFile;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCService;
import de.lystx.cloudsystem.library.service.serverselector.sign.SignService;
import de.lystx.cloudsystem.library.service.util.LogService;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


@Getter @Setter
public class CloudSystem extends CloudInstance {

    @Getter
    private static CloudSystem instance;

    public ServerService service;
    private final List<ReceiverInfo> receivers;

    public CloudSystem() {
        super(Type.CLOUDSYSTEM);
        instance = this;

        this.receivers = new LinkedList<>();

        this.cloudServices.add(new StatisticsService(this, "Stats", CloudService.Type.UTIL));
        this.cloudServices.add(new GroupService(this, "Groups", CloudService.Type.MANAGING));
        this.cloudServices.add(new TemplateService(this, "Templates", CloudService.Type.MANAGING));
        this.cloudServices.add(new PermissionService(this, "Permissions", CloudService.Type.MANAGING));
        this.cloudServices.add(new SignService(this, "Signs", CloudService.Type.MANAGING));
        this.cloudServices.add(new NPCService(this, "NPCs", CloudService.Type.MANAGING));
        this.cloudServices.add(new DatabaseService(this, "Database", CloudService.Type.MANAGING));
        this.cloudServices.add(new CloudPlayerService(this, "CloudPlayerService", CloudService.Type.MANAGING));

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
    }

    @Override
    public void reload() {
        super.reload();
        this.syncTemplates();
        this.getService(PermissionService.class).load();
        this.getService(PermissionService.class).loadEntries();
        this.getService(NPCService.class).load();
        this.getService(SignService.class).load();
        this.getService(SignService.class).loadSigns();
        this.getService(StatisticsService.class).getStatistics().add("reloadedCloud");
    }

    public void syncTemplates() {
        File directory = this.getService(FileService.class).getTemplatesDirectory();
        for (File file : directory.listFiles()) {
            this.sendPacket(new PacketTransferFile("template_transfer", file));
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

    public ReceiverInfo getReceiver(String name) {
        return this.receivers.stream().filter(receiverInfo -> receiverInfo.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
