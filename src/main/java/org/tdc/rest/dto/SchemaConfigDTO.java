package org.tdc.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Schema Config data transfer object for use with REST services. 
 */
public class SchemaConfigDTO {
	private final String addr;
	
	@JsonCreator
	public SchemaConfigDTO(
			@JsonProperty("schemaAddress") String addr) {
		
		this.addr = addr;
	}
	
	public String getSchemaAddress() {
		return addr;
	}
}
