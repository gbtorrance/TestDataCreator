package org.tdc.book;

import java.util.Collection;
import java.util.List;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.PageColumnConfig;
import org.tdc.config.book.PageConfig;
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
	
	private static final int COL_OCCURS = 0;
	private static final int COL_CUSTOM_BASE = 1;
	
	private final Book book;
	private final SpreadsheetFile spreadsheetFile;
	private final BookConfig config;
	private final int nodeRowStart;
	private final int dataColStart;
	
	private int maxColumns;
	private Page currentPage;
	private PageConfig currentPageConfig;
	private Spreadsheet currentSheet;
	
	public BookWriterImpl(Book book, SpreadsheetFile spreadsheetFile) {
		this.book = book;
		this.spreadsheetFile = spreadsheetFile;
		this.config = book.getConfig();
		this.nodeRowStart = config.getHeaderRowCount() + 1;
		this.dataColStart = config.getTreeStructureColumnCount() + 1;
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
		currentPage = page;
		currentPageConfig = currentPage.getConfig();
		currentSheet = spreadsheetFile.createSpreadsheet(currentPage.getName());
		ModelInst modelInst = currentPage.getModelInst();
		ElementNodeInst rootElement = modelInst.getRootElement();
		ModelWriterVisitor writerVisitor = new ModelWriterVisitor();
		rootElement.accept(writerVisitor);
		formatColumns();
		writeHeaderLabels();
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
		outputOccurrenceMarkers(node); 
		outputCustomColumns(node);
	}
	
	private void processCompositorNode(CompositorNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, config.getChoiceMarkerStyle());
		}
		
		outputNodeName(node, config.getCompositorNodeStyle());
		outputOccurs(node);
		outputOccurrenceMarkers(node);
		outputCustomColumns(node);
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
		outputCustomColumns(node);
	}
	
	private void trackMaxColumns(TDCNode node) {
		maxColumns = Integer.max(maxColumns, node.getColOffset()+1);
	}
	
	private int getNodeRow(TDCNode node) {
		return nodeRowStart + node.getRowOffset();
	}
	
	private int getNodeCol(TDCNode node) {
		return 1 + node.getColOffset();
	}
	
	private int getDataCol(int dataColOffset) {
		return dataColStart + dataColOffset;
	}
	
	private void outputChoiceMarker(NonAttribNode node, CellStyle cellStyle) {
		currentSheet.setCellValue(">", getNodeRow(node), getNodeCol(node) - 1, cellStyle);
	}

	private void outputNodeName(TDCNode node, CellStyle cellStyle) {
		currentSheet.setCellValue(node.getDispName(), getNodeRow(node), getNodeCol(node), cellStyle);
	}
	
	private void outputOccurs(TDCNode node) {
		currentSheet.setCellValue(node.getDispOccurs(), getNodeRow(node), getDataCol(COL_OCCURS));
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

	private void outputCustomColumns(TDCNode node) {
		List<PageColumnConfig> columns = currentPageConfig.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			PageColumnConfig column = columns.get(i);
			CellStyle style = column.getStyle();
			String variableName = column.getReadFromVariable();
			String value = node.getVariable(variableName);
			currentSheet.setCellValue(value, getNodeRow(node), getDataCol(COL_CUSTOM_BASE) + i, style);
		}
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
		List<PageColumnConfig> columns = currentPageConfig.getColumns(); 
		for (int i = 0; i < columns.size(); i++) {
			PageColumnConfig column = columns.get(i);
			currentSheet.setColumnWidth(getDataCol(COL_CUSTOM_BASE) + i, column.getWidth());
		}
		currentSheet.freeze(getDataCol(COL_CUSTOM_BASE) + columns.size(), config.getHeaderRowCount()+1);
	}
	
	private void writeHeaderLabels() {
		int rowCount = config.getHeaderRowCount();
		CellStyle style = config.getDefaultHeaderStyle();
		List<PageColumnConfig> columns = currentPageConfig.getColumns(); 
		for (int row = 1; row <= rowCount; row++) {
			currentSheet.setCellValue(
					config.getTreeStructureHeaderLabel(row), row, 1, style);
			currentSheet.setCellValue(
					config.getOccursHeaderLabel(row), row, getDataCol(COL_OCCURS), style);
			for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
				PageColumnConfig column = columns.get(colIndex);
				currentSheet.setCellValue(
						column.getHeaderLabel(row), row, getDataCol(COL_CUSTOM_BASE) + colIndex, style);
			}
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
