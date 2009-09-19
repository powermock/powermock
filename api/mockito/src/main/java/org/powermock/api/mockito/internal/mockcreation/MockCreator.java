package org.powermock.api.mockito.internal.mockcreation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.mockito.Mockito;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.creation.jmock.ClassImposterizer;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.util.MockName;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoMethodInvocationControl;
import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.Whitebox;

public class MockCreator {

    @SuppressWarnings("unchecked")
    public static <T> T mock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator, Method... methods) {
        if (type == null) {
            throw new IllegalArgumentException("The class to mock cannot be null");
        }

        T mock = null;
        final String mockName = toInstanceName(type);

        final Class<T> typeToMock;
        if (type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers())) {
            typeToMock = (Class<T>) new ClassReplicaCreator().createClassReplica(type);
        } else {
            typeToMock = type;
        }

        MockData<T> mockData = createMethodInvocationControl(mockName, typeToMock, methods, isSpy, (T) delegator);

        mock = mockData.getMock();
        if (isStatic) {
            MockRepository.putStaticMethodInvocationControl(type, mockData.getMethodInvocationControl());
        } else {
            MockRepository.putInstanceMethodInvocationControl(mock, mockData.getMethodInvocationControl());
        }

        if (isSpy && !isStatic) {
            // TODO Must add something as additional state so that MockGateway
            // can figure out if this _instance_ should never be proxied!! I.e.
            // we must remove the MockGateway#DONT_MOCK_NEXT_CALL and change it
            // to something that takes the instance (or class?) into account.
            // (it shouldn't be in case of spies, at least in case of instance
            // spies!)
        }

        if (mock instanceof InvocationSubstitute == false) {
            MockRepository.addObjectsToAutomaticallyReplayAndVerify(mock);
        }
        return mock;
    }

    private static <T> MockData<T> createMethodInvocationControl(final String mockName, Class<T> type, Method[] methods, boolean isSpy,
            Object delegator) {
        final MockSettingsImpl mockSettings;
        if (isSpy) {
            mockSettings = (MockSettingsImpl) new MockSettingsImpl().defaultAnswer(Mockito.CALLS_REAL_METHODS);
        } else {
            mockSettings = (MockSettingsImpl) Mockito.withSettings();
        }
        MockHandler<T> mockHandler = new MockHandler<T>(new MockName(mockName, type),
                Whitebox.getInternalState(Mockito.class, MockingProgress.class), new MatchersBinder(), mockSettings);
        MethodInterceptorFilter filter = new MethodInterceptorFilter(type, mockHandler);
        final T mock = (T) ClassImposterizer.INSTANCE.imposterise(filter, type);
        final MockitoMethodInvocationControl invocationControl = new MockitoMethodInvocationControl(filter, isSpy && delegator == null ? new Object()
                : null, methods);
        return new MockData<T>(invocationControl, mock);
    }

    private static String toInstanceName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        // lower case first letter
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    /**
     * Class that encapsulate a mock and its corresponding invocation control.
     */
    private static class MockData<T> {
        private final MockitoMethodInvocationControl methodInvocationControl;

        private final T mock;

        MockData(MockitoMethodInvocationControl methodInvocationControl, T mock) {
            this.methodInvocationControl = methodInvocationControl;
            this.mock = mock;
        }

        public MockitoMethodInvocationControl getMethodInvocationControl() {
            return methodInvocationControl;
        }

        public T getMock() {
            return mock;
        }
    }
}
