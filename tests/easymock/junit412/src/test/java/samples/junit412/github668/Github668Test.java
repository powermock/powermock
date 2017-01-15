package samples.junit412.github668;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({TwoMockFieldsWithDifferentTypesClass.class, TwoMockFieldsWithSameTypeCase.class})
public class Github668Test {

}
