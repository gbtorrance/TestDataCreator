package org.tdc.shared.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class BookConfigDTO {
	private String addr;
	private String bookName;
	private String bookDescription;
	
	public String getBookAddress() {
		return addr;
	}
	
	public void setBookAddress(String addr) {
		this.addr = addr;
	}
	
	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookDescription() {
		return bookDescription;
	}

	public void setBookDescription(String bookDescription) {
		this.bookDescription = bookDescription;
	}

	@Override
	public String toString() {
		return addr;
	}
}
