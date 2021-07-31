package de.lystx.hytoracloud.driver.commons.storage;

import java.util.HashMap;
import java.util.List;

/**
 * This is a normal {@link HashMap} but
 * you can for example get a key by an object
 * or cast an object automatically
 *
 * @param <K> the key generic
 * @param <V> the value generic
 */
public class CloudMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 72837171396219550L;

    /**
     * Gets a key by value
     *
     * @param value the value
     * @return key or null if not found
     */
    public K getKey(Object value) {
        for (K k : this.keySet()) {
            if (this.get(k).equals(value)) {
                return k;
            }
        }
        return null;
    }

    /**
     * Puts a value into this map
     *
     * @param k the key
     * @param v the value
     * @return current map
     */
    public CloudMap<K, V> append(K k, V v) {
        this.put(k, v);
        return this;
    }

    /**
     * Gets a {@link Boolean} by key
     *
     * @param key the key
     * @return boolean
     */
    public Boolean getBoolean(String key) {
        return (Boolean) this.get(key);
    }

    /**
     * Gets a {@link String} by key
     *
     * @param key the key
     * @return string
     */
    public String getString(String key) {
        return (String) this.get(key);
    }

    /**
     * Gets a {@link List} by key
     *
     * @param key the key
     * @return list
     */
    public <Z> List<Z> getList(String key) {
        return (List<Z>) this.get(key);
    }

    /**
     * Gets a {@link Integer} by key
     *
     * @param key the key
     * @return integer
     */
    public Integer getInteger(String key) {
        return (Integer) this.get(key);
    }

    /**
     * Gets an object reference by key
     *
     * @param key the key
     * @param <T> the generic type
     * @return object
     */
    public <T> T getObject(String key) {
        return (T) this.get(key);
    }

    @Override
    public String toString() {
        return JsonDocument.toString(this);
    }
}
