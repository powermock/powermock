package demo.org.powermock.examples.simple;


/**
 * Hello world!
 *
 */
public class Greeter {
    public static void main( String[] args ) {
        new Greeter().run(5, getMessage());
    }

    private void run(int count, String message) {
        Logger logger = new Logger();
        for(int indx=0; indx<count; indx++){
			logger.log(message);
        }
    }

    static String getMessage() {
		return SimpleConfig.getGreeting() + " " + SimpleConfig.getTarget();
	}
}
