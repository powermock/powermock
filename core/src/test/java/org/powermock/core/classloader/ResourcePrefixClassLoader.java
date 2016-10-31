package org.powermock.core.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

@SuppressWarnings("SameParameterValue")
public class ResourcePrefixClassLoader extends ClassLoader {

    private final String prefix;

    public ResourcePrefixClassLoader(ClassLoader parent, String prefix) {
        super(parent);
        this.prefix = prefix;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        // default super behaviour returns null, we want to delegate to our parent, with a prefix
        return getParent().getResources(this.prefix + name);
    }

    @Override
    protected URL findResource(String name) {
        return getParent().getResource(this.prefix + name);
    }
}
