package samples.powermockito.junit4.bugs.github698;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Parent.class)
public class GitHub698Test {
    @Test
    public void shouldSuppressBothCallToParentClasses() {
        suppress(method(Parent.class));
        Child child = spy(new Child());

        assertThat(child.print()).isEqualTo("nullChildnull");
    }
}
