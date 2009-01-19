package samples.junit4.stackoverflow;

public class EvilHashCode {
    //Required to produce error
    public String s = returnS();

    public String returnS()
    {
            return "s";
    }
	
	@Override
	public int hashCode() {
		return evilHashCode();
	}

	public int evilHashCode() {
		return 3;
	}
}
