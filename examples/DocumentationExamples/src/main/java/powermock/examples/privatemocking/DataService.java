package powermock.examples.privatemocking;

/**
 * A class used to demonstrate how it's possible for PowerMock to mock private
 * methods invocations.
 */
public class DataService {

	public boolean replaceData(final String dataId, final byte[] binaryData) {
		return modifyData(dataId, binaryData);
	}

	public boolean deleteData(final String dataId) {
		return modifyData(dataId, null);
	}

	/**
	 * Modify the data.
	 * 
	 * @param dataId
	 *            The ID of the data slot where the binary data will be stored.
	 * @param binaryData
	 *            The binary data that will be stored. If <code>null</code>
	 *            this means that the data for the particular slot is considered
	 *            removed.
	 * @return <code>true</code> if the operation was successful,
	 *         <code>false</code> otherwise.
	 */
	private boolean modifyData(final String dataId, final byte[] binaryData) {
		/*
		 * Imagine this method doing something complex and expensive.
		 */
		return true;
	}
}
