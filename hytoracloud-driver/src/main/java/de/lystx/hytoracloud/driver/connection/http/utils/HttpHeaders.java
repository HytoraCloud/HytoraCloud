package de.lystx.hytoracloud.driver.connection.http.utils;

import java.util.HashMap;
import java.util.List;

public class HttpHeaders extends HashMap<String, List<String>> {

    private static final long serialVersionUID = -7420107586335461179L;

    /**
     * Gets a String by its key
     *
     * @param key the key
     * @return string or null
     */
    public String getFirst(Object key) {
        List<String> list = get(key);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

}
