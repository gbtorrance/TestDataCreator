package org.tdc.util;

import java.util.ArrayList;
import java.util.List;

import org.tdc.model.Attrib;
import org.tdc.model.CanHaveAttributes;
import org.tdc.model.CanHaveChildren;
import org.tdc.model.NonAttrib;
import org.tdc.model.TDCNode;

public abstract class AbstractModelWriterForTesting {
	
	private TDCNode rootNode;
	private int indentSize;
	
	public AbstractModelWriterForTesting(TDCNode rootNode, int indentSize) {
		this.rootNode = rootNode;
		this.indentSize = indentSize;
	}
	
	public String writeToString() {
		List<String> lines = writeToStringList();
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	public List<String> writeToStringList() {
		List<String> lines = new ArrayList<>();
		processNode(lines, rootNode, 0);
		return lines;
	}
	
	protected int getIndentSize() {
		return indentSize;
	}
	
	private void processNode(List<String> lines, TDCNode node, int level) {
		String line = tempBuildNodeString(node, level); 
		lines.add(line);
		if (node instanceof CanHaveAttributes) {
			CanHaveAttributes canHaveAttributes = (CanHaveAttributes)node;
			List<? extends Attrib> attribs = canHaveAttributes.getAttributes();
			for (Attrib attrib : attribs) {
				processNode(lines, attrib, level+1);
			}
		}
		if (node instanceof CanHaveChildren) {
			CanHaveChildren canHaveChildren = (CanHaveChildren)node;
			List<? extends NonAttrib> nonAttribs = canHaveChildren.getChildren();
			for (NonAttrib nonAttrib : nonAttribs) {
				processNode(lines, nonAttrib, level+1);
			}
		}
	}
	
	protected abstract String tempBuildNodeString(TDCNode node, int level); 
}
