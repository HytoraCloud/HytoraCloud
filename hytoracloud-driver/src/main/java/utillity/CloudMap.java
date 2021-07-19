package utillity;

import java.util.HashMap;
import java.util.List;

/**
 * This is a normal {@link HashMap} but
 * you can for example get a key by an object
 * or cast an object automatically
 * @param <K>
 * @param <V>
 */
public class CloudMap<K, V> extends HashMap<K, V> {

    public K getKey(Object value) {
        for (K k : this.keySet()) {
            if (this.get(k).equals(value)) {
                return k;
            }
        }
        return null;
    }

    public <Z> List<Z> getList(String key, Class<Z> vClass) {
        return (List<Z>) this.get(key);
    }

    public CloudMap<K, V> append(K k, V v) {
        this.put(k, v);
        return this;
    }

    public Boolean getBoolean(String key) {
        return (Boolean) this.get(key);
    }

    public String getString(String key) {
        return (String) this.get(key);
    }

    public Integer getInteger(String key) {
        return (Integer) this.get(key);
    }


    public <T> T getObject(String key, Class<T> tClass) {
        return (T) this.get(key);
    }
}
