package org.tdc.modelinst;

import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.CompositorType;

/**
 * A {@link NonAttribNodeInst} implementation specific to "compositors".
 * 
 * <p>Compositors (such as "sequence" and "choice") are nodes that can contain other nodes.
 */
public class CompositorNodeInst extends NonAttribNodeInst {
	
	public CompositorNodeInst(NonAttribNodeInst parent, CompositorNodeDef compositorNodeDef) {
		super(parent, compositorNodeDef);
	}

	@Override
	public CompositorNodeDef getNodeDef() {
		return (CompositorNodeDef)super.getNodeDef();
	}

	public CompositorType getType() {
		return getNodeDef().getType();
	}

	@Override
	public String toShortString() {
		return getNodeDef().toShortString();
	}

	@Override
	public String toTestSummaryString() {
		return "[" + getType() + "], minOccurs:" + getMinOccurs() + ", maxOccurs:" + getMaxOccurs();
	}

	@Override
	public String toString() {
		return super.toString() + ", type (compositor def node): " + getNodeDef().getType();
	}
}
