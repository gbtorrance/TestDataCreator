package org.tdc.modeldef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.model.CanHaveAttributes;
import org.tdc.model.Nameable;
import org.tdc.model.Typeable;

public class ElementNodeDef extends NonAttribNodeDef implements CanHaveAttributes, Nameable, Typeable {
	
	private String name;
	private String dataType;
	private List<AttribNodeDef> attributes = new ArrayList<>();
	
	private String lineNum;     // TODO make generic; MeF specific
	private String formNum;     // TODO make generic; MeF specific
	private String description; // TODO make generic; MeF specific
	
	public ElementNodeDef(NonAttribNodeDef parent) {
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
	public List<AttribNodeDef> getAttributes() {
		return Collections.unmodifiableList(attributes);
	}
	
	@Override
	public boolean hasAttribute() {
		return attributes.size() > 0;
	}
	
	// intentionally package level
	void addAttribute(AttribNodeDef attribute) {
		attributes.add(attribute);
	}
	
	@Override
	public String toShortString() {
		return name;
	}

	@Override
	public String toTestSummaryString() {
		return name + ", type:" + dataType + ", minOccurs:" + getMinOccurs() + ", maxOccurs:" + getMaxOccurs();
	}

	@Override
	public String toString() {
		return super.toString() + ", name: " + name + ", dataType: " + dataType;
	}

	
	
	// TODO make generic; MeF specific
	public String getLineNum() {
		return lineNum;
	}
	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}
	public String getFormNum() {
		return formNum;
	}
	public void setFormNum(String formNum) {
		this.formNum = formNum;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
