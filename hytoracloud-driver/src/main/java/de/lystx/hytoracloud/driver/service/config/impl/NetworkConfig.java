package de.lystx.hytoracloud.driver.service.config.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketUpdateNetworkConfig;
import io.thunder.packet.PacketBuffer;
import de.lystx.hytoracloud.driver.service.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.service.config.impl.fallback.FallbackConfig;
import de.lystx.hytoracloud.driver.service.config.impl.labymod.LabyModConfig;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.GlobalProxyConfig;
import io.thunder.utils.objects.ThunderObject;
import io.vson.elements.object.Objectable;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class NetworkConfig implements Serializable, ThunderObject {

    private String host;
    private Integer port;
    private boolean setupDone;
    private boolean autoUpdater;

    private GlobalProxyConfig networkConfig;
    private LabyModConfig labyModConfig;
    private MessageConfig messageConfig;
    private FallbackConfig fallbackConfig;


    /**
     * Create default Config for everything
     *
     * @return default config
     */
    public static NetworkConfig defaultConfig() {
        return new NetworkConfig("0",
                1401,
                false,
                false,
                new GlobalProxyConfig(
                        25565,
                        30000,
                        false,
                        true,
                        true,
                        new LinkedList<>()
                ),
                new LabyModConfig(
                        false,
                        "§8» §7HytoraCloud §8× §b%service% §8[§b%online_players%§8/§b%max_players%§8]",
                        true
                ),
                new MessageConfig(
                        "§8» §bCloud §8┃ §7",
                        "%prefix%§7The server §a%server% §7is now starting§8...",
                        "%prefix%§7The server §c%server% §7is now stopping§8...",
                        "%prefix%§cYou are already on a lobbyserver!",
                        "%prefix%§cNo lobbyserver could be found!",
                        "%prefix%§cThe network is not available for you at this time",
                        "%prefix%§cThe CloudSystem is still booting up! There are no servers to connect on at this time!",
                        "%prefix%§cThe servergroup §e%group% §cis in maintenance!",
                        "%prefix%§cYou are alread connected to this service!",
                        "%prefix%§cYou are alread on the network!",
                        "%prefix%§cThis server was shut down!",
                        "%prefix%§cAn error occured§8: §e%error%"
                        ),
                new FallbackConfig(
                        new Fallback(1, "Lobby", null),
                        new ArrayList<>()
                )
        );
    }


    @SneakyThrows @Override
    public void write(PacketBuffer buf) {

        buf.writeString(host);
        buf.writeInt(port);
        buf.writeBoolean(setupDone);
        buf.writeBoolean(autoUpdater);

        buf.writeThunderObject(networkConfig);
        buf.writeThunderObject(labyModConfig);
        buf.writeThunderObject(messageConfig);
        buf.writeThunderObject(fallbackConfig);
    }

    @Override
    public void read(PacketBuffer buf) {
        host = buf.readString();
        port = buf.readInt();
        setupDone = buf.readBoolean();
        autoUpdater = buf.readBoolean();

        networkConfig = buf.readThunderObject(GlobalProxyConfig.class);
        labyModConfig = buf.readThunderObject(LabyModConfig.class);
        messageConfig = buf.readThunderObject(MessageConfig.class);
        fallbackConfig = buf.readThunderObject(FallbackConfig.class);
    }


    public void update() {
        CloudDriver.getInstance().sendPacket(new PacketUpdateNetworkConfig(this));
    }
}
