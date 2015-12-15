/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core;

import java.util.*;

public class ListMap<K, V> implements Map<K, V> {

    private List<Map.Entry<K, V>> entries = new LinkedList<Entry<K, V>>();

    private static class SimpleEntry<K, V> implements Entry<K, V> {

        private K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

    };

    @Override
    public V remove(Object key) {
        for (Iterator<Map.Entry<K, V>> i = entries.iterator(); i.hasNext();) {
            Map.Entry<K, V> entry = i.next();
            if (entry.getKey() == key) {
                i.remove();
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public V get(Object key) {
        for (Iterator<Map.Entry<K, V>> i = entries.iterator(); i.hasNext();) {
            Map.Entry<K, V> entry = i.next();
            if (entry.getKey() == key) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        for (Iterator<Map.Entry<K, V>> i = entries.iterator(); i.hasNext();) {
            Map.Entry<K, V> entry = i.next();
            if (entry.getKey() == key) {
                return entry.setValue(value);
            }
        }
        Map.Entry<K, V> entry = new SimpleEntry<K, V>(key, value);
        entries.add(entry);
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Iterator<Map.Entry<K, V>> i = entries.iterator(); i.hasNext();) {
            Map.Entry<K, V> entry = i.next();
            if (entry.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        Set<K> identityHashSet = new HashSet<K>();
        for (Map.Entry<K, V> entry : entries) {
            identityHashSet.add(entry.getKey());
        }
        return identityHashSet;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putAll(Map<? extends K, ? extends V> t) {
        Set<?> entrySet = t.entrySet();
        for (Object object : entrySet) {
            entries.add((java.util.Map.Entry<K, V>) object);
        }
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Collection<V> values() {
        Set<V> hashSet = new HashSet<V>();
        for (Map.Entry<K, V> entry : entries) {
            hashSet.add(entry.getValue());
        }
        return hashSet;
    }
}
