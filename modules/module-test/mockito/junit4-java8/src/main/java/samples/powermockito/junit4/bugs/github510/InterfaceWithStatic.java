package samples.powermockito.junit4.bugs.github510;

/**
 *
 */
public interface InterfaceWithStatic {

    static String sayHello(){
        return "What's up?";
    }

    static  String createAndSay(){
        return new ConstructorObject().sayHello();
    }

}
