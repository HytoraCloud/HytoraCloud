package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import net.hytora.networking.elements.packet.response.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface IPool<V extends Identifiable> extends Iterable<V> {

    /**
     * Loads a list of all cached objects
     *
     * @return list
     */
    List<V> getCachedObjects();

    /**
     * Sets the current cached objects
     *
     * @param cachedObjects the objects
     */
    void setCachedObjects(List<V> cachedObjects);

    /**
     * Searches for an object by its name
     *
     * @param name the name
     * @return object or null
     */
    V getCachedObject(String name);

    /**
     * Searches for an object by its uuid
     *
     * @param uniqueId the uuid
     * @return object or null
     */
    V getCachedObject(UUID uniqueId);

    /**
     * Gets an object from the cloud but does not
     * block the main thread and accepts the consumer after
     *
     * @param name the name of the object
     * @param consumer the consumer
     */
    void getObjectAsync(String name, Consumer<V> consumer);

    /**
     * Gets an object from the cloud but does not
     * block the main thread and accepts the consumer after
     *
     * @param uniqueId the uuid of the object
     * @param consumer the consumer
     */
    void getObjectAsync(UUID uniqueId, Consumer<V> consumer);

    /**
     * Gets an object synced from the cloud
     * via packet and response
     * (This might take some time to process)
     *
     * @param name the name
     * @return response or null if timed out
     */
    Response<V> getObjectSync(String name);

    /**
     * Loads an {@link Optional} for the object
     *
     * @param name the name of object
     * @return optional
     */
    default Optional<V> getOptional(String name) {
        return this.getCachedObjects().stream().filter(v -> v.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Loads an {@link Optional} for the object
     *
     * @param uniqueId the uuid of object
     * @return optional
     */
    default Optional<V> getOptional(UUID uniqueId) {
        return this.getCachedObjects().stream().filter(v -> v.getUniqueId() == uniqueId).findFirst();
    }
    /**
     * Gets an object synced from the cloud
     * via packet and response
     * (This might take some time to process)
     *
     * @param uniqueId the uuid
     * @return response or null if timed out
     */
    Response<V> getObjectSync(UUID uniqueId);
}
