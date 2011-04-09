package org.powermock.modules.junit4.rule;

import java.util.LinkedList;

/**
 * Not thread-safe
 */
class SimpleClassCache {
    private final LinkedList<String> cache = new LinkedList<String>();
    private final int cacheSize;

    SimpleClassCache(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public boolean addIfNotCached(String className) {
        if(cache.contains(className)) {
            return false;
        }

        if(cache.size() == cacheSize) {
          cache.removeFirst();
        }

        cache.add(className);
        return true;
    }

}
