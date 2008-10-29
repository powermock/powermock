package samples.abstractmocking;

/**
 * Demonstrates that PowerMock can mock abstract methods. This was previously a
 * bug in PowerMock.
 */
public abstract class AbstractMethodMocking {

	public String getValue() {
		return getIt();
	}

	protected abstract String getIt();

}
