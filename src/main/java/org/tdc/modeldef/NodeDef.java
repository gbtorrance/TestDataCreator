package org.tdc.modeldef;

public abstract class NodeDef extends AbstractNodeDef {
	
	protected NodeDef(NonAttribNodeDef parent) {
		super(parent);
	}
	
	@Override
	public NonAttribNodeDef getParent() {
		return (NonAttribNodeDef)super.getParent();
	}
}

// TODO Add an AnyElementNode child of ElementNode?
// TODO Consider 'groups'; probably a non-issue, though; just syntactical sugar for schemas
// TODO Add annotations (probably at NonAttributeNode level)
// TODO Add augmenting info
// TODO Consider list, union, restriction on simple types
// TODO Consider restriction and extension on simple and complex content
// TODO Test empty element content type

