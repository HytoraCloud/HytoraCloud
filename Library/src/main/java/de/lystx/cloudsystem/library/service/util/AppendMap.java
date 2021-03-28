package de.lystx.cloudsystem.library.service.util;

import java.util.HashMap;

public class AppendMap<K, V> extends HashMap<K, V> {

    public AppendMap<K, V> append(K k, V v) {
        this.put(k, v);
        return this;
    }
}
