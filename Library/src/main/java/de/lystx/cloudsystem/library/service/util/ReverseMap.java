package de.lystx.cloudsystem.library.service.util;

import java.util.HashMap;

public class ReverseMap<K, V> extends HashMap<K, V> {

    public K getKey(Object value) {
        for (K k : this.keySet()) {
            if (this.get(k).equals(value)) {
                return k;
            }
        }
        return null;
    }

}
