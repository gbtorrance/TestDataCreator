package org.tdc.shared.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class ModelConfigDTO {
	private String addr;
	private String modelName;
	private String modelDescription;
	
	public String getModelAddress() {
		return addr;
	}
	
	public void setModelAddress(String addr) {
		this.addr = addr;
	}
	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	@Override
	public String toString() {
		return addr;
	}
}
