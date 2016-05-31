package org.tdc.model;

/**
 * Interface for attribute nodes. 
 */
public interface AttribNode extends TDCNode, Nameable, DataTypeable {
	boolean isRequired();
}
