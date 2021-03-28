package de.lystx.serverselector.cloud.manager.sign.layout;

import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;

/**
 * This is the {@link DefaultSignLayout}
 * if no LayOut was defined or found in the config
 * this LayOut will be saved and used afterwards
 */
public class DefaultSignLayout extends VsonObject {

    /**
     * Returns new Default SignLayOut
     */
    public DefaultSignLayout(VsonSettings... vsonSettings) {
        super(vsonSettings);
        VsonArray loadingLayouts = new VsonArray();
        VsonObject loading1 = new VsonObject();
        VsonObject loading2 = new VsonObject();
        VsonObject loading3 = new VsonObject();
        VsonObject loading4 = new VsonObject();

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

        loadingLayouts.append(loading1);
        loadingLayouts.append(loading2);
        loadingLayouts.append(loading3);
        loadingLayouts.append(loading4);
        this.append("repeatTick", 20);
        this.append("loadingLayout", loadingLayouts);
        this.append("otherLayouts", new VsonObject()
                .append("ONLINE", new VsonObject()
                        .append("0", "&8│ &b%server% &8│")
                        .append("1", "&aLobby")
                        .append("2", "%motd%")
                        .append("3", "&8× &7%online%&8/&7%max% &8×")
                )
                .append("FULL", new VsonObject()
                        .append("0", "&8│ &b%server% &8│")
                        .append("1", "&6VIP")
                        .append("2", "%motd%")
                        .append("3", "&8× &7%online%&8/&7%max% &8×")
                )
                .append("MAINTENANCE", new VsonObject()
                        .append("0", "")
                        .append("1", "&8│ &b%group% &8│")
                        .append("2", "&8× &cMaintenance &8×")
                        .append("3", "")
                )
        );
    }

}
