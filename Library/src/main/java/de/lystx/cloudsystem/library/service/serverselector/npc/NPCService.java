package de.lystx.cloudsystem.library.service.serverselector.npc;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

import java.io.File;

@Getter
public class NPCService extends CloudService {

    private final File npcFile;
    private Document document;

    public NPCService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);

        this.npcFile = cloudLibrary.getService(FileService.class).getNpcFile();
        this.document = new Document();

        this.load();
    }

    public void load() {
        if (!this.npcFile.exists()) {
            this.document.save(this.npcFile);
        }
        this.document = Document.fromFile(this.npcFile);
    }

    public void append(String key, Document document) {
        this.document.append(key, document);
    }

    public void remove(String key) {
        this.document.remove(key);
    }

    public void save() {
        this.document.save();
    }
}
