package samples.powermockito.junit4.jacoco;

import java.util.Random;

public final class InstanceMethods {

    private static final Random random = new Random();

    public int calculateSomething(int in){
        int r = in;
        for (int i = 0; i < max(); i++) {
            r += in * i % getSomeFactor();
        }
        return r;
    }

    private int getSomeFactor() {
        return random.nextInt(2);
    }

    private int max() {
        return random.nextInt(100);
    }

}
