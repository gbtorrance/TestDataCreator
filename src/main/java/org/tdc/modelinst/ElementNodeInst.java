package org.tdc.modelinst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.model.AttribNode;
import org.tdc.model.ElementNode;
import org.tdc.model.ModelVisitor;
import org.tdc.model.NonAttribNode;
import org.tdc.modeldef.ElementNodeDef;

/**
 * A {@link NonAttribNodeInst} implementation specific to XML elements.
 */
public class ElementNodeInst extends NonAttribNodeInst implements ElementNode {
	
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
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
		for (AttribNode attrib : getAttributes()) {
			attrib.accept(visitor);
		}
		for (NonAttribNode nonAttrib : getChildren()) {
			nonAttrib.accept(visitor);
		}
	}
}
