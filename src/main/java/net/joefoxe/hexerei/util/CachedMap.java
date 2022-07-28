package net.joefoxe.hexerei.util;


import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CachedMap<K, V> {

    private final Map<K, ValueWrapper> cache;
    private final long lifespan;
    private long lastCheck;

    protected CachedMap(Map<K, ValueWrapper> map, long lifespan) {
        this.lifespan = lifespan;
        this.cache = map;
    }

    /**
     * @param lifespan   the minimum lifespan of an entry
     * @param comparator the comparator to compare the keys
     */
    public CachedMap(long lifespan, Comparator<K> comparator) {
        this(new TreeMap<>(comparator), lifespan);
    }

    /**
     * @param lifespan the minimum lifespan of an entry
     */
    public CachedMap(long lifespan) {
        this(new HashMap<>(), lifespan);
    }

    public CachedMap() {
        this(-1);
    }

    public V get(K key, Supplier<V> valueSupplier) {
        V value;
        if (cache.containsKey(key)) {
            value = cache.get(key).getValue();
        } else {
            value = valueSupplier.get();
            cache.put(key, new ValueWrapper(value));
        }
        cleanup();
        return value;
    }

    private void cleanup() {
        if (lifespan < 0) {
            return;
        }
        long time = System.currentTimeMillis();
        if (time - lastCheck > lifespan) {
            Collection<K> collect = cache.entrySet().stream().filter(kValueWrapperEntry -> kValueWrapperEntry.getValue().checkInvalid(time)).map(Map.Entry::getKey).collect(Collectors.toSet());
            cache.keySet().removeAll(collect);
            lastCheck = time;
        }
    }

    public boolean has(K key) {
        return cache.containsKey(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private class ValueWrapper {

        private V value;
        private long accessTimestamp;

        public ValueWrapper(V value) {
            this.value = value;
            this.accessTimestamp = System.currentTimeMillis();
        }

        public boolean checkInvalid(long currentTime) {
            if (lifespan >= 0) {
                return currentTime - accessTimestamp > lifespan;
            }
            return false;
        }

        public V getValue() {
            accessTimestamp = System.currentTimeMillis();
            return value;
        }

    }

}