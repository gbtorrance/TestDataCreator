package org.tdc.model;

import org.tdc.shared.util.SharedConst;

/**
 * Abstract {@link TDCNode} implementation.
 */
public abstract class AbstractTDCNode implements TDCNode {

	private final TDCNode parent;
	private final ModelSharedState sharedState;
	
	private String mpath;
	private int colOffset = SharedConst.UNDEFINED;
	private int rowOffset = SharedConst.UNDEFINED;

	public AbstractTDCNode(TDCNode parent, ModelSharedState sharedState) {
		this.parent = parent;
		this.sharedState = sharedState;
	}

	@Override
	public TDCNode getParent() {
		return parent;
	}
	
	@Override
	public String getMPath() {
		return mpath;
	}
	
	public void setMPath(String mpath) {
		getSharedState().throwExceptionIfImmutable("setMPath");
		this.mpath = mpath;
	}
	
	@Override
	public int getColOffset() {
		return colOffset;
	}
	
	public void setColOffset(int colOffset) {
		getSharedState().throwExceptionIfImmutable("setColOffset");
		this.colOffset = colOffset;
	}
	
	@Override
	public int getRowOffset() {
		return rowOffset;
	}
	
	public void setRowOffset(int rowOffset) {
		getSharedState().throwExceptionIfImmutable("setRowOffset");
		this.rowOffset = rowOffset;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
				", name: " + getDispName() + ", occurs: " + getDispOccurs() +
				", rowOffset: " + rowOffset + ", colOffset: " + colOffset;
	}

	protected ModelSharedState getSharedState() {
		return sharedState;
	}
}
