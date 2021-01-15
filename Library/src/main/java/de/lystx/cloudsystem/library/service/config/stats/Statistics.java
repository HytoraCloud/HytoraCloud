package de.lystx.cloudsystem.library.service.config.stats;


import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Statistics implements Serializable {

    private final Map<String, Integer> stats;

    private File file;

    private int registeredPlayers;
    private int startedServices;
    private int connections;
    private int executedCommands;
    private int maintenanceSwitched;
    private int bootedUp;

    public Statistics() {
        this.stats = new HashMap<>();
        this.registeredPlayers = 0;
        this.startedServices = 0;
        this.connections = 0;
        this.executedCommands = 0;
        this.maintenanceSwitched = 0;
        this.bootedUp = 0;
    }

    public void load() {
        Document document = Document.fromFile(this.file);
        if (document.isEmpty()) {
            document.append("connections", 0);
            document.append("startedServices", 0);
            document.append("connections", 0);
            document.append("bootedUp", 0);
            document.append("executedCommands", 0);
            document.append("maintenanceSwitched", 0);
            document.append("reloadedCloud", 0);
            document.save(this.file);
        }
        for (String key : document.keys()) {
            this.stats.put(key, document.getInteger(key));
        }
    }

    public void add(String key) {
        this.stats.put(key, (this.stats.getOrDefault(key, 0) + 1));
    }

    public Document toDocument() {
        Document document = new Document();
        this.stats.forEach(document::append);
        return document;
    }


    public void save() {
        Document document = Document.fromFile(this.file);
        this.stats.forEach(document::append);
        document.save(this.file);
    }



}
