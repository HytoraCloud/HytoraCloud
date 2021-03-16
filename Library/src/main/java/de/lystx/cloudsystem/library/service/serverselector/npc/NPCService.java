package de.lystx.cloudsystem.library.service.serverselector.npc;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.io.FileService;
import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class NPCService extends CloudService {

    private VsonObject document;
    private VsonObject config;

    public NPCService(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType) {
        super(cloudLibrary, name, cloudType);
        this.load();
    }

    /**
     * Loads config and all NPCs
     */
    public void load() {
        try {
            this.document = new VsonObject(getCloudLibrary().getService(FileService.class).getNpcFile(), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            this.config = new VsonObject(getCloudLibrary().getService(FileService.class).getNpcLayout(), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);

            if (this.config.isEmpty() || !this.config.getFile().exists()) {
                this.config.putAll(new NPCConfig(
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
                        Collections.singletonList(new VsonObject()
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
    public NPCConfig getNPCConfig() {
        this.load();
        VsonArray vsonArray = this.config.getArray("items");
        List<VsonObject> vsonObjects = new LinkedList<>();
        for (VsonValue value : vsonArray.values()) {
            vsonObjects.add((VsonObject) value);
        }
        return new NPCConfig(this.config.getInteger("inventoryRows", 0), this.config.getString("inventoryTitle"), this.config.getBoolean("corners"), this.config.getString("connectingMessage"), this.config.getString("itemName"), this.config.getList("lore", String.class), this.config.getString("itemType"), vsonObjects);
    }

    /**
     * Saves NPC
     * @param key
     * @param vsonObject
     */
    public void append(String key, VsonObject vsonObject) {
        this.document.append(key, vsonObject);
    }

    /**
     * Deletes NPC
     * @param key
     */
    public void remove(String key) {
        this.document.remove(key);
    }

    /**
     * Saves Config and NPCs
     */
    public void save() {
        try {
            this.document.save();
            this.config.save();
        } catch (Exception e) {
            // Not saved because of Receiver!
        }
    }
}
