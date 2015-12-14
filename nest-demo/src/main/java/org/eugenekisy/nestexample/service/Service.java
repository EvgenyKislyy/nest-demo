package org.eugenekisy.nestexample.service;

import javax.annotation.PostConstruct;

import org.eugenekisy.nestexample.connector.NestConnector;
import org.eugenekisy.nestexample.util.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(basePackages = { "com.example.*" })
@PropertySource("classpath:application.properties")
public class Service implements NestConnector.AuthenticationListener {

	private static Logger logger = LoggerFactory.getLogger(Service.class);

	private static final String THERMOSTAT_ID = "thermostat.id";
	private static final String PROTECT_ID = "protect.id";

	private Double minTemperature = 0.0;
	private Double temperature = 0.0;
	private Double maxTemperature = 0.0;

	private boolean security;

	private String thermostatId;
	private String protectId;

	@Autowired
	private Environment env;

	@Autowired
	private NestConnector nestConnector;

	@PostConstruct
	public void init() {
		thermostatId = env.getProperty(THERMOSTAT_ID);
		protectId = env.getProperty(PROTECT_ID);

		nestConnector.authenticate(this);
		
		nestConnector.addDeviceListener(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_C,
				new ThermostatThemperatureListener());
		nestConnector.addDeviceListener(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_LOW_C,
				new ThermostatLowThemperatureListener());
		nestConnector.addDeviceListener(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_HIGH_C,
				new ThermostatHighThemperatureListener());
	}

	@Override
	public void onAuthenticationSuccess(String message) {
		logger.info("Authenticated: {}", message);
		// doAction();
	}

	@Override
	public void onAuthenticationFailure(int errorCode) {
		logger.error("Authenticated exception: {}", errorCode);
	}

	private class SmokeAlarmListener implements NestConnector.EventListener {
		@Override
		public void onDataChange(String value) {
			if (!value.equals(Keys.SMOKE_CO_ALARM.SMOKE_ALARM_STATES.OK)) {
				logger.error("Smoke Protection state is not ok {}", value);
				nestConnector.setDeviceParam(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.HVAC_MODE,
						Keys.THERMOSTAT.HVAC_MODES.OFF);
			} else {
				logger.info("Smoke Protection state is ok {}", value);
				nestConnector.setDeviceParam(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.HVAC_MODE,
						Keys.THERMOSTAT.HVAC_MODES.HEAT_COOL);
			}
		}

		@Override
		public void onCancelled(int errorCode) {
			logger.error("Error {}", errorCode);
		}
	}

	private class ThermostatThemperatureListener implements NestConnector.EventListener {
		@Override
		public void onDataChange(String value) {
			temperature = Double.parseDouble(value);

		}

		@Override
		public void onCancelled(int errorCode) {
			logger.error("Error getting temperature{}", errorCode);
		}
	}
	
	private class ThermostatLowThemperatureListener implements NestConnector.EventListener {
		@Override
		public void onDataChange(String value) {
			minTemperature = Double.parseDouble(value);

		}

		@Override
		public void onCancelled(int errorCode) {
			logger.error("Error getting temperature{}", errorCode);
		}
	}
	
	private class ThermostatHighThemperatureListener implements NestConnector.EventListener {
		@Override
		public void onDataChange(String value) {
			maxTemperature = Double.parseDouble(value);

		}

		@Override
		public void onCancelled(int errorCode) {
			logger.error("Error getting temperature{}", errorCode);
		}
	}

	private class DefaultValueListenter implements NestConnector.EventListener {
		@Override
		public void onDataChange(String value) {
			logger.info("Value {}", value);

		}

		@Override
		public void onCancelled(int errorCode) {
			logger.error("Error {}", errorCode);

		}
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
		nestConnector.setDeviceParam(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_C, temperature);
	}

	public boolean getSecurity() {
		return security;
	}

	public void setSecurity(boolean security) {
		if (security) {
			nestConnector.addDeviceListener(Keys.SMOKE_CO_ALARMS, protectId, Keys.SMOKE_CO_ALARM.CO_ALARM_STATE,
					new SmokeAlarmListener());
		} else {
			nestConnector.removeEventListener(Keys.SMOKE_CO_ALARMS, protectId, Keys.SMOKE_CO_ALARM.CO_ALARM_STATE,
					new SmokeAlarmListener());
		}
		this.security = security;
	}

	public Double getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(Double minTemperature) {
		this.minTemperature = minTemperature;
		nestConnector.setDeviceParam(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_LOW_C, minTemperature);
	}

	public Double getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(Double maxTemperature) {
		this.maxTemperature = maxTemperature;
		nestConnector.setDeviceParam(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_HIGH_C, maxTemperature);
	}

	private void doAction() {
		setParameters();
		nestConnector.printAll(new DefaultValueListenter());
		nestConnector.printAllDevices(new DefaultValueListenter());
		printParameters();
	}

	private void setParameters() {
		nestConnector.setDeviceParam(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.FAN_TIMER_ACTIVE, true);
		nestConnector.setDeviceParam(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_C, 21.5);
	}

	private void printParameters() {
		nestConnector.addDeviceListener(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.FAN_TIMER_ACTIVE,
				new DefaultValueListenter());
		nestConnector.addDeviceListener(Keys.THERMOSTATS, thermostatId, Keys.THERMOSTAT.TARGET_TEMP_C,
				new DefaultValueListenter());

	}

}
