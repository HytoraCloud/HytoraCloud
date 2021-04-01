package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.elements.list.Filter;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Constants {

    public static CloudExecutor EXECUTOR = null;
    public static CloudType CLOUD_TYPE = CloudType.LIBRARY;
    public static ServiceType SERVICE_TYPE = ServiceType.SPIGOT;
    public static String BUKKIT_VERSION;
    public static final Executor THREAD_POOL = Executors.newFixedThreadPool(1);
    public static Filter<Service> SERVICE_FILTER = new Filter<>(new LinkedList<>());
    public static Filter<CloudPlayer> CLOUDPLAYERS = new Filter<>(new LinkedList<>());
    public static final List<UUID> DELETERS = new LinkedList<>();
    public static final String INTERNAL_RECEIVER = "InternalReceiver";

    public static PermissionPool PERMISSION_POOL = null;

    public static final PermissionGroup DEFAULT_PERMISSION_GROUP = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new SerializableDocument());

    public static final String PASTE_SERVER_URL_DOCUMENTS = "https://paste.labymod.net/documents";
    public static final String PASTE_SERVER_URL = "https://paste.labymod.net/";
    public static final String PASTE_SERVER_URL_RAW = "https://paste.labymod.net/raw/";

    public static boolean NEEDS_DEPENDENCIES;
    public static boolean JLINE_COMPLETER_INSTALLED;

    public static final Map<UUID, CloudPlayerInventory> INVENTORIES = new HashMap<>();


    public static CloudPlayerData getDefaultData(UUID uuid, String name, String ip) {
        final CloudPlayerData cloudPlayerData = new CloudPlayerData(uuid, name, Collections.singletonList(new PermissionEntry(uuid, PERMISSION_POOL.getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), ip, true, new Date().getTime(), 0L);
        cloudPlayerData.setDefault(true);
        return cloudPlayerData;
    }

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
