package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.service.util.CloudMap;
import io.vson.elements.object.VsonObject;
import io.vson.tree.VsonTree;

import java.io.Serializable;
import java.util.Map;

public abstract class Packet extends CloudMap<String, Object> implements Serializable {

    /**
     * Appends a value to the packet document
     * @param key
     * @param value
     * @return current packet
     */
    public Packet append(String key, Object value) {
        this.put(key, value);
        return this;
    }

    /**
     * Appeding whole value to packet
     * @param value
     * @return current packet
     */
    public Packet append(Object value) {
        this.putAll(VsonObject.encode((VsonObject) new VsonTree(value).tree()));
        return this;
    }

}
