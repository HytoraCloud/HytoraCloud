package de.lystx.hytoracloud.bridge.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestTPS;
import de.lystx.hytoracloud.driver.utils.reflection.Reflections;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.utils.minecraft.NetworkInfo;
import net.hytora.networking.elements.packet.response.ResponseStatus;

public class BukkitHandlerTPS implements PacketHandler {

    public void handle(HytoraPacket packet) {

       if (packet instanceof PacketRequestTPS) {
            PacketRequestTPS packetRequestTPS = (PacketRequestTPS)packet;

            if (packetRequestTPS.getServer().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {

                String tps;
                double[] arrayOfDouble = (double[]) Reflections.getField("recentTps","MinecraftServer","getServer");
                if (arrayOfDouble == null) return;
                byte b = 0;
                if (b < arrayOfDouble.length) {
                    tps = new NetworkInfo().formatTps(arrayOfDouble[b]);
                } else {
                    tps = "§cError";
                }

                packet.reply(ResponseStatus.SUCCESS, tps);

            }
        }
    }


}
