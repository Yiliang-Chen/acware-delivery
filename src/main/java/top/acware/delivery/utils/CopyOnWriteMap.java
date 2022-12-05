package top.acware.delivery.utils;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Map 的一个读优化，仅同步写入并在每次修改时执行完整复制，保证高并发下是安全的
 */
public class CopyOnWriteMap<K, V> implements ConcurrentMap<K, V> {

    private volatile Map<K, V> map;

    /* 设置不可变 Map */
    public CopyOnWriteMap() {
        this.map = Collections.emptyMap();
    }

    /* 将传入的 map 设置成不可变 */
    public CopyOnWriteMap(Map<K, V> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * 读写分离，没加锁的高并发下也能保证线程安全
     */
    @Override
    public V get(Object key) {
        return map.get(key);
    }

    /**
     * 通过复制 map 进行添加，保证线程安全
     */
    @Override
    public synchronized V put(K key, V value) {
        Map<K, V> copy = new HashMap<>(this.map);
        V val = copy.put(key, value);
        this.map = Collections.unmodifiableMap(copy);
        return val;
    }

    @Override
    public synchronized V remove(Object key) {
        Map<K, V> copy = new HashMap<>(this.map);
        V val = copy.remove(key);
        this.map = Collections.unmodifiableMap(copy);
        return val;
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        Map<K, V> copy = new HashMap<>(this.map);
        copy.putAll(m);
        this.map = Collections.unmodifiableMap(copy);
    }

    @Override
    public void clear() {
        this.map = Collections.emptyMap();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /* 不存在就 put */
    @Override
    public synchronized V putIfAbsent(K key, V value) {
        if (!containsKey(key))
            return put(key, value);
        else
            return get(key);
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        if (containsKey(key) && get(key).equals(value)) {
            remove(key);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        if (containsValue(key) && get(key).equals(oldValue)) {
            put(key, newValue);
            return true;
        }
        return false;
    }

    @Override
    public synchronized V replace(K key, V value) {
        if (containsValue(key))
            return put(key, value);
        return null;
    }
}
