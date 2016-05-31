package org.tdc.modeldef;

import org.tdc.model.CompositorNode;
import org.tdc.model.CompositorType;
import org.tdc.model.ModelVisitor;
import org.tdc.model.NonAttribNode;

/**
 * A {@link NonAttribNodeDef} implementation specific to "compositors".
 * 
 * <p>Compositors (such as "sequence" and "choice") are nodes that can contain other nodes.
 */
public class CompositorNodeDef extends NonAttribNodeDef implements CompositorNode {
	
	private CompositorType compositorType;

	public CompositorNodeDef(NonAttribNodeDef parent, CompositorType compositorType) {
		super(parent);
		this.compositorType = compositorType;
	}
	
	@Override
	public CompositorType getCompositorType() {
		return compositorType;
	}
	
	@Override
	public String getDispName() {
		return "[" + compositorType + "]";
	}

	@Override
	public String getDispType() {
		return "";
	}
	
	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
		for (NonAttribNode nonAttrib : getChildren()) {
			nonAttrib.accept(visitor);
		}
	}
}
