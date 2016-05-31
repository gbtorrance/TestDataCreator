package org.tdc.modelinst;

import org.tdc.model.AbstractTDCNode;
import org.tdc.modeldef.NodeDef;

/**
 * {@link AbstractTDCNode} implementation specific to 'instance' nodes.
 *  
 * <p>Adds the ability to associate a "definition" node ({@link NodeDef}) to an "instance" node ({@link NodeInst}).
 * 
 * @see AttribNodeInst
 * @see NonAttribNodeInst
 */
public abstract class NodeInst extends AbstractTDCNode {
	
	private final NodeDef nodeDef;
	
	public NodeInst(NonAttribNodeInst parent) {
		this(parent, null);
	}

	public NodeInst(NonAttribNodeInst parent, NodeDef nodeDef) {
		super(parent);
		this.nodeDef = nodeDef;
	}
	
	public NodeDef getNodeDef() {
		return nodeDef;
	}

	@Override
	public NonAttribNodeInst getParent() {
		return (NonAttribNodeInst)super.getParent();
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
	
	@Override
	public String getDispName() {
		return nodeDef.getDispName();
	}

	@Override
	public String getDispType() {
		return nodeDef.getDispType();
	}

	@Override
	public String getDispOccurs() {
		return nodeDef.getDispOccurs();
	}
}

