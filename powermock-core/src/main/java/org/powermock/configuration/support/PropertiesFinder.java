package org.powermock.configuration.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


class PropertiesFinder {
    
    private final ClassLoader classLoader;
    
    PropertiesFinder(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    List<ConfigurationSource> find(final String configurationFile) throws IOException, URISyntaxException {
        
        final List<ConfigurationSource> configurations = new ArrayList<ConfigurationSource>();
        
        Enumeration<URL> resources = classLoader.getResources(configurationFile);
        
        while (resources.hasMoreElements()) {
            URL candidate = resources.nextElement();
            configurations.add(new ConfigurationSource(candidate.getFile(), candidate.openStream()));
        }
        
        return configurations;
    }
    
    static class ConfigurationSource {
        
        private final String location;
        private final InputStream inputStream;
        
        
        ConfigurationSource(final String location, final InputStream inputStream) {
            this.location = location;
            this.inputStream = inputStream;
        }
        
        InputStream inputStream() {
            return inputStream;
        }
        
        String getLocation() {
            return location;
        }
        
        @Override
        public String toString() {
            return "ConfigurationSource{" + "location='" + location +  '}';
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            
            final ConfigurationSource that = (ConfigurationSource) o;
            
            return getLocation() != null ? getLocation().equals(that.getLocation()) : that.getLocation() == null;
        }
        
        @Override
        public int hashCode() {
            return getLocation() != null ? getLocation().hashCode() : 0;
        }
    }
}
