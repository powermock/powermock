package org.powermock.examples.spring.mockito;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import powermock.examples.spring.IdGenerator;
import powermock.examples.spring.Message;
import powermock.examples.spring.MyBean;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

/**
 *
 */
@ContextConfiguration("classpath:/example-context.xml")
public abstract class SpringExampleTest {
    @Autowired
    private MyBean myBean;

    @Test
    public void mockStaticMethod() throws Exception {
        // Given
        final long expectedId = 2L;
        mockStatic(IdGenerator.class);
        expect(IdGenerator.generateNewId()).andReturn(expectedId);
        replay(IdGenerator.class);

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(expectedId, message.getId());
        assertEquals("My bean message", message.getContent());
    }

    @Test
    public void mockStaticMethodAndVerify() throws Exception {
        // Given
        final long expectedId = 2L;
        mockStatic(IdGenerator.class);
        expect(IdGenerator.generateNewId()).andReturn(expectedId);
        replay(IdGenerator.class);

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(expectedId, message.getId());
        assertEquals("My bean message", message.getContent());
    }

    @Test
    public void stubStaticMethod() throws Exception {
        // Given
        final long expectedId = 2L;
        stub(method(IdGenerator.class, "generateNewId")).toReturn(expectedId);

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(expectedId, message.getId());
        assertEquals("My bean message", message.getContent());
    }

    @Test
    public void suppressStaticMethod() throws Exception {
        // Given
        suppress(method(IdGenerator.class, "generateNewId"));

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(0L, message.getId());
        assertEquals("My bean message", message.getContent());
    }
}
