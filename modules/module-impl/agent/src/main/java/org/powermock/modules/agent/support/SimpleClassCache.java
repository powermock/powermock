package org.powermock.modules.agent.support;

import java.util.LinkedList;

class SimpleClassCache {
    private final LinkedList<String> cache = new LinkedList<String>();
    private final int cacheSize;

    SimpleClassCache(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public synchronized boolean addIfNotCached(String className) {
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
