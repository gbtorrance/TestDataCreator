package org.tdc.book;

import org.tdc.modelinst.ModelInst;

/**
 * Defines core functionality for a Page.
 * 
 * <p> Pages are contained within {@link Book}s and each refers to 
 * a particular {@link ModelInst} which defines the structure of the Page. 
 */
public interface Page {
	String getName();
	ModelInst getModelInst();
}
