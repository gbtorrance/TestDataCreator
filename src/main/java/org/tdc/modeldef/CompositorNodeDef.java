package org.tdc.modeldef;

import org.tdc.modeldef.CompositorType;

/**
 * A {@link NonAttribNodeDef} implementation specific to "compositors".
 * 
 * <p>Compositors (such as "sequence" and "choice") are nodes that can contain other nodes.
 */
public class CompositorNodeDef extends NonAttribNodeDef {
	
	private CompositorType type;

	public CompositorNodeDef(NonAttribNodeDef parent, CompositorType type) {
		super(parent);
		this.type = type;
	}
	
	public CompositorType getType() {
		return type;
	}
	
	@Override
	public String toShortString() {
		return "[" + type + "]";
	}

	@Override
	public String toTestSummaryString() {
		return "[" + type + "], minOccurs:" + getMinOccurs() + ", maxOccurs:" + getMaxOccurs();
	}

	@Override
	public String toString() {
		return super.toString() + ", type: " + type;
	}
}
