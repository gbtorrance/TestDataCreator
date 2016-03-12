package org.tdc.model;

/**
 * Defines functionality of notes that can be repeated.
 */
public interface Repeatable {
	int getMinOccurs();
	int getMaxOccurs();
	boolean isUnbounded();
}
