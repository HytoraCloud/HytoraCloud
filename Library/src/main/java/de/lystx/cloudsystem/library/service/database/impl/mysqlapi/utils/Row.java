package de.lystx.cloudsystem.library.service.database.impl.mysqlapi.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Set;


@Getter
public class Row {

    private final HashMap<String, Object> content;

    public Row() {
        this.content = new HashMap<>();
    }
    public void addcolumn(String name, Object content) {
        this.content.put(name, content);
    }

    public Object get(String key) {
        return content.get(key);
    }

    public Set<String> getKeys() {
        return content.keySet();
    }
}
