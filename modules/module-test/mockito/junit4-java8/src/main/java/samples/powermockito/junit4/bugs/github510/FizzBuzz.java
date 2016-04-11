package samples.powermockito.junit4.bugs.github510;

import java.io.PrintStream;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 *
 */
public class FizzBuzz implements IntConsumer {

    private final PrintStream outputStream;

    public FizzBuzz(PrintStream outputStream) {

        this.outputStream = outputStream;
    }

    public static boolean isFizz(int i) {
        return i % 5 == 0;
    }

    public static void run(PrintStream outputStream, int max) {
        IntStream.range(0, max).forEach(new FizzBuzz(outputStream));
    }

    @Override
    public void accept(int value) {
        outputStream.println("fizz");
    }
}