package org.tdc.modeldef;

import org.tdc.model.AbstractNode;

/**
 * Abstract implementation of {@link AbstractNode} that is specific to "definition" Models ({@link org.tdc.modeldef.ModelDef ModelDef}).
 * 
 * @see NodeDef
 */
public abstract class AbstractNodeDef extends AbstractNode {

	public AbstractNodeDef(AbstractNodeDef parent) {
		super(parent);
	}
	
	@Override
	public AbstractNodeDef getParent() {
		return (AbstractNodeDef)super.getParent();
	}

	@Override
	protected void setMPath(String mpath) {
		// overriding to allow accessibility to other classes in this package
		super.setMPath(mpath);
	}
}
