package org.tdc.modeldef;

import org.tdc.model.Attrib;
import org.tdc.model.Nameable;
import org.tdc.model.Requireable;
import org.tdc.model.Typeable;

public class AttribNodeDef extends NodeDef implements Attrib, Nameable, Typeable, Requireable {
	
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
	void setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public ElementNodeDef getParent() {
		return (ElementNodeDef)super.getParent();
	}

	@Override
	public String toShortString() {
		return "@" + getName();
	}

	@Override
	public String toTestSummaryString() {
		return "@" + name + ", type:" + dataType + ", req:" + isRequired + "";
	}

	@Override
	public String toString() {
		return super.toString() + ", name: " + "@" + name + ", dataType: " + dataType;
	}
}
