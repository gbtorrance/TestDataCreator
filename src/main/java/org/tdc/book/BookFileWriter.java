package org.tdc.book;

import java.util.Collection;
import java.util.List;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.DocIDRowConfig;
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
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.NonAttribNodeInst;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.util.Util;

/**
 * Implements functionality for writing a new {@link Book} spreadsheet file confirming to a {@link BookConfig}. 
 */
public class BookFileWriter {
	
	private static final int COL_CUSTOM_BASE = 0;
	
	private final BookConfig config;
	private final SpreadsheetFile spreadsheetFile;
	private final ModelInstFactory modelInstFactory;
	private final int docIDRowStart;
	private final int dataColStart;
	
	private int maxColumns;
	private PageConfig currentPageConfig;
	private Spreadsheet currentSheet;
	private int headerRowStart;
	private int nodeRowStart;
	
	public BookFileWriter(BookConfig config, SpreadsheetFile spreadsheetFile, ModelInstFactory modelInstFactory) {
		this.config = config;
		this.spreadsheetFile = spreadsheetFile;
		this.modelInstFactory = modelInstFactory;
		this.docIDRowStart = 1;
		this.dataColStart = config.getTreeStructureColumnCount() + 1;
	}

	public void write() {
		writePages();
		writeConfigSheet();
	}
	
	private void writePages() {
		Collection<PageConfig> pageConfigs = config.getPageConfigs().values();
		for (PageConfig pageConfig : pageConfigs) {
			writePage(pageConfig);
		}
	}
	
	private void writePage(PageConfig pageConfig) {
		maxColumns = 0;
		currentPageConfig = pageConfig;
		currentSheet = spreadsheetFile.createSpreadsheet(currentPageConfig.getPageName());
		headerRowStart = docIDRowStart + currentPageConfig.getDocIDRows().size();
		nodeRowStart = headerRowStart + config.getHeaderRowCount();
		ModelInst modelInst = modelInstFactory.getModelInst(currentPageConfig.getModelConfig());
		ElementNodeInst rootElement = modelInst.getRootElement();
		ModelWriterVisitor writerVisitor = new ModelWriterVisitor();
		rootElement.accept(writerVisitor);
		formatColumns();
		writeDocIDRowLabels();
		writeHeaderLabels();
	}
	
	private void writeConfigSheet() {
		Spreadsheet configSheet = spreadsheetFile.createSpreadsheet(
				BookUtil.CONFIG_SHEET_NAME);
		configSheet.setCellValue(
				config.getAddr().toString(), 
				BookUtil.CONFIG_SHEET_BOOK_ADDR_ROW, BookUtil.CONFIG_SHEET_BOOK_ADDR_COL);
		spreadsheetFile.setSpreadsheetHidden(BookUtil.CONFIG_SHEET_NAME, true);
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
		currentSheet.freeze(getDataCol(COL_CUSTOM_BASE) + columns.size(), nodeRowStart);
	}
	
	private void writeDocIDRowLabels() {
		List<DocIDRowConfig> docIDRows = currentPageConfig.getDocIDRows();
		CellStyle style = config.getDefaultHeaderStyle();
		int rowCount = docIDRows.size();
		for (int row = 0; row < rowCount; row++) {
			DocIDRowConfig docIDRow = docIDRows.get(row);
			currentSheet.setCellValue(docIDRow.getLabel(), docIDRowStart + row, 1, style);
		}
	}
	
	private void writeHeaderLabels() {
		int rowCount = config.getHeaderRowCount();
		CellStyle style = config.getDefaultHeaderStyle();
		List<PageColumnConfig> columns = currentPageConfig.getColumns(); 
		for (int row = 0; row < rowCount; row++) {
			currentSheet.setCellValue(
					config.getTreeStructureHeaderLabel(row+1), headerRowStart + row, 1, style);
			for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
				PageColumnConfig column = columns.get(colIndex);
				currentSheet.setCellValue(
						column.getHeaderLabel(row+1), headerRowStart + row, getDataCol(COL_CUSTOM_BASE) + colIndex, style);
			}
		}
	}

	private void processAttribNode(AttribNode node) {
		trackMaxColumns(node);

		outputNodeName(node, config.getAttribNodeStyle());
		outputOccurrenceMarkers(node); 
		outputCustomColumns(node);
	}
	
	private void processCompositorNode(CompositorNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, config.getChoiceMarkerStyle());
		}
		
		outputNodeName(node, config.getCompositorNodeStyle());
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
			String value = "";
			String variableName = column.getReadFromVariable();
			String propertyName = column.getReadFromProperty();
			if (variableName != null) {
				value = node.getVariable(variableName);
			}
			if (propertyName != null) {
				value = Util.getStringValueFromProperty(node, propertyName, "");
			}
			currentSheet.setCellValue(value, getNodeRow(node), getDataCol(COL_CUSTOM_BASE) + i, style);
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
