package samples.powermockito.junit4.rule.objenesis;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.powermockito.junit4.privatemocking.PrivateInstanceMockingCases;
import samples.privateandfinal.PrivateFinal;
import samples.privatemocking.PrivateMethodDemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest( { PrivateMethodDemo.class })
public class PrivateInstanceMockingTest extends PrivateInstanceMockingCases {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();
    
    @Test(expected = ArrayStoreException.class)
    public void expectationsWorkWhenSpyingOnPrivateVoidMethods() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());

        tested.doObjectStuff(new Object());

        when(tested, "doObjectInternal", isA(Object.class)).thenThrow(new ArrayStoreException());

        tested.doObjectStuff(new Object());
    }
}
