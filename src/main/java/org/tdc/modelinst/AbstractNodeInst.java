package org.tdc.modelinst;

import org.tdc.model.AbstractNode;

/**
 * Abstract implementation of {@link AbstractNode} that is specific to "instance" Models ({@link org.tdc.modelinst.ModelInst ModelInst}).
 *
 * @see NodeInst
 * @see NoteInst
 */
public abstract class AbstractNodeInst extends AbstractNode {

	public AbstractNodeInst(AbstractNodeInst parent) {
		super(parent);
	}
	
	@Override
	public AbstractNodeInst getParent() {
		return (AbstractNodeInst)super.getParent();
	}

	@Override
	protected void setMPath(String mpath) {
		// overriding to allow accessibility to other classes in this package
		super.setMPath(mpath);
	}
}
