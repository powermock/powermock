package demo.org.powermock.examples.tutorial.partialmocking.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ServiceProducer extends ProviderArtifact {
	private final Set<DataProducer> dataProducingArtifacts;

	public ServiceProducer(int id, String name, DataProducer... dataProducingArtifacts) {
		super(id, name);
		this.dataProducingArtifacts = new HashSet<DataProducer>();
		for (DataProducer dataProducingArtifact : dataProducingArtifacts) {
			this.dataProducingArtifacts.add(dataProducingArtifact);
		}
	}

	public Set<DataProducer> getDataProducers() {
		return Collections.unmodifiableSet(dataProducingArtifacts);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataProducingArtifacts == null) ? 0 : dataProducingArtifacts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ServiceProducer other = (ServiceProducer) obj;
		if (dataProducingArtifacts == null) {
			if (other.dataProducingArtifacts != null)
				return false;
		} else if (!dataProducingArtifacts.equals(other.dataProducingArtifacts))
			return false;
		return true;
	}

}
