package org.tdc.book;

import java.util.Collection;

import org.tdc.config.book.BookConfig;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.ModelVisitor;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;
import org.tdc.modelinst.ElementNodeInst;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.NonAttribNodeInst;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * A {@link BookWriter} implementation.
 */
public class BookWriterImpl implements BookWriter {
	
	private final Book book;
	private final SpreadsheetFile spreadsheetFile;
	private final BookConfig config;
	
	private int maxColumns;
	private Spreadsheet currentSheet;
	
	public BookWriterImpl(Book book, SpreadsheetFile spreadsheetFile) {
		this.book = book;
		this.spreadsheetFile = spreadsheetFile;
		this.config = book.getConfig();
	}

	@Override
	public void write() {
		writePages();
		writeConfigSheet();
	}
	
	private void writePages() {
		Collection<Page> pages = book.getPages().values();
		for (Page page : pages) {
			writePage(page);
		}
	}
	
	private void writePage(Page page) {
		maxColumns = 0;
		currentSheet = spreadsheetFile.createSpreadsheet(page.getName());
		ModelInst modelInst = page.getModelInst();
		ElementNodeInst rootElement = modelInst.getRootElement();
		ModelWriterVisitor writerVisitor = new ModelWriterVisitor();
		rootElement.accept(writerVisitor);
		formatColumns();
	}
	
	private void writeConfigSheet() {
		Spreadsheet configSheet = spreadsheetFile.createSpreadsheet(config.getConfigSheetName());
		configSheet.setCellValue(
				config.getAddr().toString(), 
				config.getConfigSheetBookAddrRow(), config.getConfigSheetBookAddrCol());
		spreadsheetFile.setSpreadsheetHidden(config.getConfigSheetName(), true);
	}
	
	private void processAttribNode(AttribNode node) {
		trackMaxColumns(node);

		outputNodeName(node, config.getAttribNodeStyle());
		outputOccurs(node);
		// start w/parent node when checking for repeating nodes,
		// as attributes themselves will never repeat
		outputOccurrenceMarkers(node); 
	}
	
	private void processCompositorNode(CompositorNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, config.getChoiceMarkerStyle());
		}
		
		outputNodeName(node, config.getCompositorNodeStyle());
		outputOccurs(node);
		outputOccurrenceMarkers(node);
	}
	
	private void processElementNode(ElementNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, config.getChoiceMarkerStyle());
		}
		
		CellStyle cellStyle = config.getDefaultNodeStyle();
		if (node.hasChild()) {
			cellStyle = config.getParentNodeStyle();
		}

		outputNodeName(node, cellStyle);
		outputOccurs(node);
		outputOccurrenceMarkers(node);
	}
	
	private void trackMaxColumns(TDCNode node) {
		maxColumns = Integer.max(maxColumns, node.getColOffset()+1);
	}
	
	private int getNodeRow(TDCNode node) {
		return config.getNodeRowStart() + node.getRowOffset();
	}
	
	private int getNodeCol(TDCNode node) {
		return 1 + node.getColOffset();
	}
	
	private int getDataCol(int dataColOffset) {
		return config.getDataColStart() + dataColOffset;
	}
	
	private void outputChoiceMarker(NonAttribNode node, CellStyle cellStyle) {
		currentSheet.setCellValue(">", getNodeRow(node), getNodeCol(node) - 1, cellStyle);
	}

	private void outputNodeName(TDCNode node, CellStyle cellStyle) {
		currentSheet.setCellValue(node.getDispName(), getNodeRow(node), getNodeCol(node), cellStyle);
	}
	
	private void outputOccurs(TDCNode node) {
		currentSheet.setCellValue(node.getDispOccurs(), getNodeRow(node), getDataCol(1)); // TODO remove hard-coding; only temporary
	}
	
	private void outputOccurrenceMarkers(NonAttribNode nonAttribNode) {
		// current node is an element or compositor; might repeat
		outputOccurrenceMarkersMayRepeat(
				(NonAttribNodeInst)nonAttribNode, 
				getNodeRow(nonAttribNode));
	}
	
	private void outputOccurrenceMarkers(AttribNode attribNode) {
		// node is an attribute; won't repeat; 
		// but parent may; call parent
		outputOccurrenceMarkersMayRepeat(
				(NonAttribNodeInst)attribNode.getParent(), 
				getNodeRow(attribNode));
	}
	
	private void outputOccurrenceMarkersMayRepeat(NonAttribNodeInst nonAttribNodeInst, int rowNum) {
		NonAttribNodeInst possibleRepeatingNode = nonAttribNodeInst;
		do {
			if (possibleRepeatingNode.getOccurCount() > 1) {
				currentSheet.setCellValue("" + possibleRepeatingNode.getOccurNum(), 
						rowNum, getNodeCol(possibleRepeatingNode) - 1, config.getOccurMarkerStyle());
			}
			possibleRepeatingNode = (NonAttribNodeInst)possibleRepeatingNode.getParent();
		} 
		while (possibleRepeatingNode != null);
	}

	private void formatColumns() {
		int allowedColumns = config.getTreeStructureColumnCount(); 
		if (allowedColumns < maxColumns) {
			throw new RuntimeException("TreeStructureColumnCount (" + allowedColumns + 
					") must be at least " + maxColumns + " to support this particular model");
		}
		for (int i = 1; i <= allowedColumns; i++) {
			currentSheet.setColumnWidth(i, config.getTreeStructureColumnWidth());
		}
	}
	
	class ModelWriterVisitor implements ModelVisitor {
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
