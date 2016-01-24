package org.tdc.modelinst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.model.CanHaveChildren;
import org.tdc.model.NonAttrib;
import org.tdc.model.Repeatable;
import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.CompositorType;
import org.tdc.modeldef.NonAttribNodeDef;

public abstract class NonAttribNodeInst extends NodeInst implements NonAttrib, CanHaveChildren, Repeatable {
	
	private List<NonAttribNodeInst> children = new ArrayList<>();
	private int occurNum;
	private int occurCount;
//	private boolean isChoiceNode; // in other words, an immediate child of a choice compositor
	
	protected NonAttribNodeInst(NonAttribNodeInst parent, NonAttribNodeDef nonAttribNodeDef) {
		super(parent, nonAttribNodeDef);
	}
	
	@Override
	public List<NonAttribNodeInst> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public boolean hasChild() {
		return children.size() > 0;
	}
	
	// intentionally package level
	void addChild(NonAttribNodeInst child) {
		children.add(child);
	}

	// intentionally package level
	void addChildAll(List<? extends NonAttribNodeInst> childList) {
		children.addAll(childList);
	}

	@Override
	public int getMinOccurs() {
		return getNodeDef().getMinOccurs();
	}

	@Override
	public int getMaxOccurs() {
		return getNodeDef().getMaxOccurs();
	}

	@Override
	public boolean isUnbounded() {
		return getNodeDef().isUnbounded();
	}

	public int getOccurNum() {
		return occurNum;
	}
	
	// intentionally package level
	void setOccurNum(int occurNum) {
		this.occurNum = occurNum;
	}

	public int getOccurCount() {
		return occurCount;
	}
	
	// intentionally package level
	void setOccurCount(int occurCount) {
		this.occurCount = occurCount;
	}
	
	public boolean isChildOfChoice() {
		boolean result = false;
		// since compositor 'inst' nodes are sometimes removed in the 'inst' model for visual clarity,
		// we need to check the parent of the 'def' node to see if it is a choice
		NonAttribNodeDef parentOfDefNode = getNodeDef().getParent();
		if (parentOfDefNode != null && 
				parentOfDefNode instanceof CompositorNodeDef &&
				((CompositorNodeDef)parentOfDefNode).getType() == CompositorType.CHOICE) {
			result = true;
		}
		return result;
	}
	
	public boolean isFirstChildOfChoice() {
		return occurNum == 1 && isChildOfChoice();
	}

//	public boolean isChoiceNode() {
//		return isChoiceNode;
//	}
//	
//	// intentionally package level
//	void setIsChoiceNode(boolean isChoiceNode) {
//		this.isChoiceNode = isChoiceNode;
//	}
	
	@Override
	public NonAttribNodeDef getNodeDef() {
		return (NonAttribNodeDef)super.getNodeDef();
	}

	@Override
	public String toString() {
//		return super.toString() + ", occurNum: " + occurNum + ", isChoiceNode: " + isChoiceNode;
		return super.toString() + ", occurNum: " + occurNum + ", occurCount: " + occurCount;		
	}
}
