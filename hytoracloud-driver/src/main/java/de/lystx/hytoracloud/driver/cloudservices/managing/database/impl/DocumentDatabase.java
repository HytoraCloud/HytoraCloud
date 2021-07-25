package de.lystx.hytoracloud.driver.cloudservices.managing.database.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.Getter;

import java.io.File;
import java.util.*;

@Getter
public class DocumentDatabase {

    /**
     * The unique id key for a document
     */
    public static final String NAME_KEY = "_db_id_key";

    /**
     * The name of this database
     */
    private final String name;

    /**
     * All cached documents
     */
    private final Map<String, JsonDocument> cache;

    /**
     * The directory of this database
     */
    private final File directory;

    public DocumentDatabase(String name) {
        this.name = name;
        this.cache = new HashMap<>();
        this.directory = new File(CloudDriver.getInstance().getInstance(FileService.class).getDatabaseDirectory(), name + "/");
        this.loadCache();
    }

    /**
     * Loads the cache of this database
     */
    public void loadCache() {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File[] files = this.directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                cache.put(file.getName().split(".json")[0], new JsonDocument(file));
            }
        }
    }

    /**
     * A list of all cached documents
     *
     * @return collection
     */
    public Collection<JsonDocument> getDocuments() {
        return this.cache.values();
    }

    /**
     * Gets a {@link JsonDocument} entry by key
     *
     * @param key the key
     * @return document
     */
    public JsonDocument getDocument(String key) {
        JsonDocument document = this.cache.get(key);

        if (document == null) {
            File file = new File(this.directory, key + ".json");
            if (file.exists()) {
                document = new JsonDocument(file);
                this.cache.put(file.getName().split(".json")[0], document);
            }
        }
        return document;
    }

    /**
     * Inserts {@link JsonDocument}s into database
     *
     * @param documents the documents
     */
    public void insert(JsonDocument... documents) {
        for (JsonDocument document : documents) {
            if (document.has(NAME_KEY)) {
                this.cache.put(document.getString(NAME_KEY), document);
                document.save(new File(this.directory, document.getString(NAME_KEY) + ".json"));
            }
        }
    }

    /**
     * Deletes an Entry
     *
     * @param name the name
     */
    public void delete(String name) {
        JsonDocument document = getDocument(name);
        if (document != null) {
            cache.remove(name);
        }
        document.delete();
    }

    /**
     * Deletes a {@link JsonDocument}
     *
     * @param document the document
     */
    public void delete(JsonDocument document) {
        if (document.has(NAME_KEY)) {
            delete(document.getString(NAME_KEY));
        }
    }

    /**
     * Saves the cached values
     */
    public void syncCache() {
        for (String s : this.cache.keySet()) {
            JsonDocument document = this.cache.get(s);
            document.save(new File(directory, s + ".json"));
        }
    }
}
