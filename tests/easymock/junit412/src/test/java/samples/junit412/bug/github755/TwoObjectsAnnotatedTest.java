package samples.junit412.bug.github755;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.easymock.annotation.MockNice;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.newmocking.SomeDependency;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class TwoObjectsAnnotatedTest {
    
    @Mock
    private String obj1;
    
    @Mock
    private String obj2;
    
    @MockNice
    private SomeDependency someClass1;
    
    @MockNice
    private SomeDependency someClass2;
    
    @Test
    public void should_create_mock_for_all_fields_annotated_Mock() {
        assertThat(obj1).isNotNull();
        assertThat(obj2).isNotNull();
    }
    
    @Test
    public void should_create_mock_for_all_fields_annotated_MockNice() {
        assertThat(someClass1).isNotNull();
        assertThat(someClass2).isNotNull();
    }
    
}
