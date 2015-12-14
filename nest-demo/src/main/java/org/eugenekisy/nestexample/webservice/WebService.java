package org.eugenekisy.nestexample.webservice;

import org.eugenekisy.nestexample.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/WebService/")
public class WebService {

	@Autowired
	private Service service;

	@RequestMapping(value = "temperature", method = RequestMethod.GET)
	public Integer getTemperature() {
		return service.getTemperature().intValue();
	}

	@RequestMapping(value = "temperature", method = RequestMethod.POST)
	public void setTemperature(@RequestParam(value = "t", required = true) Double temperature) {
		service.setTemperature(temperature);
	}
	
	@RequestMapping(value = "min_temperature", method = RequestMethod.GET)
	public Integer getMinTemperature() {
		return service.getMinTemperature().intValue();
	}

	@RequestMapping(value = "min_temperature", method = RequestMethod.POST)
	public void setMinTemperature(@RequestParam(value = "t", required = true) Double temperature) {
		service.setMinTemperature(temperature);
	}
	
	@RequestMapping(value = "max_temperature", method = RequestMethod.GET)
	public Integer getMaxTemperature() {
		return service.getMaxTemperature().intValue();
	}

	@RequestMapping(value = "max_temperature", method = RequestMethod.POST)
	public void setMaxTemperature(@RequestParam(value = "t", required = true) Double temperature) {
		service.setMaxTemperature(temperature);
	}
	
	@RequestMapping(value = "security", method = RequestMethod.GET)
	public boolean getSecurity() {
		return service.getSecurity();
	}
	
	@RequestMapping(value = "security", method = RequestMethod.POST)
	public void setSecurity(@RequestParam(value = "s", required = true) boolean security) {
		service.setSecurity(security);
	}

}
