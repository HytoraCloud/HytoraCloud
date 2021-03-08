package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.featured.CloudPlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Constants {

    public static CloudExecutor EXECUTOR = null;
    public static PermissionPool PERMISSION_POOL = null;

    public static final String PASTE_SERVER_URL_DOCUMENTS = "https://paste.labymod.net/documents";
    public static final String PASTE_SERVER_URL = "https://paste.labymod.net/";
    public static final String PASTE_SERVER_URL_RAW = "https://paste.labymod.net/raw/";

    public static boolean NEEDS_DEPENDENCIES;
    public static boolean JLINE_COMPLETER_INSTALLED;

    public static final Map<UUID, CloudPlayerInventory> INVENTORIES = new HashMap<>();

    /**
     * Checks if dependencies are already loaded
     */
    static {
        try {
            Class.forName("jline.console.ConsoleReader");
            NEEDS_DEPENDENCIES = false;
        } catch (ClassNotFoundException e) {
            NEEDS_DEPENDENCIES =  true;
        }
        try {
            Class.forName("jline.console.completer.Completer");
            JLINE_COMPLETER_INSTALLED = false;
        } catch (ClassNotFoundException e) {
            JLINE_COMPLETER_INSTALLED =  true;
        }
    }
}
