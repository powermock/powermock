package samples.powermockito.junit4.bugs.github510;

/**
 *
 */
public class ClassUsesInterface {

    public String saySomething(){
        return InterfaceWithStatic.sayHello();
    }

    public String createAndSay(){
        return InterfaceWithStatic.createAndSay();
    }

}
