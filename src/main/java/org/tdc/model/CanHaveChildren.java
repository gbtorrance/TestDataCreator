package org.tdc.model;

import java.util.List;

public interface CanHaveChildren {
	List<? extends NonAttrib> getChildren();
	boolean hasChild();
}
