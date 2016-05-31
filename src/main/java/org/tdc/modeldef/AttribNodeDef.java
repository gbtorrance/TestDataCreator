package org.tdc.modeldef;

import org.tdc.model.AttribNode;
import org.tdc.model.ModelVisitor;

/**
 * A {@link NodeDef} implementation specific to attributes.
 */
public class AttribNodeDef extends NodeDef implements AttribNode {
	
	private String name;
	private String dataType;
	private boolean isRequired;
	
	public AttribNodeDef(ElementNodeDef parent) {
		super(parent);
	}
	
	@Override
	public String getName() {
		return name;
	}

	// intentionally package level
	void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getDataType() {
		return dataType;
	}
	
	// intentionally package level
	void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public boolean isRequired() {
		return isRequired;
	}
	
	// intentionally package level
	void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public ElementNodeDef getParent() {
		return (ElementNodeDef)super.getParent();
	}

	@Override
	public String getDispName() {
		return "@" + getName();
	}
	
	@Override
	public String getDispType() {
		return dataType;
	}
	
	@Override 
	public String getDispOccurs() {
		return isRequired ? "1..1" : "0..1";
	}
	
	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean isChildOfChoice() {
		// attributes will always be children of elements, nothing else
		return false;
	}
}
