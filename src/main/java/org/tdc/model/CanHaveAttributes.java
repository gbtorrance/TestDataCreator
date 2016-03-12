package org.tdc.model;

import java.util.List;

/**
 * Defines functionality of nodes that can have child attributes.
 */
public interface CanHaveAttributes {
	List<? extends Attrib> getAttributes();
	boolean hasAttribute();
}
