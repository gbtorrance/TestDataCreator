package org.tdc.modelcustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.ModelVisitor;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;

public class ModelCustomizerWriter extends AbstractModelCustomizer {

	private static final Logger log = LoggerFactory.getLogger(ModelCustomizerWriter.class);

	private int maxColOffset;
	
	public ModelCustomizerWriter(ElementNodeDef rootElement, ModelCustomizerFormat format, Spreadsheet sheet) {
		super(rootElement, format, sheet);
	}
	
	public void writeCustomizer() {
		getSheet().setDefaultCellStyle(getFormat().getDefaultNodeStyle());
		maxColOffset = 0;
		writeTree();
		formatColumns();
	}
	
	private void writeTree() {
		ElementNodeDef rootElement = getRootElement();
		rootElement.accept(new WriteVisitor());
	}
	
	private void processAttribNode(AttribNode node) {
		trackMaxColOffset(node);

		outputNodeName(node, getFormat().getAttribNodeStyle());
		outputNodeOccurs(node);
	}

	private void processCompositorNode(CompositorNode node) {
		trackMaxColOffset(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, getFormat().getChoiceMarkerStyle());
		}
		
		outputNodeName(node, getFormat().getCompositorNodeStyle());
		outputNodeOccurs(node);
	}
	
	private void processElementNode(ElementNode node) {
		trackMaxColOffset(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, getFormat().getChoiceMarkerStyle());
		}
		
		CellStyle cellStyle = getFormat().getDefaultNodeStyle();
		if (node.hasChild()) {
			cellStyle = getFormat().getParentNodeStyle();
		}

		outputNodeName(node, cellStyle);
		outputNodeOccurs(node);
	}
	
	private void trackMaxColOffset(TDCNode node) {
		maxColOffset = Integer.max(maxColOffset, node.getColOffset());
	}
	
	private void outputNodeName(TDCNode node, CellStyle cellStyle) {
		getSheet().setCellValue(node.getDispName(), getRowStart() + node.getRowOffset(), 
				getColStart() + node.getColOffset(), cellStyle);
	}
	
	private void outputNodeOccurs(TDCNode node) {
		getSheet().setCellValue(node.getDispOccurs(), getRowStart() + node.getRowOffset(), 
				getColStart() + getFormat().getTreeStructureColumnCount());
	}

	private void outputChoiceMarker(NonAttribNode node, CellStyle cellStyle) {
		getSheet().setCellValue(">", getRowStart() + node.getRowOffset(), 
				getColStart() + node.getColOffset() - 1, cellStyle);
	}

	private void formatColumns() {
		int cols = getFormat().getTreeStructureColumnCount(); 
		if (cols < maxColOffset + 1) {
			throw new RuntimeException("TreeStructureColumnCount (" + cols + 
					") must be at least " + (maxColOffset+1) + " to support this particular model");
		}
		for (int i = 1; i <= cols; i++) {
			getSheet().setColumnWidth(i, getFormat().getTreeStructureColumnWidth());
		}
	}
	
	class WriteVisitor implements ModelVisitor {
		@Override
		public void visit(AttribNode attribNode) {
			processAttribNode(attribNode);
		}

		@Override
		public void visit(CompositorNode compositorNode) {
			processCompositorNode(compositorNode);
		}

		@Override
		public void visit(ElementNode elementNode) {
			processElementNode(elementNode);
		}
	}
}
