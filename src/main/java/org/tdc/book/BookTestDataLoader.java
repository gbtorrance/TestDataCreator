package org.tdc.book;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.PageConfig;
import org.tdc.config.model.ModelConfig;
import org.tdc.modelinst.ModelInst;
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
		testDocDOMBuilder.setTestDocColNumAndLetter(testDoc.getColNum(), testDoc.getColLetter());
		testDocDOMBuilder.setMessages(testDoc.getMessages());
		return testDocDOMBuilder.build();
	}
}
