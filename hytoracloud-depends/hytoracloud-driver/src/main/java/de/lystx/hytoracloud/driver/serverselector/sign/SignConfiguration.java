package de.lystx.hytoracloud.driver.serverselector.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

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
     * The maintenance sign layout
     */
    private final SignLayout maintenanceLayout;

    /**
     * The starting sign layout
     */
    private final SignLayout startingLayOut;


    /**
     * Creates a default {@link SignConfiguration}
     *
     * @return config
     */
    public static SignConfiguration createDefault() {

        //The default layouts
        SignLayout online = new SignLayout("ONLINE", new String[]{"&8│ &b%server% &8│", "&aAvailable", "%motd%", "&8× &7%online%&8/&7%max% &8×"}, "STAINED_CLAY", 5);
        SignLayout full = new SignLayout("FULL", new String[]{"&8│ &b%server% &8│", "&6VIP", "%motd%", "&8× &7%online%&8/&7%max% &8×"}, "STAINED_CLAY", 1);
        SignLayout maintenance = new SignLayout("MAINTENANCE", new String[]{"", "&8│ &b%group% &8│", "&8× &cMaintenance &8×", ""}, "STAINED_CLAY", 3);
        SignLayout starting = new SignLayout("STARTING", new String[]{"", "&8│ &e%server% &8│", "&8× &0Starting... &8×", ""}, "STAINED_CLAY", 4);

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
                new SignAnimation(20, loading1, loading2, loading3, loading4), online, full, maintenance, starting);
    }
}
