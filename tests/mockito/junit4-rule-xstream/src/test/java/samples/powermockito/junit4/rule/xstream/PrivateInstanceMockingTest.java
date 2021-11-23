package samples.powermockito.junit4.rule.xstream;

import org.junit.Rule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.powermockito.junit4.privatemocking.PrivateInstanceMockingCases;
import samples.privatemocking.PrivateMethodDemo;

@PrepareForTest( { PrivateMethodDemo.class })
public class PrivateInstanceMockingTest extends PrivateInstanceMockingCases {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();
}
