package samples.powermockito.junit4.bugs.github958;

public class OuterClass {
  public static InnerSingleton theInstance = new InnerSingleton();
  public static class InnerSingleton {
    public String name = "inner";
    private InnerSingleton() {}
  }
}
