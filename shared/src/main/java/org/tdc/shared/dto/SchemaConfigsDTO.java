package org.tdc.shared.dto;

import java.util.List;

/**
 * Data Transfer Object for use with REST services. 
 */
public class SchemaConfigsDTO {
	private List<SchemaConfigDTO> schemaConfigs;
	private List<AddrErrorDTO> errors;

	public List<SchemaConfigDTO> getSchemaConfigs() {
		return schemaConfigs;
	}
	
	public void setSchemaConfigs(List<SchemaConfigDTO> schemaConfigs) {
		this.schemaConfigs = schemaConfigs;
	}
	
	public List<AddrErrorDTO> getErrors() {
		return errors;
	}
	
	public void setErrors(List<AddrErrorDTO> errors) {
		this.errors = errors;
	}
}
