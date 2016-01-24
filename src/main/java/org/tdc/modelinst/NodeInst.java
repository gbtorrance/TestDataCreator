package org.tdc.modelinst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.modeldef.NodeDef;

public abstract class NodeInst extends AbstractNodeInst {
	
	private NodeDef nodeDef;
	private List<NoteInst> preNotes;
	private List<NoteInst> postNotes;
	private int colOffset = -1;
	private int rowOffset = -1;
	
	protected NodeInst(NonAttribNodeInst parent) {
		this(parent, null);
	}

	protected NodeInst(NonAttribNodeInst parent, NodeDef nodeDef) {
		super(parent);
		this.nodeDef = nodeDef;
	}
	
	@Override
	public NonAttribNodeInst getParent() {
		return (NonAttribNodeInst)super.getParent();
	}
	
	public NodeDef getNodeDef() {
		return nodeDef;
	}
	
	public List<NoteInst> getPreNotes() {
		return preNotes != null ? Collections.unmodifiableList(preNotes) : null;
	}
	
	public void addPreNote(NoteInst note) {
		if (preNotes == null) {
			preNotes = new ArrayList<>();
		}
		preNotes.add(note);
	}

	public boolean hasPreNote() {
		return preNotes != null && preNotes.size() > 0;
	}
	
	public List<NoteInst> getPostNotes() {
		return postNotes != null ? Collections.unmodifiableList(postNotes) : null;
	}
	
	public void addPostNote(NoteInst note) {
		if (postNotes == null) {
			postNotes = new ArrayList<>();
		}
		postNotes.add(note);
	}

	public boolean hasPostNote() {
		return postNotes != null && postNotes.size() > 0;
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
	
	// this method gets the offset of the actual data value (ignoring "pre note" rows)
	public int getDataRowOffset() {
		return rowOffset + (preNotes == null ? 0 : preNotes.size());
	}
	
	// intentionally package level
	void setRowOffset(int rowOffset) {
		this.rowOffset = rowOffset;
	}
	
	public int getRowDepth() {
		return 1 + (preNotes == null ? 0 : preNotes.size()) + (postNotes == null ? 0 : postNotes.size());
	}
	
//	public boolean isInOccurPath() {
//		return isInOccurPath;
//	}
//	
//	// intentionally package level
//	void setIsInOccurPath(boolean isInOccurPath) {
//		this.isInOccurPath = isInOccurPath;
//	}
	
	@Override
	public String toString() {
//		return super.toString() + ", rowOffset: " + rowOffset + ", colOffset: " + colOffset + ", isInOccurPath: " + isInOccurPath;
		return super.toString() + ", rowOffset: " + rowOffset + ", colOffset: " + colOffset;		
	}
}

