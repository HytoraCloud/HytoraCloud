package de.lystx.cloudsystem.library.service.serverselector.npc;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;

@Getter
public class NPCService extends CloudService {

    private Document document;
    private Document config;

    public NPCService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);

        this.document = new Document(cloudLibrary.getService(FileService.class).getNpcFile());
        this.config = new Document(cloudLibrary.getService(FileService.class).getNpcLayout());

        this.load();
    }

    public void load() {
        this.document = new Document(getCloudLibrary().getService(FileService.class).getNpcFile());
        this.config = new Document(getCloudLibrary().getService(FileService.class).getNpcLayout());
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
    }

    public NPCConfig getNPCConfig() {
        return this.config.getObject(this.config.getJsonObject(), NPCConfig.class);
    }

    public void append(String key, Document document) {
        this.document.append(key, document);
    }

    public void remove(String key) {
        this.document.remove(key);
    }

    public void save() {
        this.document.save();
        this.config.save();
    }
}
