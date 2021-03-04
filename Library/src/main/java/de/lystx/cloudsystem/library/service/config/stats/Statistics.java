package de.lystx.cloudsystem.library.service.config.stats;

import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Statistics implements Serializable {

    private final Map<String, Integer> stats;

    private File file;

    public Statistics() {
        this.stats = new HashMap<>();
    }

    /**
     * Loads stats
     */
    public void load() {
        try {
            VsonObject vsonObject = new VsonObject(this.file, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            this.load(vsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads statistics of cloud
     * @param document
     */
    public void load(VsonObject document) {
        this.file = document.getFile();
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
            this.stats.put(key, document.getInteger(key, 0));
        }
    }

    /**
     * Increases stats
     * @param key
     */
    public void add(String key) {
        this.stats.put(key, (this.stats.getOrDefault(key, 0) + 1));
    }

    /**
     * TO vson
     * @return VsonObject
     */
    public VsonObject toVson() {
        VsonObject document = new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        this.stats.forEach((key, i) -> {
            document.append(key, (int)i);
        });
        return document;
    }

    /**
     * Saves to file
     * @param file
     */
    public void save(File file) {
        this.file = file;
        this.toVson().save(file);
    }
}
