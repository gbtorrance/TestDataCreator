package org.tdc.model;

public interface Repeatable {
	int getMinOccurs();
	int getMaxOccurs();
	boolean isUnbounded();
}
