package de.lystx.hytoracloud.driver.service.util.other;

/**
 * Wrapper class of some object (e.g. MooPlayer, ..)
 *
 * @param <W> The wrapper type (e.g. CloudPlayer)
 * @param <T> The wrapped type (e.g. OfflineCloudPlayer)
 */
public abstract class WrappedObject<W, T> {

    /**
     * The wrapped object accessible with {@link #unwrap()}
     */
    protected T wrappedObject;

    /**
     * The current lazy mode, if > 0 it will not update the data
     * automatically in the database after it went down to 0 again.
     */
    private int lazy = 0;

    public WrappedObject() {
        this(null);
    }

    public WrappedObject(T wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    /**
     * Updates the wrapped object
     */
    public abstract void update();

    /**
     * Unwraps this class, meaning only returning the wrapped data
     *
     * @return The wrapped data
     */
    public T unwrap() {
        return wrappedObject;
    }

    /**
     * Sets the lazy state to true, which means {@code level} times the wrapper won't update the
     * object in the database as well, it will only affect the object itself.
     *
     * @return This
     */
    public synchronized W lazyLock(int level) {
        if(level < 1) level = 1;
        lazy = level;
        return (W) this;
    }

    /**
     * Sets the lazy state to true, which means one time the wrapper won't update the
     * object in the database as well, it will only affect the object itself.
     *
     * @return This
     */
    public synchronized W lazyLock() {
        return lazyLock(1);
    }

    /**
     * Returns the lazy state and changes it as well if it's true
     *
     * @return The lazy state
     */
    protected synchronized boolean checkLaziness() {
        if(lazy > 0) {
            lazy--;
        }
        return lazy > 0;
    }

}
