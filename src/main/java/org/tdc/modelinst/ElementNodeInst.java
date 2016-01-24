package org.tdc.modelinst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.model.CanHaveAttributes;
import org.tdc.model.Nameable;
import org.tdc.model.Typeable;
import org.tdc.modeldef.ElementNodeDef;

public class ElementNodeInst extends NonAttribNodeInst implements CanHaveAttributes, Nameable, Typeable {
	
	private List<AttribNodeInst> attributes = new ArrayList<>();
	
	public ElementNodeInst(NonAttribNodeInst parent, ElementNodeDef elementNodeDef) {
		super(parent, elementNodeDef);
	}
	
	@Override
	public List<AttribNodeInst> getAttributes() {
		return Collections.unmodifiableList(attributes);
	}
	
	@Override
	public boolean hasAttribute() {
		return attributes.size() > 0;
	}
	
	// intentionally package level
	void addAttribute(AttribNodeInst attribute) {
		attributes.add(attribute);
	}
	
	@Override
	public ElementNodeDef getNodeDef() {
		return (ElementNodeDef)super.getNodeDef();
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
	public String toShortString() {
		return getNodeDef().toShortString();
	}

	@Override
	public String toTestSummaryString() {
		return getName() + ", type:" + getDataType() + ", minOccurs:" + getMinOccurs() + ", maxOccurs:" + getMaxOccurs();
	}

	@Override
	public String toString() {
		return super.toString() +  ", name (element def node): " + getNodeDef().getName();
	}

}
