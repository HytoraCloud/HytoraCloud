package de.lystx.cloudapi.bukkit.manager.labymod;

import com.google.gson.*;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.other.MessageReceiveEvent;
import de.lystx.cloudapi.bukkit.events.player.CloudPlayerLabyModJoinEvent;
import de.lystx.cloudsystem.library.service.player.featured.labymod.LabyModAddon;
import de.lystx.cloudsystem.library.service.player.featured.labymod.LabyModPlayer;
import de.lystx.cloudsystem.library.service.player.featured.labymod.VoiceChatSettings;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.IOException;
import java.util.*;

@Getter
public class LabyMod implements Listener {

    private final CloudAPI cloudAPI;
    private final List<UUID> labyModUsers;
    private final PacketUtils packetUtils;

    public LabyMod(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.packetUtils = new PacketUtils();
        this.labyModUsers = new LinkedList<>();

        CloudServer.getInstance().getServer().getPluginManager().registerEvents(this, CloudServer.getInstance());

        CloudServer.getInstance().getServer().getMessenger().registerIncomingPluginChannel( CloudServer.getInstance(), "LMC", (channel, player, bytes) -> {
            ByteBuf buf = Unpooled.wrappedBuffer( bytes );
            if (bytes.length > 32767) {
                return;
            }
            try {
                final String messageKey = getPacketUtils().readString( buf, Short.MAX_VALUE );
                final String messageContents = getPacketUtils().readString( buf, Short.MAX_VALUE );

                try {
                    if (messageContents.trim().startsWith("{}")) {
                        return;
                    }
                    if (messageContents.trim().equalsIgnoreCase("{}")) {
                        return;
                    }

                    Bukkit.getScheduler().runTask( CloudServer.getInstance(), () -> {
                        if (player.isOnline()) {
                            try {
                                Bukkit.getPluginManager().callEvent( new MessageReceiveEvent( player, messageKey, new JsonParser().parse( messageContents ) ) );
                            } catch (Exception ignored) {}
                        }
                    });
                } catch (Exception ignored) {}

            } catch (RuntimeException ignored) {}
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.labyModUsers.remove(player.getUniqueId());
    }


    @EventHandler @SneakyThrows
    public void handleMessage(MessageReceiveEvent event) {
        try {

            final String messageKey = event.getMessageKey();
            final Player player = event.getPlayer();

            if ( messageKey.equals( "INFO" ) ) {

                final VsonParser vsonParser = new VsonParser();
                final VsonValue vsonValue = vsonParser.parse(event.getJsonMessage().toString());

                VsonObject vsonObject = (VsonObject) vsonValue;
                CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(player.getName());
                LabyModPlayer labyModPlayer = new LabyModPlayer(player.getName(), vsonObject.getString("version"));
                for (VsonValue value : vsonObject.getArray("addons")) {
                    VsonObject addon = ((VsonObject)value);
                    final LabyModAddon labyModAddon = LabyModAddon.byName(addon.getString("name"));
                    if (labyModAddon == null) {
                        continue;
                    }
                    labyModPlayer.getAddons().add(labyModAddon);
                }
                cloudPlayer.setLabyModPlayer(labyModPlayer);
                cloudPlayer.update();

                if (labyModUsers.contains(player.getUniqueId())) {
                    return;
                }
                labyModUsers.add(player.getUniqueId());
                Bukkit.getPluginManager().callEvent(new CloudPlayerLabyModJoinEvent(cloudPlayer, vsonObject.has( "version" ) && vsonObject.get( "version" ).isString() ? vsonObject.get( "version" ).asString() : "Unknown"));

            } else if (messageKey.equals("voicechat")) {

                if (labyModUsers.contains(player.getUniqueId())) {
                    return;
                }
                this.doSettings(player, event);
            }
        } catch (Exception e) {
            //JUST IGNORING
        }
    }

    /**
     * Caches the Settings of LabyMod
     * VoiceChat for a CloudPlayer
     * @param player
     * @param event
     */
    public void doSettings(Player player, MessageReceiveEvent event) {

        CloudAPI.getInstance().getCloudPlayers().getAsync(player.getName(), cloudPlayer -> {
            if (cloudPlayer == null) {
                System.out.println("[LabyMod] CloudPlayer for VoiceChat settings was not found!");
                return;
            }
            try {
                VsonObject vc = new VsonObject(event.getJsonMessage().toString());
                LabyModPlayer labyModPlayer = cloudPlayer.getLabyModPlayer();
                if (labyModPlayer == null) {
                    CloudAPI.getInstance().getScheduler().scheduleDelayedTask(() -> this.doSettings(player, event), 10L);
                    return;
                }
                labyModPlayer.setVoiceChatSettings(new VoiceChatSettings(
                        vc.getBoolean("enabled"),
                        vc.getInteger("surround_range"),
                        vc.getInteger("surround_volume"),
                        vc.getBoolean("screamer_protection"),
                        vc.getInteger("screamer_protection_level"),
                        vc.getInteger("screamer_max_volume"),
                        vc.getInteger("microphone_volume")
                ));

                cloudPlayer.setLabyModPlayer(labyModPlayer);
                cloudPlayer.update();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
