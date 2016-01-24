package org.tdc.model;

import java.util.List;

public interface CanHaveAttributes {
	List<? extends Attrib> getAttributes();
	boolean hasAttribute();
}
