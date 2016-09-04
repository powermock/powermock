package org.powermock.api.mockito.internal.expectation;

import org.mockito.Matchers;
import org.mockito.internal.matchers.LocalizedMatcher;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.ConstructorExpectationSetup;
import org.powermock.api.mockito.expectation.WithExpectedArguments;
import org.powermock.api.mockito.internal.invocation.MockitoNewInvocationControl;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.impl.ArrayMergerImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConstructorExpectationSetup<T> implements ConstructorExpectationSetup<T> {

    protected final Class<T> mockType;
    protected final ArrayMerger arrayMerger;
    private Class<?>[] parameterTypes = null;
    private final MockingProgress mockingProgress;

    public AbstractConstructorExpectationSetup(Class<T> mockType) {
        this.arrayMerger = new ArrayMergerImpl();
        this.mockType = mockType;
        this.mockingProgress = new ThreadSafeMockingProgress();
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
        NewInvocationControl<OngoingStubbing<T>> newInvocationControl = (NewInvocationControl<OngoingStubbing<T>>) MockRepository
                                                                                                                           .getNewInstanceControl(unmockedType);
        if (newInvocationControl == null) {
            InvocationSubstitute<T> mock = getMockCreator().createMock(InvocationSubstitute.class, false, false, null, null, (Method[]) null);
            newInvocationControl = new MockitoNewInvocationControl(mock);
            MockRepository.putNewInstanceControl(type, newInvocationControl);
            MockRepository.addObjectsToAutomaticallyReplayAndVerify(WhiteboxImpl.getUnmockedType(type));
        }

        return expectSubstitutionLogic(newInvocationControl, arguments);
    }

    private <S> OngoingStubbing<S> expectSubstitutionLogic(NewInvocationControl<OngoingStubbing<S>> newInvocationControl, Object[] arguments) throws Exception {
        final List<LocalizedMatcher> matchers = mockingProgress.getArgumentMatcherStorage().pullLocalizedMatchers();
        if (matchers.isEmpty()){
            return newInvocationControl.expectSubstitutionLogic(arguments);
        }else{
            List<LocalizedMatcherAdapter> matcherAdapters = getMatcherAdapters(matchers);
            return newInvocationControl.expectSubstitutionLogic(matcherAdapters.toArray());
        }
    }

    protected abstract List<LocalizedMatcherAdapter> getMatcherAdapters(List<LocalizedMatcher> matchers);

    abstract MockCreator getMockCreator();

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
            paramArgs[i] = Matchers.any(paramType);
        }
        final OngoingStubbing<T> ongoingStubbing = createNewSubstituteMock(mockType, parameterTypes, paramArgs);
        Constructor<?>[] otherCtors = new Constructor<?>[allConstructors.length - 1];
        System.arraycopy(allConstructors, 1, otherCtors, 0, allConstructors.length - 1);
        return new DelegatingToConstructorsOngoingStubbing<T>(otherCtors, ongoingStubbing);
    }

    @Override
    public OngoingStubbing<T> withNoArguments() throws Exception {
        return createNewSubstituteMock(mockType, parameterTypes, new Object[0]);
    }

    @Override
    public WithExpectedArguments<T> withParameterTypes(Class<?> parameterType, Class<?>... additionalParameterTypes) {
        this.parameterTypes = arrayMerger.mergeArrays(Class.class, new Class<?>[]{parameterType}, additionalParameterTypes);
        return this;
    }

}
