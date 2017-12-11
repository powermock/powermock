package samples.powermockito.junit4.bugs.github840;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    RegularTestClass.class,
    FinalEquals.class,
    FinalEqualsWithNoRefCheck.class,
    FinalEqualsWithOtherCall.class,
    FinalEqualsWithOtherCall2.class
})
public class GitHub840Test {

    @Test
    public void test_mock_with_final_equals_that_calls_other_methods_does_not_cause_stack_overflow() {
        FinalEqualsWithOtherCall instance = mock(FinalEqualsWithOtherCall.class);
        try {
            when(instance.method1()).thenReturn(2);
            assertThat(instance.method1()).isEqualTo(2);
        } catch (StackOverflowError soe) {
            failTest("Mock threw a stack overflow error", soe);
        } catch (Throwable t) {
            failTest("Mock threw a totally unexpected exception", t);
        }
    }

    @Test
    public void test_mocking_final_equals_does_not_cause_invalid_use_of_matchers() {
        FinalEqualsWithOtherCall instance = mock(FinalEqualsWithOtherCall.class);
        try {
            when(instance.equals(any())).thenReturn(true);
            Object other = new Object();
            try {
                assertThat(instance.equals(other)).isTrue();
            } catch (Throwable ignored) { }
        } catch (InvalidUseOfMatchersException ex) {
            failTest("Mocking final equals threw exception", ex);
        }
    }

    @Test
    public void test_mocking_final_equals_does_not_call_actual_method() {
        FinalEquals instance = mock(FinalEquals.class);
        when(instance.equals(any())).thenReturn(true);
        Object other = new Object();
        try {
            assertThat(instance.equals(other)).isTrue();
            assertThat(instance.standardEqualsCalled).isFalse();
        } catch (Throwable ignored) { }
    }

    @Test
    public void test_standard_equals_not_mocked_when_gateway_instructed_to_ignore() {
        MockGateway.MOCK_STANDARD_METHODS = false;
        FinalEqualsWithOtherCall instance = mock(FinalEqualsWithOtherCall.class);
        try {
            when(instance.equals(any())).thenReturn(true);
            failTest("Mocking should not work here");
        } catch (InvalidUseOfMatchersException ignored) { }
        Object other = new Object();
        try {
            assertThat(instance.equals(other)).isTrue();
            assertThat(instance.equalsCalled).isTrue();
            verifyPrivate(instance).invoke("otherMethod");
        } catch (Throwable ignored) { }
        MockGateway.MOCK_STANDARD_METHODS = true;
    }

    @Test
    public void test_mocking_not_confused_by_multiple_classes_with_final_equals() {
        FinalEqualsWithOtherCall resource = mock(FinalEqualsWithOtherCall.class);
        FinalEqualsWithOtherCall2 instance = mock(FinalEqualsWithOtherCall2.class);
        try {
            doCallRealMethod().when(instance).setResource(any(FinalEqualsWithOtherCall.class));
            instance.setResource(resource);
            when(instance.method2()).thenReturn(5f);
            assertThat(instance.method2()).isEqualTo(5f);
            verifyPrivate(instance, never()).invoke("someMethod");
        } catch (StackOverflowError soe) {
            failTest("Mock threw a stack overflow error", soe);
        } catch (Throwable ex) {
            failTest("Mock threw a totally unexpected exception", ex);
        }
    }

    @Test
    public void test_mocking_with_call_to_real_equals_method_works() {
        FinalEqualsWithOtherCall instance = mock(FinalEqualsWithOtherCall.class);
        when(instance.equals(any())).thenCallRealMethod();
        Object other = new Object();
        try {
            assertThat(instance.equals(other)).isFalse();
            assertThat(instance.equalsCalled).isTrue();
            verifyPrivate(instance).invoke("otherMethod");
        } catch (Throwable ignored) { }
    }

    @Test
    public void test_nonstandard_final_equals_method_is_not_treated_like_standard_equals() {
        MockGateway.MOCK_STANDARD_METHODS = false;
        FinalEquals instance = mock(FinalEquals.class);
        try {
            when(instance.equals(any())).thenReturn(true);
            failTest("Mocking should not work here, MOCK_STANDARD_METHODS = false");
        } catch (InvalidUseOfMatchersException ignored) { }
        try {
            when(instance.equals(any(FinalEquals.class))).thenReturn(true);
            Object other1 = new Object();
            // Regular equals method should make a normal comparison
            assertThat(instance.equals(other1)).isFalse();
            FinalEquals other2 = new FinalEquals();
            // Non-standard equals method should be mocked
            assertThat(instance.equals(other2)).isTrue();
        } catch (InvalidUseOfMatchersException ex) {
            ex.printStackTrace();
            failTest("Specialized equals method treated the same as standard equals", ex);
        }
        MockGateway.MOCK_STANDARD_METHODS = true;
    }

    @Test
    public void test_nonstandard_equals_method_is_not_treated_like_standard_equals() {
        MockGateway.MOCK_STANDARD_METHODS = false;
        FinalEquals2 instance = mock(FinalEquals2.class);
        try {
            when(instance.equals(any(Object.class))).thenReturn(true);
            // It is expected that mocking fails for the standard equals method
            failTest("Mocking should not work here, MOCK_STANDARD_METHODS = false");
        } catch (InvalidUseOfMatchersException ignored) {
        } finally {
            // Reset the flag since the method is actually called during the
            // attempt to mock it
            instance.standardEqualsCalled = false;
        }
        when(instance.equals(any(FinalEquals2.class))).thenReturn(true);
        Object other1 = new Object();
        // Regular equals method should make a normal comparison
        assertThat(instance.equals(other1)).isFalse();
        assertThat(instance.standardEqualsCalled).isTrue();
        FinalEquals2 other2 = new FinalEquals2();
        instance.nonstandardEqualsValue = false;
        // Non-standard equals method should be mocked
        assertThat(instance.equals(other2)).isTrue();
        assertThat(instance.nonstandardEqualsCalled).isFalse();
        MockGateway.MOCK_STANDARD_METHODS = true;
    }

    @Test
    public void test_absence_of_reference_check_in_final_equals_still_works() {
        // This test is to prove that if, for whatever reason, the
        // implementation of a final equals method returns false when
        // comparing to itself, then allowing the method to PROCEED is
        // a suboptimal decision. Such an implementation is arguably
        // deficient, but not unimaginable.
        FinalEqualsWithNoRefCheck instance = mock(FinalEqualsWithNoRefCheck.class);
        when(instance.aMethod()).thenReturn(23d);
        assertThat(instance.aMethod()).isEqualTo(23d);
    }

    @Test
    public void test_mocking_nonfinal_nonstandard_equals_works_as_expected() {
        RegularTestClass instance = mock(RegularTestClass.class);
        try {
            when(instance.equals(any(RegularTestClass.class))).thenCallRealMethod();
            RegularTestClass other = new RegularTestClass();
            assertThat(instance.equals(other)).isFalse();
            assertThat(instance.nonstandardEqualsCalled).isTrue();
        } catch (Throwable ex) {
            failTest("Unexpected exception", ex);
        }
    }

    private void failTest(String msg) {
        failTest(msg, null);
    }

    private void failTest(String msg, Throwable t) {
        System.out.println("\n**************************************************************");
        System.out.println(msg);
        System.out.println("**************************************************************\n");
        if (t != null) {
            t.printStackTrace();
        }
        fail(msg);
    }

}
