package samples.expectnew;

import samples.Service;

public class ExpectNewServiceUser {

	private final Service service;
	private final int times;

	public ExpectNewServiceUser(Service service, int times) {
		this.service = service;
		this.times = times;
	}

	public String useService() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < times; i++) {
			builder.append(service.getServiceMessage());
		}
		return builder.toString();
	}
}
