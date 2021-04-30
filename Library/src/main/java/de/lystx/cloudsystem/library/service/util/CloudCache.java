package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.elements.list.CloudList;
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
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter @Setter
public class CloudCache {

    /**
     * The current ServiceType
     * Spigot or Proxy
     */
    private ServiceType currentServiceType;

    /**
     * The current CloudType
     * CloudAPI or Cloud etc
     */
    private CloudType currentCloudType;

    /**
     * The current Executor for sending
     * Packets and Queries
     */
    private CloudExecutor currentCloudExecutor;

    /**
     * The current Bukkit Version
     */
    private String bukkitVersion;

    /**
     * The PermissionPool
     */
    private PermissionPool permissionPool;

    /**
     * The filter for Services
     */
    private Filter<Service> serviceFilter;

    /**
     * The filter for CloudPlayers
     */
    private Filter<CloudPlayer> cloudPlayerFilter;

    /**
     * The ThreadPool
     */
    private final Executor threadPool;

    /**
     * The list of Players that delete NPCs
     */
    private final List<UUID> npcDeleterList;

    /**
     * THe CloudInventories of the CloudPlayers
     */
    private final Map<UUID, CloudPlayerInventory> cloudInventories;

    public static final String INTERNAL_RECEIVER = "InternalReceiver";
    public static final String PASTE_SERVER_URL_DOCUMENTS = "https://paste.labymod.net/documents";
    public static final String PASTE_SERVER_URL = "https://paste.labymod.net/";
    public static final String PASTE_SERVER_URL_RAW = "https://paste.labymod.net/raw/";

    /**
     * If alle the dependencies are
     * fully installed
     */
    private final boolean needsDependencies;

    /**
     * If the Jline Dependency for TabCOmpletion
     * is fully installed
     */
    private final boolean jlineCompleterInstalled;

    /**
     * The instance for this Cache
     */
    public static CloudCache instance;

    /**
     * Creates the Instance for this
     * {@link CloudCache} if its not set
     *
     * @return cloudcache instance
     */
    public static CloudCache getInstance() {
        if (instance == null) {
            instance = new CloudCache();
        }
        return instance;
    }


    /**
     * Checks if dependencies are already loaded
     */
    public CloudCache() {
        this.threadPool = Executors.newFixedThreadPool(1);

        this.cloudInventories = new HashMap<>();
        this.npcDeleterList = new LinkedList<>();

        this.serviceFilter = new Filter<>(new LinkedList<>());
        this.cloudPlayerFilter = new Filter<>(new CloudList<>());

        this.currentServiceType = ServiceType.SPIGOT;
        this.currentCloudType = CloudType.LIBRARY;

        boolean needsDependencies, jlineCompleterInstalled;

        try {
            Class.forName("jline.console.ConsoleReader");
            needsDependencies = false;
        } catch (ClassNotFoundException e) {
            needsDependencies =  true;
        }

        try {
            Class.forName("jline.console.completer.Completer");
            jlineCompleterInstalled = false;
        } catch (ClassNotFoundException e) {
            jlineCompleterInstalled =  true;
        }

        this.needsDependencies = needsDependencies;
        this.jlineCompleterInstalled = jlineCompleterInstalled;
    }
}
