package org.tdc.modelinst;

import org.tdc.model.Attrib;
import org.tdc.model.Nameable;
import org.tdc.model.Requireable;
import org.tdc.model.Typeable;
import org.tdc.modeldef.AttribNodeDef;

public class AttribNodeInst extends NodeInst implements Attrib, Nameable, Typeable, Requireable {
	
	public AttribNodeInst(ElementNodeInst parent, AttribNodeDef attribNodeDef) {
		super(parent, attribNodeDef);
	}
	
	@Override
	public AttribNodeDef getNodeDef() {
		return (AttribNodeDef)super.getNodeDef();
	}

	@Override
	public String getName() {
		return getNodeDef().getName();
	}
	
	@Override
	public String getDataType() {
		return getNodeDef().getDataType();
	}

	@Override
	public boolean isRequired() {
		return getNodeDef().isRequired();
	}

	@Override
	public String toShortString() {
		return getNodeDef().toShortString();
	}

	@Override
	public String toTestSummaryString() {
		return "@" + getName() + ", type:" + getDataType() + ", req:" + isRequired();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
