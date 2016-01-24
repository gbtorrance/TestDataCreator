package org.tdc.model;

public abstract class AbstractNode implements TDCNode {

	private AbstractNode parent;
	private String mpath;

	public AbstractNode(AbstractNode parent) {
		this.parent = parent;
	}

	@Override
	public AbstractNode getParent() {
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
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
		
	}
}
