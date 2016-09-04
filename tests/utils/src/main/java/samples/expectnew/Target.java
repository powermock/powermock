package samples.expectnew;

/**
 *
 */
public class Target implements ITarget{
    private final String targetName;
    private final int id;

    public Target(String targetName, int id) {

        this.targetName = targetName;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return targetName;
    }
}
