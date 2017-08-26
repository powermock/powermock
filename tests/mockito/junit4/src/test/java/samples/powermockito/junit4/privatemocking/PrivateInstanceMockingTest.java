package samples.powermockito.junit4.privatemocking;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.privatemocking.PrivateMethodDemo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrivateMethodDemo.class})
public class PrivateInstanceMockingTest extends PrivateInstanceMockingCases {
    

}
