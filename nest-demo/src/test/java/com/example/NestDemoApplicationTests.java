package com.example;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.eugenekisy.nestexample.NestDemoApplication;
import org.eugenekisy.nestexample.connector.NestConnector;
import org.eugenekisy.nestexample.connector.NestConnector.AuthenticationListener;
import org.eugenekisy.nestexample.connector.NestConnector.EventListener;
import org.eugenekisy.nestexample.service.Service;
import org.eugenekisy.nestexample.util.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NestDemoApplication.class)
@WebAppConfiguration
public class NestDemoApplicationTests {

	private static final String THERMOSTAT_ID = "qhoJSOWwE19US04E0XejZGhbWOMW9p3T";
	private static final String PROTECT_ID = "jhg3hZt1-tPkhnEo6eyOHGhbWOMW9p3T";

	@Autowired
	private Service service;

	@Autowired
	private NestConnector nestConnector;

	@Before
	public void setUp() throws Exception {

		AuthenticationListener callback = mock(AuthenticationListener.class);
		nestConnector.authenticate(callback);

	}

	@Test
	public void testFanTimerActive() {
		EventListener callback = mock(EventListener.class);
		nestConnector.addDeviceListener(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.FAN_TIMER_ACTIVE, callback);
		nestConnector.setDeviceParam(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.FAN_TIMER_ACTIVE, true);		
		verify(callback, timeout(30000)).onDataChange("true");

	}

	@Test
	public void testFanTimerPassive() {
		EventListener callback = mock(EventListener.class);
		nestConnector.addDeviceListener(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.FAN_TIMER_ACTIVE, callback);
		nestConnector.setDeviceParam(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.FAN_TIMER_ACTIVE, false);		
		verify(callback, timeout(30000)).onDataChange("false");
	}

	@Test
	public void testLowTargetTempC() {
		service.setMinTemperature(11.0);
		assertEquals(new Double(11.0), service.getMinTemperature());

		service.setMinTemperature(9.0);
		assertEquals(new Double(9.0), service.getMinTemperature());
	}

	@Test
	public void testMaxTargetTempC() {
		service.setMaxTemperature(27.0);
		assertEquals(new Double(27.0), service.getMaxTemperature());

		service.setMaxTemperature(28.0);
		assertEquals(new Double(28.0), service.getMaxTemperature());
	}

	@Test
	public void testSecurityTrue() {
		EventListener callback = mock(EventListener.class);
		nestConnector.addDeviceListener(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.HVAC_MODE, callback);
		nestConnector.setDeviceParam(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.HVAC_MODE,
				Keys.THERMOSTAT.HVAC_MODES.HEAT_COOL);
		service.setSecurity(true);
		nestConnector.setDeviceParam(Keys.SMOKE_CO_ALARMS, PROTECT_ID, Keys.SMOKE_CO_ALARM.CO_ALARM_STATE,
				Keys.SMOKE_CO_ALARM.SMOKE_ALARM_STATES.EMERGENCY);

		verify(callback, timeout(10000)).onDataChange(Keys.THERMOSTAT.HVAC_MODES.OFF);

	}

	@Test
	public void testSecurityFalse() {
		EventListener callback2 = mock(EventListener.class);

		nestConnector.addDeviceListener(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.HVAC_MODE, callback2);
		nestConnector.setDeviceParam(Keys.SMOKE_CO_ALARMS, PROTECT_ID, Keys.SMOKE_CO_ALARM.CO_ALARM_STATE,
				Keys.SMOKE_CO_ALARM.SMOKE_ALARM_STATES.OK);

		service.setSecurity(false);
		nestConnector.setDeviceParam(Keys.THERMOSTATS, THERMOSTAT_ID, Keys.THERMOSTAT.HVAC_MODE,
				Keys.THERMOSTAT.HVAC_MODES.HEAT_COOL);
		nestConnector.setDeviceParam(Keys.SMOKE_CO_ALARMS, PROTECT_ID, Keys.SMOKE_CO_ALARM.CO_ALARM_STATE,
				Keys.SMOKE_CO_ALARM.SMOKE_ALARM_STATES.WARNING);

		verify(callback2, timeout(10000)).onDataChange(Keys.THERMOSTAT.HVAC_MODES.HEAT_COOL);
	}

	@Test
	public void testServiceTemperature() {
		service.setTemperature(25.0);
		assertEquals(new Double(25.0), service.getTemperature());
		service.setTemperature(10.0);
		assertEquals(new Double(10.0), service.getTemperature());
		service.setTemperature(27.0);
		assertEquals(new Double(27.0), service.getTemperature());
		service.setTemperature(21.00);
		assertEquals(new Double(21.0), service.getTemperature());
	}

}
