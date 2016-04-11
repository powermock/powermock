package samples.powermockito.junit4.bugs.github510;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FizzBuzz.class)
public class FizzBuzzTest extends TestCase {

    public static final int ANY_NUMBER = 5;

    public void testFizzOnThreeShouldReturnFalse() {
        assertThat(FizzBuzz.isFizz(3)).isFalse();
    }

    public void testFizzOnFiveShouldReturnTrue() {
        assertThat(FizzBuzz.isFizz(5)).isTrue();
    }

    @Test
    public void acceptOutputsFizzForFizz() {
        PrintStream stream = mock(PrintStream.class);

        PowerMockito.spy(FizzBuzz.class);
        Mockito.when(FizzBuzz.isFizz(ANY_NUMBER)).thenReturn(true);

        FizzBuzz.run(stream, 10);

        Mockito.verify(stream, times(10)).println(eq("fizz"));
    }
}