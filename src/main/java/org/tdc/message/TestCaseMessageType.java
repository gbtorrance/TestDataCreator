package org.tdc.message;

import org.tdc.book.TestCase;

/**
 * {@link MessageType} enums specific to {@link TestCase} processing.
 */
public enum TestCaseMessageType implements MessageType {
	RULE_IGNORE("Ignore"),
	RULE_REJECT("Reject");
	
	private final String label;
	
	private TestCaseMessageType(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
}
