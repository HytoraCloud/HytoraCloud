package de.lystx.hytoracloud.driver.service.player.featured.labymod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.util.reflection.PacketUtils;
import de.lystx.hytoracloud.driver.service.util.reflection.Reflections;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

@Getter @Setter
public class LabyModPlayer implements Serializable {

    private final String version;
    private final String name;
    private final List<LabyModAddon> addons;
    private VoiceChatSettings voiceChatSettings;

    public LabyModPlayer(String name, String version) {
        this.version = version;
        this.name = name;
        this.addons = new LinkedList<>();
    }

    /**
     * Updates the gameMode of a {@link LabyModPlayer}
     * this will show up as an achievement to all of your friends
     * in LabyMod V3
     * @param gamemode
     */
    public void updateGamemode(String gamemode) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);
            this.sendServerMessage( player, "server_gamemode", new VsonObject()
                    .append("show_gamemode", true)
                    .append("gamemode_name", gamemode)
                    .toJson());
        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }

    /**
     * Recommends addons for the player
     * @param addons
     */
    public void recommendAddon(LabyModAddon... addons) {
        UUID[] uuids = new UUID[addons.length];
        for (int i = 0; i < addons.length; i++) {
            uuids[i] = addons[i].getUuid();
        }
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);

            JsonObject object = new JsonObject();
            JsonArray finalAddons = new JsonArray();

            for (UUID uuid1 : uuids) {
                JsonObject addon = new JsonObject();
                addon.addProperty( "uuid", uuid1.toString());
                addon.addProperty( "required", true );
                finalAddons.add( addon );
            }

            object.add( "addons", finalAddons );

            this.sendServerMessage( player, "addon_recommendation", object );
        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }

    /**
     * This will disable the VoiceChat by LabyMod
     * for this {@link LabyModPlayer}
     */
    public void disableVoicechat() {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);
            this.sendServerMessage(player, "voicechat", new VsonObject().append("allowed", false).toJson());
        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }

    /**
     * Disables / Enables voiceChat for a specific player
     * (Mutes / Unmutes the player for yourSelf)
     *
     * @param uniqueId
     */
    public void mutePlayerForSelf(UUID uniqueId, boolean mute) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);

            VsonObject vsonObject = new VsonObject();

            vsonObject.append("mute_player",
                    new VsonObject()
                    .append("mute", mute)
                    .append("target", uniqueId.toString())
            );

            this.sendServerMessage( player, "voicechat", vsonObject.toJson() );
        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }

    /**
     * This feature will show a LabyMod banner in the bottom right corner.
     * It was made for LabyMod tournaments.
     * A special feature of this watermark is that the server can force an emote to the player without changing the UUID.
     * In addition, the table list gets a new design, matching the scoreboard settings.
     *
     * Code: https://docs.labymod.net/pages/server/watermark/
     */
    public void sendWatermark(boolean visible) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);
            this.sendServerMessage( player, "watermark", new VsonObject().append("visible", visible).toJson());
        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }
    /**
     * This sets a Subtitle under a player
     * This requires (of course) LabyMod to be able
     * to view those subtitles
     *
     * Code : https://docs.labymod.net/pages/server/subtitles/
     * @param value
     */
    @SneakyThrows
    public void setSubtitle(String value) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);

            final Class<?> aClass = Class.forName("org.bukkit.Bukkit");
            final Method getOnlinePlayers = aClass.getDeclaredMethod("getOnlinePlayers");
            getOnlinePlayers.setAccessible(true);

            List<Object> list = new LinkedList<>();
            if (getOnlinePlayers.invoke(aClass) instanceof Object[]) {
                Object[] collection = (Object[]) getOnlinePlayers.invoke(aClass);
                list.addAll(Arrays.asList(collection));
            } else {
                Collection<Object> invoke = (Collection<Object>) getOnlinePlayers.invoke(aClass);
                list.addAll(invoke);
            }

            UUID playerUUID = Reflections.getUniqueId(player);

            for (Object o : list) {
                UUID uuid = Reflections.getUniqueId(o);

                if (Reflections.getUniqueId(o).equals(playerUUID)) {

                    JsonArray array = new JsonArray();
                    JsonObject subtitle = new JsonObject();
                    subtitle.addProperty( "uuid", uuid.toString());
                    subtitle.addProperty( "size", 0.8d );
                    if(value != null)  subtitle.addProperty( "value", value );
                    array.add(subtitle);

                    this.sendServerMessage( player, "account_subtitle", array);
                }
                JsonArray array = new JsonArray();
                JsonObject subtitle = new JsonObject();
                subtitle.addProperty( "uuid", playerUUID.toString());
                subtitle.addProperty( "size", 0.8d );
                if(value != null) subtitle.addProperty( "value", value );
                array.add(subtitle);
                this.sendServerMessage( o, "account_subtitle", array );
            }

        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }


    /**
     * Sends a Server Message to a Player
     * @param player
     * @param messageKey
     * @param messageContents
     */
    private void sendServerMessage(Object player, String messageKey, JsonElement messageContents) {
        PacketUtils packetUtils = new PacketUtils();
        messageContents = packetUtils.cloneJson(messageContents);
        packetUtils.sendPacket(player, packetUtils.getPluginMessagePacket("LMC", packetUtils.getBytesToSend(messageKey, messageContents.toString())));
    }

    /*
     * This will send a Client to a server
     * It will send you a preview of the Server you are trying to
     * connect with (if you haven't clicked "trust always")
     *
     * Code: https://docs.labymod.net/pages/server/server_switch/
     * @param title Title which will be displayed
     * @param address The Address of the server
     */
    public void sendClientToServer(String title, String address) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);

            this.sendServerMessage( player, "server_switch",
                    new VsonObject()
                            .append("title", title)
                            .append("address", address)
                            .append("preview", true)
                            .toJson());
        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }

    /**
     * It is possible to use Cinescopes (Black bars) for gamemode cinematics.
     *
     * Code: https://docs.labymod.net/pages/server/cinescopes/
     * @param coveragePercent
     * @param duration
     */
    public void sendCineScope(int coveragePercent, long duration ) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDAPI)) {
            Object player = Reflections.getBukkitPlayer(this.name);

            this.sendServerMessage( player, "cinescopes", new VsonObject()
                    .append("coverage", coveragePercent)
                    .append("duration", duration)
                    .toJson());
        } else {
            //TODO: LABYMOD METHOD FROM CLOUD SIDE
        }
    }

}
