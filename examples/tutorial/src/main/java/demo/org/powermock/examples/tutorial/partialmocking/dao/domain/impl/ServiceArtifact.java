package demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl;

import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.Connection;
import demo.org.powermock.examples.tutorial.partialmocking.domain.DataProducer;

public class ServiceArtifact {

	private final int id;

	private final String name;

	private final DataProducer[] dataProducers;

	public ServiceArtifact(int id, String name, DataProducer... dataProducers) {
		this.id = id;
		this.name = name;
		this.dataProducers = dataProducers;
	}

	public DataProducer[] getDataProducers() {
		return dataProducers;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Connection connectToService() {
		return new ConnectionImpl();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ServiceArtifact other = (ServiceArtifact) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
