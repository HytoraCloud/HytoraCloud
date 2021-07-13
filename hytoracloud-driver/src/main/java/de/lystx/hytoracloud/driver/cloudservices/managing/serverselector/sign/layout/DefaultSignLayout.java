package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.layout;

import com.google.gson.JsonArray;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;

/**
 * This is the {@link DefaultSignLayout}
 * if no LayOut was defined or found in the config
 * this LayOut will be saved and used afterwards
 */
public class DefaultSignLayout extends JsonEntity {

    /**
     * Returns new Default SignLayOut
     */
    public DefaultSignLayout() {


        JsonArray loadingLayouts = new JsonArray();
        JsonEntity loading1 = new JsonEntity();
        JsonEntity loading2 = new JsonEntity();
        JsonEntity loading3 = new JsonEntity();
        JsonEntity loading4 = new JsonEntity();

        loading1.append("0", "&a");
        loading1.append("1", "&8│ &bLoading... &8│");
        loading1.append("2", "&7%group% &8x &a⬛&7⬛⬛");
        loading1.append("3", "&7");

        loading2.append("0", "&a");
        loading2.append("1", "&8│ &bLoading... &8│");
        loading2.append("2", "&7%group% &8x &a⬛⬛&7⬛");
        loading2.append("3", "&7");

        loading3.append("0", "&a");
        loading3.append("1", "&8│ &bLoading... &8│");
        loading3.append("2", "&7%group% &8x &a⬛⬛⬛");
        loading3.append("3", "&7");

        loading4.append("0", "&a");
        loading4.append("1", "&8│ &bLoading... &8│");
        loading4.append("2", "&7%group% &8x &7⬛⬛⬛");
        loading4.append("3", "&7");

        loadingLayouts.add(loading1.getJsonObject());
        loadingLayouts.add(loading2.getJsonObject());
        loadingLayouts.add(loading3.getJsonObject());
        loadingLayouts.add(loading4.getJsonObject());
        this.append("repeatTick", 20);
        this.append("loadingLayout", loadingLayouts);
        this.append("otherLayouts", new JsonEntity()
                .append("ONLINE", new JsonEntity()
                        .append("0", "&8│ &b%server% &8│")
                        .append("1", "&aLobby")
                        .append("2", "%motd%")
                        .append("3", "&8× &7%online%&8/&7%max% &8×")
                )
                .append("FULL", new JsonEntity()
                        .append("0", "&8│ &b%server% &8│")
                        .append("1", "&6VIP")
                        .append("2", "%motd%")
                        .append("3", "&8× &7%online%&8/&7%max% &8×")
                )
                .append("MAINTENANCE", new JsonEntity()
                        .append("0", "")
                        .append("1", "&8│ &b%group% &8│")
                        .append("2", "&8× &cMaintenance &8×")
                        .append("3", "")
                )
        );
    }

}