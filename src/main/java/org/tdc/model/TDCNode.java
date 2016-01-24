package org.tdc.model;

//named with TDC prefix to avoid confusion with w3c Node object
public interface TDCNode {
	AbstractNode getParent();
	String getMPath();
	String toShortString();
	String toTestSummaryString();
}
