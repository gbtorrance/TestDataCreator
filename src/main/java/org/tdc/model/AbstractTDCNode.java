package org.tdc.model;

/**
 * Abstract {@link TDCNode} implementation.
 */
public abstract class AbstractTDCNode implements TDCNode {

	private final TDCNode parent;
	
	private String mpath;
	private int colOffset = -1;
	private int rowOffset = -1;

	public AbstractTDCNode(TDCNode parent) {
		this.parent = parent;
	}

	@Override
	public TDCNode getParent() {
		return parent;
	}
	
	@Override
	public String getMPath() {
		return mpath;
	}
	
	protected void setMPath(String mpath) {
		this.mpath = mpath;
	}
	
	@Override
	public int getColOffset() {
		return colOffset;
	}
	
	protected void setColOffset(int colOffset) {
		this.colOffset = colOffset;
	}
	
	@Override
	public int getRowOffset() {
		return rowOffset;
	}
	
	protected void setRowOffset(int rowOffset) {
		this.rowOffset = rowOffset;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
				", name: " + getDispName() + ", type: " + getDispType() + ", occurs: " + getDispOccurs() +
				", rowOffset: " + rowOffset + ", colOffset: " + colOffset;
	}
}
