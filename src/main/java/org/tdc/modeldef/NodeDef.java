package org.tdc.modeldef;

/**
 * Abstract {@link AbstractNodeDef} implementation.
 * 
 * @see AttribNodeDef
 * @see NonAttribNodeDef
 */
public abstract class NodeDef extends AbstractNodeDef {
	
	private int colOffset = -1;
	private int rowOffset = -1;

	protected NodeDef(NonAttribNodeDef parent) {
		super(parent);
	}
	
	@Override
	public NonAttribNodeDef getParent() {
		return (NonAttribNodeDef)super.getParent();
	}
	
	public int getColOffset() {
		return colOffset;
	}
	
	// intentionally package level
	void setColOffset(int colOffset) {
		this.colOffset = colOffset;
	}
	
	public int getRowOffset() {
		return rowOffset;
	}
	
	// intentionally package level
	void setRowOffset(int rowOffset) {
		this.rowOffset = rowOffset;
	}
}

// TODO Add an AnyElementNode child of ElementNode?
// TODO Consider 'groups'; probably a non-issue, though; just syntactical sugar for schemas
// TODO Add annotations (probably at NonAttributeNode level)
// TODO Add augmenting info
// TODO Consider list, union, restriction on simple types
// TODO Consider restriction and extension on simple and complex content
// TODO Test empty element content type

