package powermock.examples.finalmocking;

/**
 * Simple class that uses a collaborator (the StateHolder class) which is final
 * and have final methods. The purpose is to show that PowerMock has the ability
 * to create a mock of the collaborator even though it's final and to expect the
 * method calls even though they're also final.
 */
public class StateFormatter {

	private final StateHolder stateHolder;

	public StateFormatter(StateHolder stateHolder) {
		this.stateHolder = stateHolder;
	}

	public String getFormattedState() {
		String safeState = "State information is missing";
		final String actualState = stateHolder.getState();
		if (actualState != null) {
			safeState = actualState;
		}
		return safeState;
	}
}
