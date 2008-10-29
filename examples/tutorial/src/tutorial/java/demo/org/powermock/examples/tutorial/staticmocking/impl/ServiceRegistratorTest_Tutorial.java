package demo.org.powermock.examples.tutorial.staticmocking.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.powermock.Whitebox.getInternalState;
import static org.powermock.Whitebox.setInternalState;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import demo.org.powermock.examples.tutorial.staticmocking.osgi.BundleContext;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.ServiceRegistration;

public class ServiceRegistratorTest_Tutorial {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testRegisterService() throws Exception {
	}

	@Test
	public void testUnregisterService() throws Exception {
	}

	@Test
	public void testUnregisterService_idDoesntExist() throws Exception {
	}
}
