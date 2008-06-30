package demo.org.powermock.examples.tutorial.partialmocking.dao.domain;

public interface Connection {
	
	void disconnect();
	
	void send(byte[] data);

}
