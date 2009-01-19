/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ListMap<K, V> implements Map<K, V> {
	
	private List<Map.Entry<K, V>> entries = new LinkedList<Entry<K, V>>();
	
	private static class SimpleEntry<K, V> implements Entry<K, V> {
		
		private K key;
		private V value;

		public SimpleEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}
		
	};
	
	public V remove(Object key) {
		for (Iterator<Map.Entry<K, V>> i=entries.iterator(); i.hasNext();) {
			Map.Entry<K, V> entry = i.next();
			if (entry.getKey() == key) {
				i.remove();
				return entry.getValue();
			}
		}
		return null;
	}

	public void clear() {
		entries.clear();
	}

	public V get(Object key) {
		for (Iterator<Map.Entry<K, V>> i=entries.iterator(); i.hasNext();) {
			Map.Entry<K, V> entry = i.next();
			if (entry.getKey() == key) {
				return entry.getValue();
			}
		}
		return null;
	}

	public V put(K key, V value) {
		for (Iterator<Map.Entry<K, V>> i=entries.iterator(); i.hasNext();) {
			Map.Entry<K, V> entry = i.next();
			if (entry.getKey() == key) {
				return entry.setValue(value);
			}
		}
		Map.Entry<K, V> entry = new SimpleEntry<K, V>(key, value);
		entries.add(entry);
		return null;
	}
	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException();
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		throw new UnsupportedOperationException();
	}


	public int size() {
		throw new UnsupportedOperationException();
	}

	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}
}
