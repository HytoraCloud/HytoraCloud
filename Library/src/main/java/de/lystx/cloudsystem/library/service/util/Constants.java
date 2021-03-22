package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.featured.CloudPlayerInventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Constants {

    public static CloudExecutor EXECUTOR = null;
    public static final Executor THREAD_POOL = Executors.newFixedThreadPool(1);
    public static final String INTERNAL_RECEIVER = "InternalReceiver";

    public static PermissionPool PERMISSION_POOL = null;

    public static final PermissionGroup DEFAULT_PERMISSION_GROUP = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new SerializableDocument());

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
