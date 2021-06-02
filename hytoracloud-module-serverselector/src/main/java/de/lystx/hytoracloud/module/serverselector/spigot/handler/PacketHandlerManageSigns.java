package de.lystx.hytoracloud.module.serverselector.spigot.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;

import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.util.utillity.CloudMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.Map;

public class PacketHandlerManageSigns implements PacketHandler {

    
    public void handleInformation(PacketInformation information) {
        if (information.getKey().equalsIgnoreCase("createSign")) {
            final Location location = Location.deserialize((Map<String, Object>) information.getObjectMap().get("location"));
            CloudPlayer player = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer((String) information.getObjectMap().get("player"));
            ServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup((String) information.getObjectMap().get("group"));

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


                PacketInformation packetInformation = new PacketInformation("PacketInCreateSign", new CloudMap<String, Object>().append("sign", sign.serialize()));
                CloudDriver.getInstance().sendPacket(packetInformation);
                CloudDriver.getInstance().getThisService().update();
                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7You created a CloudSign for the group §b" + group.getName());
            } else {
                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe §eCloudSign §calready exists!");
            }
        } else if (information.getKey().equalsIgnoreCase("deleteSign")) {
            final Location location = Location.deserialize((Map<String, Object>) information.getObjectMap().get("location"));
            CloudPlayer player = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer((String) information.getObjectMap().get("player"));

            CloudSign cloudSign = SpigotSelector.getInstance().getSignManager().getSignUpdater().getCloudSign(location);
            if (cloudSign == null) {
                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThis §eCloudSign §cseems not to be registered!");
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
            PacketInformation packetInformation = new PacketInformation("PacketInDeleteSign", new CloudMap<String, Object>().append("sign", cloudSign.serialize()));
            CloudDriver.getInstance().sendPacket(packetInformation);
            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7You removed a CloudSign for the group §b" + cloudSign.getGroup().toUpperCase());

        }
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInformation) {
            this.handleInformation((PacketInformation) packet);
        }
    }
}
