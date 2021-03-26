package de.lystx.module.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.Result;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.module.commands.packets.CommandModulePacket;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

public class CommandModule extends Module {

    @Getter
    private static CommandModule instance;

    private CloudLibrary cloudLibrary;

    @Override
    public void onLoadConfig(CloudLibrary cloudLibrary) {
        instance = this;
        this.cloudLibrary = cloudLibrary;

        VsonObject vsonObject = getConfig();

        vsonObject.getBoolean("infoCommand", true);
        vsonObject.getBoolean("whereIsCommand", true);
        vsonObject.getBoolean("listCommand", true);
        vsonObject.getBoolean("whereAmICommand", true);
        vsonObject.save();
        this.setConfig(vsonObject);

        cloudLibrary.getService(CloudNetworkService.class).registerHandler(this);
    }

    @PacketHandler(transformTo = CommandModulePacket.class)
    public void a(CommandModulePacket packet) {
        this.cloudLibrary.sendPacket(new CommandModulePacket(this.getConfig()));
    }


    @Override
    public void onEnable(CloudLibrary cloudLibrary) {}

    @Override
    public void onDisable(CloudLibrary cloudLibrary) {}
}
