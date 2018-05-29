package org.powermock.core.test;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.powermock.core.classloader.MockClassLoaderFactoryTest;

import java.util.Collection;

public class ContainsCondition extends Condition<Object> {
    
    public static ContainsCondition contains(final String expectedClassToModify) {
        return new ContainsCondition(expectedClassToModify);
    }
    
    private final String expectedClassToModify;
    
    private ContainsCondition(final String expectedClassToModify) {
        super(String.format("contains value `%s`", expectedClassToModify));
        this.expectedClassToModify = expectedClassToModify;
    }
    
    @Override
    public boolean matches(final Object value) {
        Collection<String> strings = (Collection<String>) value;
        return strings.contains(expectedClassToModify);
    }
    
}
