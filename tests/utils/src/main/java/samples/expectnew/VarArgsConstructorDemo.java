package samples.expectnew;

import samples.Service;

public class VarArgsConstructorDemo {

	private final String[] strings;
	private final Service[] services;
	private final byte[][] byteArrays;

	public VarArgsConstructorDemo(String... strings) {
		this.strings = strings;
		services = new Service[0];
		byteArrays = new byte[0][0];
	}

	public VarArgsConstructorDemo(byte[]... byteArrays) {
		this.byteArrays = byteArrays;
		services = new Service[0];
		strings = new String[0];
	}

	public VarArgsConstructorDemo(Service... services) {
		this.services = services;
		strings = new String[0];
		byteArrays = new byte[0][0];
	}

	public String[] getAllMessages() {
		return strings;
	}

	public Service[] getAllServices() {
		return services;
	}

	public byte[][] getByteArrays() {
		return byteArrays;
	}
}
