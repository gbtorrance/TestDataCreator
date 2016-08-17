package org.tdc.book;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.PageConfig;
import org.tdc.config.model.ModelConfig;
import org.tdc.dom.TestDocDOMBuilder;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.ModelVisitor;
import org.tdc.model.TDCNode;
import org.tdc.modelinst.ModelInst;
import org.tdc.result.Result;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.w3c.dom.Document;

/**
 * Loads a DOM {@link Document} for every {@link TestDoc} in the {@link Book}.
 * 
 * <p>Note: Apache POI spreadsheet processing is NOT thread safe; 
 * as such, the processing for a {@link SpreadsheetFile} must be done in a single thread.
 */
public class BookTestDataLoader {

	private static final Logger log = LoggerFactory.getLogger(BookTestDataLoader.class);
	
	private final TestDocDOMBuilder testDocDOMBuilder;
	private final Book book;
	private final SpreadsheetFile spreadsheetFile;
	
	public BookTestDataLoader(Book book, SpreadsheetFile spreadsheetFile) {
		this.testDocDOMBuilder = new TestDocDOMBuilder();
		this.book = book;
		this.spreadsheetFile = spreadsheetFile;
	}

	public void loadTestData() {
		Collection<Page> pages = book.getPages().values();
		for (Page page : pages) {
			loadPageTestData(page);
		}
	}
	
	private void loadPageTestData(Page page) {
		log.debug("About to load TestDoc DOM documents for Page: " + page.getName());
		
		ModelInst modelInst = page.getModelInst();
		Spreadsheet sheet = spreadsheetFile.getSpreadsheet(page.getName());
		ModelConfig modelConfig = modelInst.getModelConfig();
		String namespace = modelConfig.getSchemaRootElementNamespace();
		PageConfig pageConfig = page.getConfig();
		int nodeRowStart = pageConfig.getNodeRowStart();
		testDocDOMBuilder.setModelInst(modelInst);
		testDocDOMBuilder.setSpreadsheet(sheet);
		testDocDOMBuilder.setNamespace(namespace);
		testDocDOMBuilder.setNodeRowStart(nodeRowStart);
		
		PageStructureVerifier verifier = new PageStructureVerifier(modelInst, pageConfig, sheet);
		verifier.verifyStructure();
		
		List<TestDoc> testDocs = page.getTestDocs();
		for (TestDoc testDoc : testDocs) {
			loadTestDocData(testDoc);
		}
	}

	private void loadTestDocData(TestDoc testDoc) {
		log.debug("About to load TestDoc DOM documents for TestCase num {}, TestSet name '{}', column {}", 
				testDoc.getCaseNum(), testDoc.getSetName(), testDoc.getColNum());
		
		Document domDocument = testDoc.getDOMDocument();
		if (domDocument == null) {
			domDocument = buildDOMDocument(testDoc);
			testDoc.setDOMDocument(domDocument);
			log.debug("TestDoc DOM document built!");
		}
		else {
			log.debug("TestDoc DOM document already found; skip and continue!");
		}
	}

	private Document buildDOMDocument(TestDoc testDoc) {
		Result testLoadResult = new Result();
		testDoc.getResults().setTestLoadResult(testLoadResult);
		testDocDOMBuilder.setResult(testLoadResult);
		testDocDOMBuilder.setTestDocColNumAndLetter(testDoc.getColNum(), testDoc.getColLetter());
		return testDocDOMBuilder.build();
	}

	private static class PageStructureVerifier implements ModelVisitor {
		private final ModelInst modelInst;
		private final PageConfig pageConfig;
		private final Spreadsheet sheet;
		
		public PageStructureVerifier(ModelInst modelInst, PageConfig pageConfig, Spreadsheet sheet) {
			this.modelInst = modelInst;
			this.pageConfig = pageConfig;
			this.sheet = sheet;
		}
		
		public void verifyStructure() {
			modelInst.getRootElement().accept(this);
		}
		
		@Override
		public void visit(AttribNode attribNode) {
			validateNodeName(attribNode);
		}

		@Override
		public void visit(CompositorNode compositorNode) {
			validateNodeName(compositorNode);
		}

		@Override
		public void visit(ElementNode elementNode) {
			validateNodeName(elementNode);
		}
		
		private void validateNodeName(TDCNode node) {
			int row = pageConfig.getNodeRowStart() +  node.getRowOffset();
			int col = pageConfig.getNodeColStart() + node.getColOffset();
			String expectedValue = node.getDispName();
			String actualValue = sheet.getCellValue(row, col);
			if (!actualValue.equals(expectedValue)) {
				String msg = "Page structure invalid for page '" + pageConfig.getPageName() + 
						"'; actual value '" + actualValue + "' at row " + row + " col " + col + 
						"; expected value '" + expectedValue + "'"; 
				throw new RuntimeException(msg);
			}
		}
	}
}
