package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit5.PowerMockExtension;
import org.powermock.reflect.Whitebox;
import samples.simplemix.SimpleMix;
import samples.simplemix.SimpleMixCollaborator;
import samples.staticinitializer.StaticInitializerExample;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * T5: Whitebox internal state on classes that only work under PowerMock:
 * a mocked FINAL collaborator injected into a private field, and a static final
 * field of a class whose (throwing) static initializer PowerMock suppresses.
 */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest({SimpleMix.class, SimpleMixCollaborator.class})
@SuppressStaticInitializationFor("samples.staticinitializer.StaticInitializerExample")
class T5WhiteboxOnPreparedClassTest {

    @Test
    void finalCollaboratorMockIsInjectedIntoPrivateField() {
        SimpleMix tested = new SimpleMix();
        SimpleMixCollaborator collaborator = mock(SimpleMixCollaborator.class);
        when(collaborator.getRandomInteger()).thenReturn(6);

        Whitebox.setInternalState(tested, "collaborator", collaborator);

        SimpleMixCollaborator injected = Whitebox.getInternalState(tested, "collaborator");
        assertSame(collaborator, injected);
        assertEquals(6, injected.getRandomInteger());
    }

    @Test
    void staticFinalFieldOfClassWithSuppressedInitializerIsSettable() {
        // the real static initializer throws "This code must be suppressed!"
        assertNull(StaticInitializerExample.getMySet());

        Set<String> set = new HashSet<String>();
        Whitebox.setInternalState(StaticInitializerExample.class, "mySet", set);

        assertSame(set, Whitebox.getInternalState(StaticInitializerExample.class, "mySet"));
        assertSame(set, StaticInitializerExample.getMySet());
    }
}
