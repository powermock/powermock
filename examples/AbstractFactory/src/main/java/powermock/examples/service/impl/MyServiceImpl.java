package powermock.examples.service.impl;

import java.util.HashSet;
import java.util.Set;


import powermock.examples.domain.Person;
import powermock.examples.service.MyService;

public class MyServiceImpl implements MyService {

	/**
	 * {@inheritDoc}
	 */
	public Set<Person> getAllPersons() {
		return new HashSet<Person>();
	}
}
