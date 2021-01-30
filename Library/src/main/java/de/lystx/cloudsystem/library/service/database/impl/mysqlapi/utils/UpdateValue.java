package de.lystx.cloudsystem.library.service.database.impl.mysqlapi.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class UpdateValue {

    private final Map<String, String> data = new HashMap<>();

    public UpdateValue(String val1,  String val2) {
        data.put(val1, val2);
    }
    public void add (String val1, String val2) {
        data.put(val1, val2);
    }

    public Set<String> getKeys() {
        return data.keySet();
    }
    public String get(String key) {
        return data.get(key);
    }

}
