package powermock.examples.partialmocking;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Used to demonstrate PowerMock's ability to partial mock method calls.
 * Simple exmaple without private method mocking.
 *
 * Created by Katharina Laube on 08.09.2014.
 */
public class StatusSenderTest {

    private StatusSender tested;
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {

        /* Mock only the sendStatus method.
           (This mock creation should be moved to the test method,
           when method sendStatus is tested as well.)
         */
        tested = createPartialMock(StatusSender.class, "sendStatus");

        // Mock all methods of entityManager
        entityManager = createMock(EntityManager.class);
        tested.entityManager = entityManager;
    }

    @Test
    public void testHandleStatus() throws Exception {
        Status status = new Status();

        // expect call of void method from EntityManager
        entityManager.persist(status);

        /* expect call of another method in the tested class
           which will be tested separately. */
        expect(tested.sendStatus(status)).andReturn(true);

        replayAll();

        tested.handleStatus(status);

        verifyAll();
    }
}
