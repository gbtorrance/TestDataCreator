package org.tdc.message;

import org.tdc.book.TestDoc;

/**
 * {@link MessageType} enums specific to {@link TestDoc} processing.
 */
public enum TestDocMessageType implements MessageType {
	WARNING("Warning"),
	SCHEMA_ERROR("Schema Error"),
	SCHEMA_WARNING("Schema Warning"),
	SCHEMA_FATAL_ERROR("Schema Fatal Error");
	
	private final String label;
	
	private TestDocMessageType(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
}
