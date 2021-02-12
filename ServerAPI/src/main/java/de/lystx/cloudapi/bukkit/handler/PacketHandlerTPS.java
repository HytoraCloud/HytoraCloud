package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.manager.Reflections;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketPlayOutTPS;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter @AllArgsConstructor
public class PacketHandlerTPS extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutTPS) {
            PacketPlayOutTPS packetPlayOutTPS = (PacketPlayOutTPS)packet;
            Service service = packetPlayOutTPS.getService();
            if (packetPlayOutTPS.getTps() == null && service.getServiceGroup().getName().equalsIgnoreCase(cloudAPI.getService().getServiceGroup().getName())) {
                String tps;
                double[] arrayOfDouble = (double[]) Reflections.getField("recentTps","MinecraftServer","getServer");
                if (arrayOfDouble == null) return;
                byte b = 0;
                if (b < arrayOfDouble.length) {
                    tps = this.format(arrayOfDouble[b]);
                } else {
                    tps = "§cError";
                }
                PacketPlayOutTPS tps1 = new PacketPlayOutTPS(packetPlayOutTPS.getPlayer(), cloudAPI.getService(), tps);
                tps1.setSendBack(false);
                cloudAPI.sendPacket(tps1);
            }
        }
    }

    String format(double tps) {
        return (tps > 50.0D ? ChatColor.GREEN : (tps > 30.0D ? ChatColor.YELLOW : ChatColor.RED)).toString() + (tps > 60.0D ? "*" : "") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 60.0D);
    }
}
