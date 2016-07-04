package org.tdc.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model Config data transfer object for use with REST services. 
 */
public class ModelConfigDTO {
	private final String addr;
	
	@JsonCreator
	public ModelConfigDTO(
			@JsonProperty("modelAddress") String addr) {
		
		this.addr = addr;
	}
	
	public String getModelAddress() {
		return addr;
	}
	
	@Override
	public String toString() {
		return addr;
	}
}
