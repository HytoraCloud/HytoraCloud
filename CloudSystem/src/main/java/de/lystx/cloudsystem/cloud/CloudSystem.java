package de.lystx.cloudsystem.cloud;

import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupDone;
import de.lystx.cloudsystem.cloud.booting.CloudBootingSetupNotDone;
import de.lystx.cloudsystem.cloud.commands.*;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.cloud.commands.PlayerCommand;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import lombok.Getter;
import lombok.Setter;

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
    public void sendPacket(Packet packet) {
        this.getService(CloudNetworkService.class).sendPacket(packet);
    }

    public ReceiverInfo getReceiver(String name) {
        return this.receivers.stream().filter(receiverInfo -> receiverInfo.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
