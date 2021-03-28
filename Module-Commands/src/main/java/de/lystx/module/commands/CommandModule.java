package de.lystx.module.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.both.PacketInformation;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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

    @PacketHandler
    public void handle(PacketInformation packet) {
        if (packet.getKey().equalsIgnoreCase("modulePacket")) {
            Map<String, Object> map = new HashMap<>();
            map.put("config", this.getConfig());
            this.cloudLibrary.sendPacket(new PacketInformation("modulePacket_back", map));
        }
    }


    @Override
    public void onEnable(CloudLibrary cloudLibrary) {}

    @Override
    public void onDisable(CloudLibrary cloudLibrary) {}
}
