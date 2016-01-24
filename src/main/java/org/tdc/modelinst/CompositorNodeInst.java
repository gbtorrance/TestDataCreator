package org.tdc.modelinst;

import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.CompositorType;

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
