package demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl;

import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.Connection;

public class ConnectionImpl implements Connection {

	public void disconnect() {
		System.out.println("Disconnecting...");
	}

	public void send(byte[] data) {
		System.out.println("Sending data of " + data.length + " bytes.");
	}
}
