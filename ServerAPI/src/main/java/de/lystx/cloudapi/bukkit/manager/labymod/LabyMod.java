package de.lystx.cloudapi.bukkit.manager.labymod;

import com.google.gson.*;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.CloudPlayerLabyModJoinEvent;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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

        CloudServer.getInstance().getServer().getMessenger().registerIncomingPluginChannel(CloudServer.getInstance(), "LABYMOD", (channel, player, bytes) -> {
            try {
                if (this.labyModUsers.contains(player.getUniqueId())) {
                    return;
                }
                this.labyModUsers.add(player.getUniqueId());
                Bukkit.getScheduler().runTask(CloudServer.getInstance(), () -> Bukkit.getPluginManager().callEvent(new CloudPlayerLabyModJoinEvent(player, packetUtils.readString(Unpooled.wrappedBuffer(bytes), 32767))));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });

        CloudServer.getInstance().getServer().getMessenger().registerIncomingPluginChannel(CloudServer.getInstance(), "LMC", (channel, player, bytes) -> {
            if (this.labyModUsers.contains(player.getUniqueId())) {
                return;
            }
            this.labyModUsers.add(player.getUniqueId());
            try {
                Bukkit.getScheduler().runTask(CloudServer.getInstance(), () -> Bukkit.getPluginManager().callEvent(new CloudPlayerLabyModJoinEvent(player, "3.7.7")));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }


    public void sendServerMessage(Player player, String messageKey, JsonElement messageContents) {
        messageContents = this.packetUtils.cloneJson(messageContents);
        this.packetUtils.sendPacket(player, this.packetUtils.getPluginMessagePacket("LMC", this.packetUtils.getBytesToSend(messageKey, messageContents.toString())));
    }


    public void sendClientToServer( Player player, String title, String address) {

        JsonObject object = new JsonObject();
        object.addProperty( "title", title ); // Title of the warning
        object.addProperty( "address", address ); // Destination server address
        object.addProperty( "preview", true ); // Display the server icon, motd and user count


        this.sendServerMessage( player, "server_switch", object );
    }
    private void updateGameInfo( Player player, boolean hasGame, String gamemode, long startTime, long endTime ) {

        JsonObject obj = new JsonObject();
        obj.addProperty( "hasGame", hasGame );

        if ( hasGame ) {
            obj.addProperty( "game_mode", gamemode );
            obj.addProperty( "game_startTime", startTime );
            obj.addProperty( "game_endTime", endTime );
        }

        this.sendServerMessage( player, "discord_rpc", obj );
    }

    public void setSubtitle( Player receiver, UUID subtitlePlayer, String value ) {
        JsonArray array = new JsonArray();
        JsonObject subtitle = new JsonObject();
        subtitle.addProperty( "uuid", subtitlePlayer.toString() );
        subtitle.addProperty( "size", 0.8d ); // Range is 0.8 - 1.6 (1.6 is Minecraft default)
        if(value != null)
            subtitle.addProperty( "value", value );
        array.add(subtitle);

        this.sendServerMessage( receiver, "account_subtitle", array );
    }

    public void sendCurrentPlayingGamemode( Player player, boolean visible, String gamemodeName ) {
        JsonObject object = new JsonObject();
        object.addProperty( "show_gamemode", visible ); // Gamemode visible for everyone
        object.addProperty( "gamemode_name", gamemodeName ); // Name of the current playing gamemode

        this.sendServerMessage( player, "server_gamemode", object );
    }

    public void sendCineScope( Player player, int coveragePercent, long duration ) {
        JsonObject object = new JsonObject();
        object.addProperty( "coverage", coveragePercent );
        object.addProperty( "duration", duration );
        this.sendServerMessage( player, "cinescopes", object );
    }

    public void sendMutedPlayerTo( Player player, UUID mutedPlayer, boolean muted ) {
        JsonObject voicechatObject = new JsonObject();
        JsonObject mutePlayerObject = new JsonObject();

        mutePlayerObject.addProperty("mute", muted );
        mutePlayerObject.addProperty("target", String.valueOf(mutedPlayer));

        voicechatObject.add("mute_player", mutePlayerObject);

        this.sendServerMessage( player, "voicechat", voicechatObject );
    }

    public void disableVoiceChat( Player player ) {
        JsonObject object = new JsonObject();
        object.addProperty( "allowed", false );
        this.sendServerMessage( player, "voicechat", object );
    }

    public boolean isLabyMod(UUID uuid) {
        return this.labyModUsers.contains(uuid);
    }

    @EventHandler
    public void onLabyModJoin(CloudPlayerLabyModJoinEvent event) {

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.labyModUsers.remove(player.getUniqueId());
    }
}
