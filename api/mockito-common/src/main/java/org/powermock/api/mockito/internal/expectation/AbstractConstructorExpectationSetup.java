package org.powermock.api.mockito.internal.expectation;

import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.ConstructorExpectationSetup;
import org.powermock.api.mockito.expectation.WithExpectedArguments;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.impl.ArrayMergerImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class AbstractConstructorExpectationSetup<T> implements ConstructorExpectationSetup<T> {

    protected final Class<T> mockType;
    protected final ArrayMerger arrayMerger;
    private Class<?>[] parameterTypes = null;

    public AbstractConstructorExpectationSetup(Class<T> mockType) {
        this.arrayMerger = new ArrayMergerImpl();
        this.mockType = mockType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> OngoingStubbing<T> createNewSubstituteMock(Class<T> type, Class<?>[] parameterTypes,
                                                           Object... arguments) throws Exception {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        final Class<T> unmockedType = (Class<T>) WhiteboxImpl.getUnmockedType(type);
        if (parameterTypes == null) {
            WhiteboxImpl.findUniqueConstructorOrThrowException(type, arguments);
        } else {
            WhiteboxImpl.getConstructor(unmockedType, parameterTypes);
        }

        /*
        * Check if this type has been mocked before
        */
        NewInvocationControl<OngoingStubbing<T>> newInvocationControl =
                (NewInvocationControl<OngoingStubbing<T>>) MockRepository.getNewInstanceControl(unmockedType);
        if (newInvocationControl == null) {
            InvocationSubstitute<T> mock = getMockCreator().createMock(InvocationSubstitute.class, false, false, null, null, (Method[]) null);
            newInvocationControl = createNewInvocationControl(mock);
            MockRepository.putNewInstanceControl(type, newInvocationControl);
            MockRepository.addObjectsToAutomaticallyReplayAndVerify(WhiteboxImpl.getUnmockedType(type));
        }

        return newInvocationControl.expectSubstitutionLogic(arguments);
    }

    abstract MockCreator getMockCreator();
    abstract <T> NewInvocationControl<OngoingStubbing<T>> createNewInvocationControl(InvocationSubstitute<T> mock);

    void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    OngoingStubbing<T> withArguments(Object[] additionalArguments) throws Exception {
        return createNewSubstituteMock(mockType, parameterTypes, additionalArguments);
    }

    @Override
    public OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
        return createNewSubstituteMock(mockType, parameterTypes, arrayMerger.mergeArrays(Object.class, new Object[]{firstArgument},
                                                                                         additionalArguments));
    }

    @Override
    public OngoingStubbing<T> withAnyArguments() throws Exception {
        if (mockType == null) {
            throw new IllegalArgumentException("Class to expected cannot be null");
        }
        final Class<T> unmockedType = (Class<T>) WhiteboxImpl.getUnmockedType(mockType);
        final Constructor<?>[] allConstructors = WhiteboxImpl.getAllConstructors(unmockedType);
        final Constructor<?> constructor = allConstructors[0];
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] paramArgs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            paramArgs[i] = createParamArgMatcher(paramType);
        }
        final OngoingStubbing<T> ongoingStubbing = createNewSubstituteMock(mockType, parameterTypes, paramArgs);
        Constructor<?>[] otherCtors = new Constructor<?>[allConstructors.length - 1];
        System.arraycopy(allConstructors, 1, otherCtors, 0, allConstructors.length - 1);
        return new DelegatingToConstructorsOngoingStubbing<T>(otherCtors, ongoingStubbing);
    }

    abstract Object createParamArgMatcher(Class<?> paramType);

    @Override
    public OngoingStubbing<T> withNoArguments() throws Exception {
        return createNewSubstituteMock(mockType, parameterTypes);
    }

    @Override
    public WithExpectedArguments<T> withParameterTypes(Class<?> parameterType, Class<?>... additionalParameterTypes) {
        this.parameterTypes = arrayMerger.mergeArrays(Class.class, new Class<?>[]{parameterType}, additionalParameterTypes);
        return this;
    }

}
