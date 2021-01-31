package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.manager.Reflections;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketPlayOutTPS;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
@AllArgsConstructor
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
                DecimalFormat decimalFormat = new DecimalFormat("##.#");
                int i = arrayOfDouble.length;
                byte b = 0;
                if (b < i) {
                    double t = arrayOfDouble[b];
                    tps = decimalFormat.format(t);
                    if (t >= 20) {
                        tps = "§2*" + tps;
                    } else if (t < 20 && t > 18) {
                        tps = "§a" + tps;
                    } else if (t < 18 && t > 15) {
                        tps = "§e" + tps;
                    } else if (t < 15 && t > 12) {
                        tps = "§6" + tps;
                    } else if (t < 12 && t > 10) {
                        tps = "§c" + tps;
                    } else {
                        tps = "§4" + tps;
                    }
                } else {
                    tps = "§cError";
                }
                PacketPlayOutTPS tps1 = new PacketPlayOutTPS(packetPlayOutTPS.getPlayer(), cloudAPI.getService(), tps);
                tps1.setSendBack(false);
                cloudAPI.sendPacket(tps1);
            }
        }
    }
}
