package samples.powermockito.junit4.jacoco;

import java.util.Random;

public class StaticMethods {

    private static final Random RANDOM = new Random();

    public static int calculateSomething(int in){
        int r = in;
        for (int i = 0; i < max(); i++) {
            r += in * i % getSomeFactor();
        }
        return r;
    }

    private static int getSomeFactor() {
        return RANDOM.nextInt(2);
    }

    private static int max() {
        return RANDOM.nextInt(100);
    }

}
