package demo.org.powermock.examples.tutorial.hellopower;

import java.util.Properties;

/**
 * Important third party code that cannot be changed.
 */
public class SimpleConfig {

    private static Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(SimpleConfig.class.getClassLoader().getResourceAsStream("simpleConfig.properties"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String getGreeting() {
        return PROPERTIES.getProperty("greeting");
    }

    public static String getTarget() {
        return PROPERTIES.getProperty("target");
    }
}
