package org.tdc.model;

import java.util.List;

/**
 * Defines functionality of notes that can have children.
 */
public interface CanHaveChildren {
	List<? extends NonAttrib> getChildren();
	boolean hasChild();
}
