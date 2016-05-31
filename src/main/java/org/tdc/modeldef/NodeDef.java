package org.tdc.modeldef;

import org.tdc.model.AbstractTDCNode;

/**
 * {@link AbstractTDCNode} implementation specific to 'definition' nodes. 
 * 
 * @see AttribNodeDef
 * @see NonAttribNodeDef
 */
public abstract class NodeDef extends AbstractTDCNode {
	
	public NodeDef(NonAttribNodeDef parent) {
		super(parent);
	}
	
	@Override
	public NonAttribNodeDef getParent() {
		return (NonAttribNodeDef)super.getParent();
	}

	@Override
	protected void setMPath(String mpath) {
		// overriding to allow accessibility to other classes in this package
		super.setMPath(mpath);
	}

	@Override
	protected void setColOffset(int colOffset) {
		// overriding to allow accessibility to other classes in this package
		super.setColOffset(colOffset);
	}
	
	@Override
	protected void setRowOffset(int rowOffset) {
		// overriding to allow accessibility to other classes in this package
		super.setRowOffset(rowOffset);
	}
}
