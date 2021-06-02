package de.lystx.hytoracloud.bridge.bukkit.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketTPS;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.util.reflection.Reflections;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.util.minecraft.NetworkInfo;

public class PacketHandlerTPS implements PacketHandler {

    public void handle(Packet packet) {
        if (packet instanceof PacketTPS) {
            PacketTPS packetTPS = (PacketTPS)packet;
            Service service = packetTPS.getService();
            if (packetTPS.getTps() == null && service.getServiceGroup().getName().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getServiceGroup().getName())) {
                String tps;
                double[] arrayOfDouble = (double[]) Reflections.getField("recentTps","MinecraftServer","getServer");
                if (arrayOfDouble == null) return;
                byte b = 0;
                if (b < arrayOfDouble.length) {
                    tps = new NetworkInfo().formatTps(arrayOfDouble[b]);
                } else {
                    tps = "§cError";
                }
                PacketTPS tps1 = new PacketTPS(packetTPS.getPlayer(), CloudDriver.getInstance().getThisService(), tps);
                tps1.setSendBack(false);
                CloudDriver.getInstance().sendPacket(tps1);
            }
        }
    }


}
