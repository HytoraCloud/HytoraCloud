package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.utils.Reflections;
import de.lystx.cloudsystem.library.elements.packets.both.PacketTPS;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter @AllArgsConstructor
public class PacketHandlerTPS extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketTPS) {
            PacketTPS packetTPS = (PacketTPS)packet;
            Service service = packetTPS.getService();
            if (packetTPS.getTps() == null && service.getServiceGroup().getName().equalsIgnoreCase(cloudAPI.getService().getServiceGroup().getName())) {
                String tps;
                double[] arrayOfDouble = (double[]) Reflections.getField("recentTps","MinecraftServer","getServer");
                if (arrayOfDouble == null) return;
                byte b = 0;
                if (b < arrayOfDouble.length) {
                    tps = new NetworkInfo().formatTps(arrayOfDouble[b]);
                } else {
                    tps = "§cError";
                }
                PacketTPS tps1 = new PacketTPS(packetTPS.getPlayer(), cloudAPI.getService(), tps);
                tps1.setSendBack(false);
                cloudAPI.sendPacket(tps1);
            }
        }
    }


}
