package org.powermock.api.mockito.internal.pluginswitch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import samples.classwithnonpublicparent.ClassWithNonPublicParent;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PowerMockPluginSwitchTest {

    private String mSomeOtherString = "some other string";

    @After
    @Before
    public void setup() {
        PowerMockPluginSwitch.enablePowerMockMaker();
    }

    @Test(expected=MissingMethodInvocationException.class)
    public void test_PowerMockMakerEnabled_Throws() {
        PowerMockPluginSwitch.enablePowerMockMaker();
        ClassWithNonPublicParent mockClassWithNonPublicParent = mock(ClassWithNonPublicParent.class);
        when(mockClassWithNonPublicParent.getSomeStringFromPackageProtectedClass()).thenReturn(mSomeOtherString);
        PowerMockPluginSwitch.disablePowerMockMaker();
    }

    @Test
    public void test_PowerMockMakerDisabled_MocksMethod() {
        PowerMockPluginSwitch.disablePowerMockMaker();
        ClassWithNonPublicParent mockClassWithNonPublicParent = mock(ClassWithNonPublicParent.class);
        when(mockClassWithNonPublicParent.getSomeStringFromPackageProtectedClass()).thenReturn(mSomeOtherString);
        Assert.assertEquals(mSomeOtherString, mockClassWithNonPublicParent.getSomeStringFromPackageProtectedClass());
    }

    @Test
    public void test_PowerMockMaker_SwitchesCorrectly() {
        test_PowerMockMakerDisabled_MocksMethod();
        try {
            test_PowerMockMakerEnabled_Throws();
        } catch (MissingMethodInvocationException e) {
            return;
        }
        assertTrue("Expected exception not thrown.", false);
    }
}