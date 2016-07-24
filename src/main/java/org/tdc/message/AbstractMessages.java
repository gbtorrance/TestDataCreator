package org.tdc.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract collection and means of processing {@link Message} objects 
 * of various {@link MessageType}s. 
 * 
 * <p>{@link Message}s are used, typically, when validating {@link TestDoc}s and {@link TestCase}s 
 * as a means of providing feedback to the user.
 * 
 * @see TestCaseMessages
 * @see TestDocMessages
 */
public abstract class AbstractMessages {
	
	private final Map<MessageType, List<Message>> messages = new HashMap<>();
	
	public abstract Collection<MessageType> getValidMessageTypes();

	public Collection<MessageType> getMessageTypes() {
		return messages.keySet();
	}
	
	public List<Message> getMessagesByType(MessageType type) {
		return Collections.unmodifiableList(
				messages.getOrDefault(type, Collections.EMPTY_LIST));
	}
	
	public int getMessageCountByType(MessageType type) {
		List<Message> list = messages.get(type);
		return list == null ? 0 : list.size();
	}
	
	public List<Message> getAllMessages() {
		List<Message> listAll = new ArrayList<>();
		messages.values().stream().forEach(list -> listAll.addAll(list));
		return Collections.unmodifiableList(listAll);
	}
	
	public void addMessage(MessageType type, String message) {
		Message m = new Message.Builder(type, message).build();
		addMessage(m);
	}
	
	public void addMessage(Message message) {
		MessageType type = message.getType();
		if (!getValidMessageTypes().contains(type)) {
			throw new IllegalStateException("Invalid message type: " + type.getLabel());
		}
		List<Message> list = messages.get(type);
		if (list == null) {
			list = new ArrayList<>();
			messages.put(type, list);
		}
		list.add(message);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Messages:");
		for (MessageType type : getValidMessageTypes()) {
			sb.append(" [").append(type.getLabel()).append(":").append(getMessageCountByType(type)).append("]");
		}
		return sb.toString();
	}
}
