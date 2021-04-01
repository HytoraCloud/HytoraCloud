package de.lystx.cloudsystem.cloud;

import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupDone;
import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupNotDone;
import de.lystx.cloudsystem.cloud.commands.*;
import de.lystx.cloudsystem.cloud.handler.ReceiverManager;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.cloud.commands.PlayerCommand;
import de.lystx.cloudsystem.library.elements.interfaces.CloudService;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.event.Event;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


@Getter @Setter
public class CloudSystem extends CloudInstance implements CloudService {

    @Getter
    private static CloudSystem instance;

    public ServerService service;
    private final ReceiverManager receiverManager;


    public CloudSystem() {
        super(CloudType.CLOUDSYSTEM);
        instance = this;

        this.cloudServices.add(new StatisticsService(this, "Stats", de.lystx.cloudsystem.library.service.CloudService.CloudServiceType.UTIL));
        this.cloudServices.add(new GroupService(this, "Groups", de.lystx.cloudsystem.library.service.CloudService.CloudServiceType.MANAGING));
        this.cloudServices.add(new PermissionService(this, "Permissions", de.lystx.cloudsystem.library.service.CloudService.CloudServiceType.MANAGING));
        this.cloudServices.add(new DatabaseService(this, "Database", de.lystx.cloudsystem.library.service.CloudService.CloudServiceType.MANAGING));
        this.cloudServices.add(new CloudPlayerService(this, "CloudPlayerService", de.lystx.cloudsystem.library.service.CloudService.CloudServiceType.MANAGING));

        this.getService(CommandService.class).registerCommand(new EditCommand());
        this.getService(CommandService.class).registerCommand(new ModulesCommand());
        this.getService(CommandService.class).registerCommand(new CreateCommand());
        this.getService(CommandService.class).registerCommand(new DeleteCommand());
        this.getService(CommandService.class).registerCommand(new PermsCommand());
        this.getService(CommandService.class).registerCommand(new PlayerCommand());
        this.getService(CommandService.class).registerCommand(new MaintenanceCommand());

        this.authManager.createKey();

        this.receiverManager = new ReceiverManager(this);
        this.bootstrap();
    }

    @Override
    public void reload() {
        super.reload();
        this.getService(PermissionService.class).load();
        this.getService(PermissionService.class).loadEntries();
        this.getService(StatisticsService.class).getStatistics().add("reloadedCloud");
    }

    public void syncGroupsWithServices() {
        this.getService(GroupService.class).loadGroups();
        for (List<Service> value : new LinkedList<>(this.getService().getServices().values())) {
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
        this.getService(StatisticsService.class).save();

        for (File file : this.getService(FileService.class).getTempDirectory().listFiles()) {
            if (file.getName().startsWith("[!]")) {
                FileUtils.forceDelete(file);
            }
        }
    }

    @Override
    public CloudExecutor getCurrentExecutor() {
        return this.getService(CloudNetworkService.class).getCloudServer();
    }

    @Override
    public CloudType getType() {
        return CloudType.CLOUDSYSTEM;
    }

    @Override
    public void callEvent(Event event) {
        this.sendPacket(new PacketCallEvent(event));
    }
}
