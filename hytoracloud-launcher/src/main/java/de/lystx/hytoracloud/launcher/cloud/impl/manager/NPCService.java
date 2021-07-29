package de.lystx.hytoracloud.launcher.cloud.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudItem;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The {@link NPCService} manages the NPCs itsself
 * and the Config.
 *
 * It will check for new NPCs or NPCs to delete
 * It will manage the LayOut of your NPC-Inventory
 */
@Getter
@ICloudServiceInfo(
        name = "NPCService",
        type = CloudServiceType.CONFIG,
        description = {
                "This service is used to manage all NPCS",
                "Store and load them to and from files"
        },
        version = 1.1
)
public class NPCService implements ICloudService {

    private JsonDocument jsonDocument;
    private JsonDocument config;

    private final File npcDirectory;
    private final File npcFile;
    private final File npcLayout;


    @SneakyThrows
    public NPCService() {
        this.reload();

        this.npcDirectory = new File(CloudDriver.getInstance().getInstance(FileService.class).getDatabaseDirectory(), "npcs/"); this.npcDirectory.mkdirs();
        this.npcFile = new File(this.npcDirectory, "npcs.json"); this.npcFile.createNewFile();
        this.npcLayout = new File(this.npcDirectory, "config.json");

    }

    /**
     * Loads config and all NPCs
     */
    public void reload() {
        try {
            this.jsonDocument = new JsonDocument(npcFile);
            this.config = new JsonDocument(npcLayout);

            if (this.npcFile.exists()) {
                this.npcFile.createNewFile();
            }
            if (this.npcLayout.exists()) {
                this.npcLayout.createNewFile();
            }

            if (this.config.isEmpty() || !this.config.getFile().exists()) {
                this.config.append(new NPCConfig(
                        6,
                        "§8» §7Group §8┃ §b%group%",
                        true,
                        "",
                        new CloudItem("MINECART")
                        .lore(Arrays.asList(
                                "§8",
                                "§8§m-----------",
                                "§8» §bOnline §8» §7%online%",
                                "§8» §bMaxPlayers §8» §7%max%",
                                "§8» §bMotd §8» §7%motd%",
                                "§8» §bState §8» §7%state%"
                        )).display("§8» §b%service%"),
                        Collections.singletonList(new CloudItem("NAME_TAG")
                                .slot(4)
                                .display("§8» §3%group%")
                                .lore(Arrays.asList(
                                        "§8§m-----------",
                                        "§8» §bOnline §8» §7%online_services%",
                                        "§8» §bType §8» §7%type%",
                                        "§8» §bTemplate §8» §7%template%"
                                )))
                ));
                this.config.save();
            }
        } catch (Exception e) {
            //Receiver
        }
    }


    public List<NPCMeta> toMetas() {
        return this.jsonDocument.keySet(NPCMeta.class);
    }

    /**
     * Get NPCConfig
     * @return NPCConfig
     */
    @SneakyThrows
    public NPCConfig getNPCConfig() {
        this.reload();
        return this.config.getAs(NPCConfig.class);
    }

    /**
     * Registers an {@link NPCMeta}
     *
     * @param meta the meta
     */
    public void registerNPC(NPCMeta meta) {
        this.jsonDocument.append(meta.getUniqueId().toString(), meta);
    }

    /**
     * Unregisters {@link NPCMeta}
     *
     * @param key the uuid
     */
    public void unregisterNPC(String key) {
        this.jsonDocument.remove(key);
    }

    /**
     * Saves Config and NPCs
     */
    public void save() {
        try {
            this.jsonDocument.save(this.npcFile);
            this.config.save(this.npcLayout);
        } catch (Exception e) {
            // Not saved because of Receiver!
        }
    }
}
