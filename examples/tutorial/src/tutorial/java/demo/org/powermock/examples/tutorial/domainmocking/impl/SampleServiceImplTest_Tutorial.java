package demo.org.powermock.examples.tutorial.domainmocking.impl;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectLastCall;
import static org.powermock.PowerMock.expectNew;
import static org.powermock.PowerMock.createMockAndExpectNew;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import demo.org.powermock.examples.tutorial.domainmocking.EventService;
import demo.org.powermock.examples.tutorial.domainmocking.PersonService;
import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;
import demo.org.powermock.examples.tutorial.domainmocking.domain.SampleServiceException;

public class SampleServiceImplTest_Tutorial {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCreatePerson() throws Exception {
	}

	@Test
	public void testCreatePerson_error() throws Exception {
	}

	@Test(expected = SampleServiceException.class)
	public void testCreatePerson_illegalName() throws Exception {
	}
}
