package net.hytora.networking.elements.other;


import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class TimedHashMap<K, V> extends ConcurrentHashMap<K, V> implements Closeable {

    /**
     * The serialized UID
     */
    private static final long serialVersionUID = 7931044222401042026L;

    /**
     * The scheduler
     */
    private final ScheduledExecutorService executor;

    /**
     * The time map where values time out
     */
    private final Map<K, Long> timeMap;

    /**
     * The time it expires
     */
    private final long expiryInMillis;


    public TimedHashMap() {

        this.timeMap = new ConcurrentHashMap<>();
        this.expiryInMillis = 1000;
        this.executor = Executors.newScheduledThreadPool(1);

        this.startTask();
    }


    /**
     * Inserts a value with a key into this map
     * for a given time
     *
     * @param key the key
     * @param value the value
     * @param time the time it should expire
     */
    public void put(K key, V value, long time) {
        this.timeMap.put(key, System.currentTimeMillis() + time);
        super.put(key, value);
    }

    /**
     * Start the service
     */
    private void startTask() {
        this.executor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();

            Iterator<Entry<K, Long>> iterator = timeMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<K, Long> entry = iterator.next();
                if (currentTime > (entry.getValue() + expiryInMillis)) {
                    ((BiConsumer<?, Boolean>) this.get(entry.getKey())).accept(null, false);
                    this.remove(entry.getKey());
                    iterator.remove();
                }
            }
        }, expiryInMillis / 2, expiryInMillis / 2, TimeUnit.MILLISECONDS);
    }

    /**
     * Allow to stop the service and empty all lists.
     */
    @Override
    public void close() {
        this.executor.shutdownNow();
        this.timeMap.clear();
        this.clear();
    }

}