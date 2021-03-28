package de.lystx.serverselector.spigot.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.both.PacketInformation;
import de.lystx.cloudsystem.library.service.util.AppendMap;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.serverselector.cloud.manager.sign.base.CloudSign;
import de.lystx.serverselector.spigot.SpigotSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.Map;

public class PacketHandlerManageSigns {

    @PacketHandler
    public void handleInformation(PacketInformation information) {
        if (information.getKey().equalsIgnoreCase("createSign")) {
            final Location location = Location.deserialize((Map<String, Object>) information.getData().get("location"));
            CloudPlayer player = CloudAPI.getInstance().getCloudPlayers().get((String) information.getData().get("player"));
            ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup((String) information.getData().get("group"));

            CloudSign sign = new CloudSign((int) location.getX(), (int) location.getY(), (int) location.getZ(), group.getName(), location.getWorld().getName());
            if (SpigotSelector.getInstance().getSignManager().getSignUpdater().getCloudSign(location) == null) {
                Block block = Bukkit.getWorld(sign.getWorld()).getBlockAt(sign.getX(), sign.getY(), sign.getZ());
                Sign signBlock = (Sign) block.getState();
                signBlock.setLine(0, "§8§m------");
                signBlock.setLine(1, "§b" + group.getName().toUpperCase());
                signBlock.setLine(2, "RELOADING...");
                signBlock.setLine(3, "§8§m------");
                signBlock.update(true);
                SpigotSelector.getInstance().getSignManager().getCloudSigns().add(sign);


                PacketInformation packetInformation = new PacketInformation("PacketInCreateSign", new AppendMap<String, Object>().append("sign", sign.serialize()));
                CloudAPI.getInstance().sendPacket(packetInformation);
                CloudAPI.getInstance().sendPacket(new PacketInServiceUpdate(CloudAPI.getInstance().getService()));
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You created a CloudSign for the group §b" + group.getName());
            } else {
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe §eCloudSign §calready exists!");
            }
        } else if (information.getKey().equalsIgnoreCase("deleteSign")) {
            final Location location = Location.deserialize((Map<String, Object>) information.getData().get("location"));
            CloudPlayer player = CloudAPI.getInstance().getCloudPlayers().get((String) information.getData().get("player"));

            CloudSign cloudSign = SpigotSelector.getInstance().getSignManager().getSignUpdater().getCloudSign(location);
            if (cloudSign == null) {
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis §eCloudSign §cseems not to be registered!");
                return;
            }
            Block block = Bukkit.getWorld(cloudSign.getWorld()).getBlockAt(cloudSign.getX(), cloudSign.getY(), cloudSign.getZ());
            Sign signBlock = (Sign) block.getState();
            signBlock.setLine(0, "§8§m------");
            signBlock.setLine(1, "§4⚠⚠⚠⚠⚠");
            signBlock.setLine(2, "§8» §cRemoved");
            signBlock.setLine(3, "§8§m------");
            signBlock.update(true);
            SpigotSelector.getInstance().getSignManager().getCloudSigns().remove(cloudSign);
            PacketInformation packetInformation = new PacketInformation("PacketInDeleteSign", new AppendMap<String, Object>().append("sign", cloudSign.serialize()));
            CloudAPI.getInstance().sendPacket(packetInformation);
            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You removed a CloudSign for the group §b" + cloudSign.getGroup().toUpperCase());

        }
    }

}
