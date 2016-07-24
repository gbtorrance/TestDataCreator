package org.tdc.message;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.tdc.book.TestDoc;

/**
 * {@link AbstractMessages} subclass supporting {@link MessageType}s specific to {@link TestDoc}s.
 */
public class TestDocMessages extends AbstractMessages {
	private static final Collection<MessageType> VALID_MESSAGE_TYPES = 
			Collections.unmodifiableCollection(
					Arrays.asList(TestDocMessageType.values()));
	
	@Override
	public Collection<MessageType> getValidMessageTypes() {
		return VALID_MESSAGE_TYPES;
	}
}
