package demo.org.powermock.examples.tutorial.partialmocking.service.impl;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.powermock.Whitebox.invokeMethod;
import static org.powermock.Whitebox.setInternalState;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import demo.org.powermock.examples.tutorial.partialmocking.dao.ProviderDao;
import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl.ServiceArtifact;
import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;

public class ProviderServiceImplTest_Tutorial {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetAllServiceProviders() throws Exception {
	}

	@Test
	public void testGetAllServiceProviders_noServiceProvidersFound() throws Exception {
	}

	@Test
	public void testServiceProvider_found() throws Exception {
	}

	@Test
	public void testServiceProvider_notFound() throws Exception {
	}

	@Test
	public void getAllServiceProducers() throws Exception {
	}

	@Test
	public void getAllServiceProducers_empty() throws Exception {
	}
}
