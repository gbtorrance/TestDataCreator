package org.tdc.message;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.tdc.book.TestCase;

/**
 * {@link AbstractMessages} subclass supporting {@link MessageType}s specific to {@link TestCase}s.
 */
public class TestCaseMessages extends AbstractMessages {
	private static final Collection<MessageType> VALID_MESSAGE_TYPES = 
			Collections.unmodifiableCollection(
					Arrays.asList(TestCaseMessageType.values()));
	
	@Override
	public Collection<MessageType> getValidMessageTypes() {
		return VALID_MESSAGE_TYPES;
	}
}
