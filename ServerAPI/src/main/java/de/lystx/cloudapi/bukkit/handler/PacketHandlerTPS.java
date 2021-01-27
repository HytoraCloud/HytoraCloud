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
            Service service = ((PacketPlayOutTPS) packet).getService();
            if (((PacketPlayOutTPS) packet).getTps() == null && (service == null || service.getName().equalsIgnoreCase(cloudAPI.getService().getName()))) {
                String tps;
                double[] arrayOfDouble = (double[]) Reflections.getField("recentTps","MinecraftServer","getServer");
                DecimalFormat decimalFormat = new DecimalFormat("##.#");
                int i = arrayOfDouble.length;
                byte b = 0;
                if (b < i) {
                    double t = arrayOfDouble[b];
                    tps = decimalFormat.format(t);
                    if (tps.contains("20") || tps.contains("19") || tps.contains("18")) {
                        tps = (tps.contains("20") ? " *" : "") + "§a" + tps;
                    } else if (tps.contains("17") || tps.contains("16") || tps.contains("15")) {
                        tps = "§e" + tps;
                    } else if (tps.contains("14") || tps.contains("13") || tps.contains("12")) {
                        tps = "§6" + tps;
                    } else if (tps.contains("11") || tps.contains("10") || tps.contains("9")) {
                        tps = "§c" + tps;
                    } else {
                        tps = "§4" + tps;
                    }
                } else {
                    tps = "§cError";
                }
                cloudAPI.sendPacket(new PacketPlayOutTPS(((PacketPlayOutTPS) packet).getPlayer(), cloudAPI.getService(), tps));
            }
        }
    }
}
