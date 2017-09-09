package org.powermock.api.mockito;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.powermock.api.mockito.internal.expectation.DefaultMethodExpectationSetupTestCase;
import org.powermock.api.mockito.internal.mockcreation.MockCreatorTestCase;
import org.powermock.api.mockito.mockmaker.PowerMockMakerTestCase;


/*
 * We have to use suite in this case to define order of test and prevent fluky test which
 * are depends on ordering.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                        PowerMockMakerTestCase.class,
                        MockCreatorTestCase.class,
                        DefaultMethodExpectationSetupTestCase.class
})
public class PowerMockMockito2ApiTestSuite {
}
