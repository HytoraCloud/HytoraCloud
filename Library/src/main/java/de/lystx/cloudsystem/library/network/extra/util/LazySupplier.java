package de.lystx.cloudsystem.library.network.extra.util;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * The lazy supplier is very neat if you want to run something sync which would only be running async
 */
public class LazySupplier<T> {

    @Getter
    private boolean empty = true;
    private T object = null;

    /**
     * Supplies the object
     *
     * @param t The object as T
     */
    public void accept(T t) {
        synchronized(this) {
            object = t;
            empty = false;
            this.notifyAll();
        }
    }

    /**
     * Gets the supplied object
     *
     * @return The element after object was supplied
     */
    public T get() {
        synchronized(this) {
            while(empty){
                try {
                    this.wait();
                }
                catch(InterruptedException e) {
                    //e.printStackTrace();
                }
            }
            return object;
        }
    }

    public T get(long timeout, TimeUnit unit) {
        synchronized(this) {
            while(empty){
                try {
                    this.wait(unit.toMillis(timeout));
                }
                catch(InterruptedException e) {
                    //e.printStackTrace();
                }
            }
            return object;
        }
    }

}
