package powermock.examples.spring;

import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public final class FinalClass {

    public final String sayHello(){
        return "Hello, man!";
    }
}
