package de.lystx.hytoracloud.driver.service.util.other;

import io.thunder.packet.impl.response.IResponse;

import java.util.List;
import java.util.UUID;

public interface ObjectPool<T> {

    /**
     * Lists all cached objects
     *
     * @return list of objects
     */
    List<T> getCachedObjects();

    /**
     * Gets an object by name
     *
     * @param name the name
     * @return object
     */
    T getCached(String name);

    /**
     * Gets an object by uuid
     *
     * @param uniqueId the uuid
     * @return object
     */
    T getCached(UUID uniqueId);

    /**
     * Gets an Object by packets
     *
     * @param name the name of the object
     * @return response containing object
     */
    IResponse<T> getAsResponse(String name);

    /**
     * Gets an Object by packets
     *
     * @param uniqueId the uuid of the object
     * @return response containing object
     */
    IResponse<T> getAsResponse(UUID uniqueId);
}
