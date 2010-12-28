package org.powermock.classloading.spi;

public interface DeepClonerSPI {
    <T> T clone(T objectToClone);
}
