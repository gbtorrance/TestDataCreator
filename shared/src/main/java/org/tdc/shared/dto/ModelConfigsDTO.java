package org.tdc.shared.dto;

import java.util.List;

/**
 * Data Transfer Object for use with REST services. 
 */
public class ModelConfigsDTO {
	private List<ModelConfigDTO> modelConfigs;
	private List<AddrErrorDTO> errors;

	public List<ModelConfigDTO> getModelConfigs() {
		return modelConfigs;
	}
	
	public void setModelConfigs(List<ModelConfigDTO> modelConfigs) {
		this.modelConfigs = modelConfigs;
	}
	
	public List<AddrErrorDTO> getErrors() {
		return errors;
	}
	
	public void setErrors(List<AddrErrorDTO> errors) {
		this.errors = errors;
	}
}
