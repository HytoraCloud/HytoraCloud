package de.lystx.hytoracloud.driver.utils.interfaces;

import java.io.Serializable;

public interface Syncable<V> extends Serializable {


    /**
     * Updates the current object
     * from cache and returns same type object
     *
     * @return object from cache
     */
    V sync();

}
