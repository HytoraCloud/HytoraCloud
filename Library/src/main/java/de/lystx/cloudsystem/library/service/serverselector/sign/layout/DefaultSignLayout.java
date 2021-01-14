package de.lystx.cloudsystem.library.service.serverselector.sign.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lystx.cloudsystem.library.elements.other.Document;


public class DefaultSignLayout extends Document {


    public DefaultSignLayout() {

        JsonArray loadingLayouts = new JsonArray();
        JsonObject loading1 = new JsonObject();
        JsonObject loading2 = new JsonObject();
        JsonObject loading3 = new JsonObject();
        JsonObject loading4 = new JsonObject();

        loading1.addProperty("0", "&a");
        loading1.addProperty("1", "&8│ &bLoading... &8│");
        loading1.addProperty("2", "&7%group% &8x &a⬛&7⬛⬛");
        loading1.addProperty("3", "&7");

        loading2.addProperty("0", "&a");
        loading2.addProperty("1", "&8│ &bLoading... &8│");
        loading2.addProperty("2", "&7%group% &8x &a⬛⬛&7⬛");
        loading2.addProperty("3", "&7");

        loading3.addProperty("0", "&a");
        loading3.addProperty("1", "&8│ &bLoading... &8│");
        loading3.addProperty("2", "&7%group% &8x &a⬛⬛⬛");
        loading3.addProperty("3", "&7");

        loading4.addProperty("0", "&a");
        loading4.addProperty("1", "&8│ &bLoading... &8│");
        loading4.addProperty("2", "&7%group% &8x &7⬛⬛⬛");
        loading4.addProperty("3", "&7");

        loadingLayouts.add(loading1);
        loadingLayouts.add(loading2);
        loadingLayouts.add(loading3);
        loadingLayouts.add(loading4);
        this.append("repeatTick", 20);
        this.append("loadingLayout", loadingLayouts);
        this.append("otherLayouts", new Document()
                .append("ONLINE", new Document()
                        .append("0", "&8│ &b%server% &8│")
                        .append("1", "&aLobby")
                        .append("2", "%motd%")
                        .append("3", "&8× &7%online%&8/&7%max% &8×")
                )
                .append("FULL", new Document()
                        .append("0", "&8│ &b%server% &8│")
                        .append("1", "&6VIP")
                        .append("2", "%motd%")
                        .append("3", "&8× &7%online%&8/&7%max% &8×")
                )
                .append("MAINTENANCE", new Document()
                        .append("0", "")
                        .append("1", "&8│ &b%group% &8│")
                        .append("2", "&8× &cMaintenance &8×")
                        .append("3", "")
                )
        );
    }
}
