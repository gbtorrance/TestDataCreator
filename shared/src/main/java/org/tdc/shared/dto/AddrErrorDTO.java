package org.tdc.shared.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class AddrErrorDTO {
	private String addr;
	private String errorMessage;
	
	public AddrErrorDTO() {
		
	}

	public AddrErrorDTO(String addr, String errorMessage) {
		this.addr = addr;
		this.errorMessage = errorMessage;
	}

	public String getAddr() {
		return addr;
	}
	
	public void setAddr(String addr) {
		this.addr = addr;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
