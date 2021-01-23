package de.lystx.cloudapi.bukkit.manager.labymod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import lombok.Getter;
import net.labymod.serverapi.Addon;
import net.labymod.serverapi.bukkit.LabyModPlugin;
import net.labymod.serverapi.bukkit.event.LabyModPlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

@Getter
public class LabyMod implements Listener {

    private final CloudAPI cloudAPI;
    private final List<UUID> labyModUsers;
    private final Map<UUID, List<Addon>> addons;

    public LabyMod(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.labyModUsers = new LinkedList<>();
        this.addons = new HashMap<>();
        CloudServer.getInstance().getServer().getPluginManager().registerEvents(this, CloudServer.getInstance());
    }

    public void sendClientToServer( Player player, String title, String address) {

        JsonObject object = new JsonObject();
        object.addProperty( "title", title ); // Title of the warning
        object.addProperty( "address", address ); // Destination server address
        object.addProperty( "preview", true ); // Display the server icon, motd and user count

        LabyModPlugin.getInstance().sendServerMessage( player, "server_switch", object );
    }

    private void updateGameInfo( Player player, boolean hasGame, String gamemode, long startTime, long endTime ) {

        // Create game json object
        JsonObject obj = new JsonObject();
        obj.addProperty( "hasGame", hasGame );

        if ( hasGame ) {
            obj.addProperty( "game_mode", gamemode );
            obj.addProperty( "game_startTime", startTime ); // Set to 0 for countdown
            obj.addProperty( "game_endTime", endTime ); // // Set to 0 for timer
        }

        // Send to user
        LabyModPlugin.getInstance().sendServerMessage( player, "discord_rpc", obj );
    }

    public void setSubtitle( Player receiver, UUID subtitlePlayer, String value ) {
        // List of all subtitles
        JsonArray array = new JsonArray();

        // Add subtitle
        JsonObject subtitle = new JsonObject();
        subtitle.addProperty( "uuid", subtitlePlayer.toString() );

        // Optional: Size of the subtitle
        subtitle.addProperty( "size", 0.8d ); // Range is 0.8 - 1.6 (1.6 is Minecraft default)

        // no value = remove the subtitle
        if(value != null)
            subtitle.addProperty( "value", value );

        // You can set multiple subtitles in one packet
        array.add(subtitle);

        // Send to LabyMod using the API
        LabyModPlugin.getInstance().sendServerMessage( receiver, "account_subtitle", array );
    }

    public void sendCurrentPlayingGamemode( Player player, boolean visible, String gamemodeName ) {
        JsonObject object = new JsonObject();
        object.addProperty( "show_gamemode", visible ); // Gamemode visible for everyone
        object.addProperty( "gamemode_name", gamemodeName ); // Name of the current playing gamemode

        // Send to LabyMod using the API
        LabyModPlugin.getInstance().sendServerMessage( player, "server_gamemode", object );
    }

    public void sendCineScope( Player player, int coveragePercent, long duration ) {
        JsonObject object = new JsonObject();

        // Cinescope height (0% - 50%)
        object.addProperty( "coverage", coveragePercent );

        // Duration
        object.addProperty( "duration", duration );

        // Send to LabyMod using the API
        LabyModPlugin.getInstance().sendServerMessage( player, "cinescopes", object );
    }

    public void sendMutedPlayerTo( Player player, UUID mutedPlayer, boolean muted ) {
        JsonObject voicechatObject = new JsonObject();
        JsonObject mutePlayerObject = new JsonObject();

        mutePlayerObject.addProperty("mute", muted );
        mutePlayerObject.addProperty("target", String.valueOf(mutedPlayer));

        voicechatObject.add("mute_player", mutePlayerObject);

        // Send to LabyMod using the API
        LabyModPlugin.getInstance().sendServerMessage( player, "voicechat", voicechatObject );
    }

    public void disableVoiceChat( Player player ) {
        JsonObject object = new JsonObject();

        // Disable the voice chat for this player
        object.addProperty( "allowed", false );

        // Send to LabyMod using the API
        LabyModPlugin.getInstance().sendServerMessage( player, "voicechat", object );
    }

    public boolean isLabyMod(UUID uuid) {
        return this.labyModUsers.contains(uuid);
    }

    public List<Addon> getAddons(UUID uuid) {
        return this.addons.getOrDefault(uuid, new LinkedList<>());
    }

    @EventHandler
    public void onJoin(LabyModPlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.labyModUsers.add(player.getUniqueId());
        this.addons.put(player.getUniqueId(), event.getAddons());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.labyModUsers.remove(player.getUniqueId());
        this.addons.remove(player.getUniqueId());
    }
}
