package de.lystx.module.commands.bungee;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketInformation;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.module.commands.bungee.commands.*;
import io.vson.elements.object.VsonObject;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;


public class CommandsModuleBungee extends Plugin {

    @Override
    public void onEnable() {
        CloudAPI.getInstance().sendPacket(new PacketInformation("modulePacket", new HashMap<>()));
        CloudAPI.getInstance().registerPacketHandler(this);
    }
    @PacketHandler()
    public void handle(PacketInformation packet) {
        if (!packet.getKey().equalsIgnoreCase("modulePacket_back")) {
            return;
        }
        final VsonObject result = (VsonObject) packet.getData().get("config");
        if (result == null) {
            return;
        }
        if (result.getBoolean("listCommand")) {
            CloudAPI.getInstance().registerCommand(new ListCommand());
        } else {
            CloudAPI.getInstance().unregisterCommand(new ListCommand());
        }
        if (result.getBoolean("whereIsCommand")) {
            CloudAPI.getInstance().registerCommand(new WhereIsCommand());
        } else {
            CloudAPI.getInstance().unregisterCommand(new WhereIsCommand());
        }
        if (result.getBoolean("whereAmICommand")) {
            CloudAPI.getInstance().registerCommand(new WhereAmICommand());
        } else {
            CloudAPI.getInstance().unregisterCommand(new WhereAmICommand());
        }
        if (result.getBoolean("infoCommand")) {
            CloudAPI.getInstance().registerCommand(new NetworkCommand());
        } else {
            CloudAPI.getInstance().unregisterCommand(new NetworkCommand());
        }
    }


}
