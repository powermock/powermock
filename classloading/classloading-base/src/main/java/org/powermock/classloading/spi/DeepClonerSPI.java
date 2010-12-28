package org.powermock.classloading.spi;

/**
 * A deep-cloner must implement this interface.
 */
public interface DeepClonerSPI {
    <T> T clone(T objectToClone);
}
