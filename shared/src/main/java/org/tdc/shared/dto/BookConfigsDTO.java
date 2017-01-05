package org.tdc.shared.dto;

import java.util.List;

/**
 * Data Transfer Object for use with REST services. 
 */
public class BookConfigsDTO {
	private List<BookConfigDTO> bookConfigs;
	private List<AddrErrorDTO> errors;

	public List<BookConfigDTO> getBookConfigs() {
		return bookConfigs;
	}
	
	public void setBookConfigs(List<BookConfigDTO> bookConfigs) {
		this.bookConfigs = bookConfigs;
	}
	
	public List<AddrErrorDTO> getErrors() {
		return errors;
	}
	
	public void setErrors(List<AddrErrorDTO> errors) {
		this.errors = errors;
	}
}
