package org.powermock.core;

import org.junit.Test;

import java.util.AbstractList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

// Must pass on JDK 16+ with java.lang closed: generated classes are defined without reflective ClassLoader.defineClass.
// Design-neutral: checks only what any replica implementation has to deliver, not its naming scheme or class loader.
public class ClassReplicaCreatorTest {

    @Test
    public void should_create_replica_of_final_system_class() throws Exception {
        Class<String> replica = new ClassReplicaCreator().createClassReplica(String.class);

        assertNotSame(String.class, replica);
        assertNotEquals(String.class.getName(), replica.getName());
        // loadable by name from the loader that defined it
        assertSame(replica, Class.forName(replica.getName(), false, replica.getClassLoader()));
        // usable: it can be instantiated, unlike the final original it stands in for
        Object instance = replica.getDeclaredConstructor().newInstance();
        assertFalse(instance instanceof String);
    }

    @Test
    public void should_create_concrete_subclass_of_abstract_class() {
        Class<?> concrete = new ConcreteClassGenerator().createConcreteSubClass(AbstractList.class);
        assertEquals(AbstractList.class, concrete.getSuperclass());
    }
}
