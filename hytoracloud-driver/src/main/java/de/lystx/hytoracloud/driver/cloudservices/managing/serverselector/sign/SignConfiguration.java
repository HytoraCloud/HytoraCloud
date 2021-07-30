package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign;

import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Getter @AllArgsConstructor
public class SignConfiguration implements Serializable {

    private static final long serialVersionUID = -4620016682091731021L;

    /**
     * The knockback config
     */
    private final KnockbackConfig knockBackConfig;

    /**
     * The loading animation
     */
    private final SignAnimation loadingLayout;

    /**
     * The online sign layout
     */
    private final SignLayout onlineLayout;

    /**
     * The full sign layout
     */
    private final SignLayout fullLayout;

    /**
     * The full sign layout
     */
    private final SignLayout maintenanceLayout;


    /**
     * Creates a default {@link SignConfiguration}
     *
     * @return config
     */
    public static SignConfiguration createDefault() {

        //The default layouts
        SignLayout online = new SignLayout("ONLINE", new String[]{"&8│ &b%server% &8│", "&aLobby", "%motd%", "&8× &7%online%&8/&7%max% &8×"}, "STAINED_CLAY", 5);
        SignLayout full = new SignLayout("FULL", new String[]{"&8│ &b%server% &8│", "&6VIP", "%motd%", "&8× &7%online%&8/&7%max% &8×"}, "STAINED_CLAY", 1);
        SignLayout maintenance = new SignLayout("MAINTENANCE", new String[]{"", "&8│ &b%group% &8│", "&8× &cMaintenance &8×", ""}, "STAINED_CLAY", 3);

        //The loading animation
        SignLayout loading1 = new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &a⬛&7⬛⬛", ""}, "STAINED_CLAY", 14);
        SignLayout loading2 = new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &a⬛⬛&7⬛", ""}, "STAINED_CLAY", 14);
        SignLayout loading3 = new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &a⬛⬛⬛", ""}, "STAINED_CLAY", 14);
        SignLayout loading4 = new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &7⬛⬛⬛", ""}, "STAINED_CLAY", 14);

        return new SignConfiguration(
                new KnockbackConfig(
                        true,
                        0.7,
                        0.5,
                        "cloudsystem.signs.bypass"
                ),
                new SignAnimation(20, loading1, loading2, loading3, loading4), online, full, maintenance);
    }
}
