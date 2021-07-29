package de.lystx.hytoracloud.driver.cloudservices.managing.database.impl;

import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;

import java.util.LinkedList;
import java.util.List;

public class ObjectDatabase<V> extends DocumentDatabase {

    private final Class<V> vClass;

    public ObjectDatabase(String name, Class<V> vClass) {
        super(name);
        this.vClass = vClass;
    }

    /**
     * Inserts an object into database
     *
     * @param key the key where its stored
     * @param object the database
     */
    public void insert(String key, V object) {
        JsonDocument document = new JsonDocument();
        document.append(NAME_KEY, key);
        document.append("object", object);

        this.insert(document);
    }

    /**
     * Gets a list of all entries
     *
     * @return list of entries
     */
    public List<V> getEntries() {
        List<V> list = new LinkedList<>();

        for (JsonDocument document : this.getDocuments()) {
            list.add(document.get("object", vClass));
        }
        return list;
    }
}
