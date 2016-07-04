package org.tdc.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Book Config data transfer object for use with REST services. 
 */
public class BookConfigDTO {
	private final String addr;
	
	@JsonCreator
	public BookConfigDTO(
			@JsonProperty("bookAddress") String addr) {
		
		this.addr = addr;
	}
	
	public String getBookAddress() {
		return addr;
	}
	
	@Override
	public String toString() {
		return addr;
	}
}
