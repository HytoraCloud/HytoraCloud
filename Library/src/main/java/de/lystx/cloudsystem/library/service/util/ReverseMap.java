package de.lystx.cloudsystem.library.service.util;

import java.util.HashMap;

/**
 * This is a normal {@link HashMap} but
 * you can for example get a key by an object
 * or cast an object automatically
 * @param <K>
 * @param <V>
 */
public class ReverseMap<K, V> extends HashMap<K, V> {

    public K getKey(Object value) {
        for (K k : this.keySet()) {
            if (this.get(k).equals(value)) {
                return k;
            }
        }
        return null;
    }

    public <T> T getObject(String key, Class<T> tClass) {
        return (T) this.get(key);
    }
}
