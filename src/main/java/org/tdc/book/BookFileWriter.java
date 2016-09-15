package org.tdc.book;

import java.util.Collection;
import java.util.List;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.DocIDRowConfig;
import org.tdc.config.book.PageConfig;
import org.tdc.config.book.PageNodeDetailColumnConfig;
import org.tdc.dom.TestDocCopier;
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
	
	private final BookConfig config;
	private final SpreadsheetFile spreadsheetFile;
	private final ModelInstFactory modelInstFactory;
	
	private int maxColumns;
	private PageConfig currentPageConfig;
	private ModelInst currentModelInst;
	private Spreadsheet currentSheet;
	
	public BookFileWriter(BookConfig config, SpreadsheetFile spreadsheetFile, ModelInstFactory modelInstFactory) {
		this.config = config;
		this.spreadsheetFile = spreadsheetFile;
		this.modelInstFactory = modelInstFactory;
	}

	public void write() {
		writePages(null);
		writeConfigSheet();
	}
	
	public void writeWithTestDataFromExistingBook(Book basedOnBook) {
		writePages(basedOnBook);
		writeConfigSheet();
	}
	
	public void deleteDefaultSheet() {
		// when using a template file, a default sheet will always exist 
		// (because Excel won't save a file without at least one sheet);
		String defaultSheetName = "Sheet1";
		if (spreadsheetFile.getSpreadsheet(defaultSheetName) != null) {
			spreadsheetFile.deleteSpreadsheet(defaultSheetName);
		}
	}
	
	private void writePages(Book basedOnBook) {
		Collection<PageConfig> pageConfigs = config.getPageConfigs().values();
		for (PageConfig pageConfig : pageConfigs) {
			writePage(pageConfig, basedOnBook);
		}
	}
	
	private void writePage(PageConfig pageConfig, Book basedOnBook) {
		maxColumns = 0;
		currentPageConfig = pageConfig;
		currentModelInst = modelInstFactory.getModelInst(currentPageConfig.getModelConfig());
		currentSheet = spreadsheetFile.createSpreadsheet(currentPageConfig.getPageName());
		writeModelStructure();
		formatColumns();
		writeDocIDRowLabels();
		writeHeaderLabels();
		writeTestsFromExistingBook(basedOnBook);
	}

	private void writeConfigSheet() {
		Spreadsheet configSheet = spreadsheetFile.createSpreadsheet(
				BookUtil.CONFIG_SHEET_NAME);
		configSheet.setCellValue(
				config.getAddr().toString(), 
				BookUtil.CONFIG_SHEET_BOOK_ADDR_ROW, BookUtil.CONFIG_SHEET_BOOK_ADDR_COL);
		spreadsheetFile.setSpreadsheetHidden(BookUtil.CONFIG_SHEET_NAME, true);
	}
	
	private void writeModelStructure() {
		ElementNodeInst rootElement = currentModelInst.getRootElement();
		ModelWriterVisitor writerVisitor = new ModelWriterVisitor();
		rootElement.accept(writerVisitor);
	}
	
	private void formatColumns() {
		int allowedColumns = config.getNodeColumnCount(); 
		if (allowedColumns < maxColumns) {
			throw new RuntimeException("TreeStructureColumnCount (" + allowedColumns + 
					") must be at least " + maxColumns + " to support this particular model");
		}
		for (int i = 1; i <= allowedColumns; i++) {
			currentSheet.setColumnWidth(i, config.getNodeColumnWidth());
		}
		List<PageNodeDetailColumnConfig> columns = currentPageConfig.getNodeDetailColumnConfigs(); 
		for (int i = 0; i < columns.size(); i++) {
			PageNodeDetailColumnConfig column = columns.get(i);
			currentSheet.setColumnWidth(column.getColNum(), column.getWidth());
		}
		currentSheet.freeze(currentPageConfig.getTestDocColStart(), currentPageConfig.getNodeRowStart());
	}
	
	private void writeDocIDRowLabels() {
		List<DocIDRowConfig> docIDRows = currentPageConfig.getDocIDRowConfigs();
		CellStyle style = config.getDefaultHeaderStyle();
		int rowCount = docIDRows.size();
		for (int row = 0; row < rowCount; row++) {
			DocIDRowConfig docIDRow = docIDRows.get(row);
			currentSheet.setCellValue(docIDRow.getLabel(), docIDRow.getRowNum(), 
					currentPageConfig.getDocIDRowLabelCol(), style);
		}
	}
	
	private void writeHeaderLabels() {
		int rowCount = config.getHeaderRowCount();
		CellStyle style = config.getDefaultHeaderStyle();
		List<PageNodeDetailColumnConfig> columns = currentPageConfig.getNodeDetailColumnConfigs(); 
		for (int row = 0; row < rowCount; row++) {
			currentSheet.setCellValue(
					config.getNodeHeaderLabel(row+1), currentPageConfig.getHeaderRowStart() + row, 1, style);
			for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
				PageNodeDetailColumnConfig column = columns.get(colIndex);
				currentSheet.setCellValue(
						column.getHeaderLabel(row+1), 
						currentPageConfig.getHeaderRowStart() + row, column.getColNum(), style);
			}
		}
	}

	private void writeTestsFromExistingBook(Book basedOnBook) {
		if (basedOnBook != null) {
			String pageName = currentPageConfig.getPageName();
			Page basedOnPage = basedOnBook.getPages().get(pageName);
			if (basedOnPage != null) {
				List<TestDoc> basedOnTestDocs = basedOnPage.getTestDocs();
				writeTestsFromExistingTestDocs(basedOnTestDocs);
			}
		}
	}
	
	private void writeTestsFromExistingTestDocs(List<TestDoc> basedOnTestDocs) {
		int colNumOnCurrentPage = currentPageConfig.getTestDocColStart();
		for (TestDoc basedOnTestDoc : basedOnTestDocs) {
			writeTestFromExistingTestDoc(basedOnTestDoc, colNumOnCurrentPage++);
		}
	}

	private void writeTestFromExistingTestDoc(TestDoc basedOnTestDoc, int colNumOnCurrentPage) {
		TestDocCopier copier = new TestDocCopier(currentModelInst, currentPageConfig, currentSheet);
		copier.copyTestDoc(basedOnTestDoc, colNumOnCurrentPage);
	}

	private void trackMaxColumns(TDCNode node) {
		maxColumns = Integer.max(maxColumns, node.getColOffset()+1);
	}
	
	private int getNodeRow(TDCNode node) {
		return currentPageConfig.getNodeRowStart() + node.getRowOffset();
	}
	
	private int getNodeCol(TDCNode node) {
		return currentPageConfig.getNodeColStart() + node.getColOffset();
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
		List<PageNodeDetailColumnConfig> columns = currentPageConfig.getNodeDetailColumnConfigs();
		for (int i = 0; i < columns.size(); i++) {
			PageNodeDetailColumnConfig column = columns.get(i);
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
			currentSheet.setCellValue(value, getNodeRow(node), column.getColNum(), style);
		}
	}
	
	class ModelWriterVisitor implements ModelVisitor {
		@Override
		public void visit(AttribNode node) {
			trackMaxColumns(node);
			outputNodeName(node, config.getAttribNodeStyle());
			outputOccurrenceMarkers(node); 
			outputCustomColumns(node);
		}

		@Override
		public void visit(CompositorNode node) {
			trackMaxColumns(node);
			if (node.isChildOfChoice()) {
				outputChoiceMarker(node, config.getChoiceMarkerStyle());
			}
			outputNodeName(node, config.getCompositorNodeStyle());
			outputOccurrenceMarkers(node);
			outputCustomColumns(node);
		}

		@Override
		public void visit(ElementNode node) {
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
	}
}
