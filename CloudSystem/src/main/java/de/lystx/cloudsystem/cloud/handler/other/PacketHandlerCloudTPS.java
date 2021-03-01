package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInTPS;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter @AllArgsConstructor
public class PacketHandlerCloudTPS extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInTPS) {
            CloudPlayer player = cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(((PacketPlayInTPS) packet).getPlayerName());
            if (player == null) {
                return;
            }
            double d = cloudSystem.getTicksPerSecond().getTPS();
            int raw = (int)d;
            DecimalFormat decimalFormat = new DecimalFormat("##.##");
            String tps;
            if (raw >= 20) {
                tps = "§a*" + decimalFormat.format(d);
            } else if (raw == 19 || raw == 18) {
                tps = "§a" + decimalFormat.format(d);
            } else if (raw > 14) {
                tps = "§6" + decimalFormat.format(d);
            } else if (raw < 14 && raw > 10) {
                tps = "§c" + decimalFormat.format(d);
            } else {
                tps = "§4" + decimalFormat.format(d);
            }

            player.sendMessage(cloudSystem.getService(ConfigService.class).getNetworkConfig().getMessageConfig().getPrefix().replace("&", "§") +
                    "§6TPS§8: §b" + tps);
        }
    }
}
