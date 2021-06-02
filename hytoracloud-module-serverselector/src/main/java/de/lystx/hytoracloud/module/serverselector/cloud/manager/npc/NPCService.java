package de.lystx.hytoracloud.module.serverselector.cloud.manager.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.other.SerializableDocument;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.module.serverselector.cloud.ModuleSelector;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
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

    private JsonBuilder jsonBuilder;
    private JsonBuilder config;

    private final File npcDirectory;
    private final File npcFile;
    private final File npcLayout;


    @SneakyThrows
    public NPCService() {
        this.load();



        this.npcDirectory = new File(ModuleSelector.getInstance().getModuleDirectory(), "npcs/"); this.npcDirectory.mkdirs();
        this.npcFile = new File(this.npcDirectory, "npcs.json"); this.npcFile.createNewFile();
        this.npcLayout = new File(this.npcDirectory, "config.json");

    }

    /**
     * Loads config and all NPCs
     */
    public void load() {
        try {
            this.jsonBuilder = new JsonBuilder(npcFile);
            this.config = new JsonBuilder(npcLayout);

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
                        "§8» §b%service%",
                        Arrays.asList(
                                "§8",
                                "§8§m-----------",
                                "§8» §bOnline §8» §7%online%",
                                "§8» §bMaxPlayers §8» §7%max%",
                                "§8» §bMotd §8» §7%motd%",
                                "§8» §bState §8» §7%state%"
                        ),
                        "MINECART",
                        Collections.singletonList(new SerializableDocument()
                                .append("slot", 4)
                                .append("type", "NAME_TAG")
                                .append("name", "§8» §3%group%")
                                .append("lore", Arrays.asList(
                                        "§8§m-----------",
                                        "§8» §bOnline §8» §7%online_services%",
                                        "§8» §bType §8» §7%type%",
                                        "§8» §bTemplate §8» §7%template%"
                                )).append("glow", false))
                ));
                this.config.save();
            }
        } catch (Exception e) {
            //Receiver
        }
    }

    /**
     * Get NPCConfig
     * @return NPCConfig
     */
    @SneakyThrows
    public NPCConfig getNPCConfig() {
        this.load();
        JsonArray jsonArray = this.config.getArray("items");
        List<SerializableDocument> vsonObjects = new LinkedList<>();
        for (JsonElement jsonElement : jsonArray) {
            vsonObjects.add(SerializableDocument.fromDocument(new JsonBuilder(jsonElement.toString())));
        }

        return new NPCConfig(this.config.getInteger("inventoryRows", 0), this.config.getString("inventoryTitle"), this.config.getBoolean("corners"), this.config.getString("connectingMessage"), this.config.getString("itemName"), this.config.getList("lore", String.class), this.config.getString("itemType"), vsonObjects);
    }

    /**
     * Saves NPC
     * @param key
     * @param vsonObject
     */
    public void append(String key, JsonBuilder vsonObject) {
        this.jsonBuilder.append(key, vsonObject);
    }

    /**
     * Deletes NPC
     * @param key
     */
    public void remove(String key) {
        this.jsonBuilder.remove(key);
    }

    /**
     * Saves Config and NPCs
     */
    public void save() {
        try {
            this.jsonBuilder.save(this.npcFile);
            this.config.save(this.npcLayout);
        } catch (Exception e) {
            // Not saved because of Receiver!
        }
    }
}
