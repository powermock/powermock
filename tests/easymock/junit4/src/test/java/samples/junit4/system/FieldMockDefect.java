package samples.junit4.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * 
 * The reason why it fails is because we create a new instance of the field
 * using Whitebox but then hashCode doesn't work because it's missing
 * constructor args.
 * <p>
 * 
 * if (Field.class.isAssignableFrom(mock.getClass())) {
 * Whitebox.setInternalState(mock, "clazz", (Object) Object.class);
 * Whitebox.setInternalState(mock, "name", Object.class.getName()); }
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Field.class)
public class FieldMockDefect {
    @Test
    public void test() {
        final Field fieldMock = createMock(Field.class);
        expect(fieldMock.getName()).andReturn("foo");
        replayAll();
        assertEquals("foo", new ClassUnderTest().foo(fieldMock));
        verifyAll();

    }

    private static final class ClassUnderTest {
        public String foo(Field field) {
            return field.getName();
        }
    }
}