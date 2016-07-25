package org.tdc.book;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.dom.TestDocXMLGenerator;

/**
 * Generates a test data file for every every {@link TestDoc} in the {@link Book}.
 */
public class BookTestDataGenerator {

	private static final Logger log = LoggerFactory.getLogger(BookTestDataGenerator.class);
	
	private final TestDocXMLGenerator testDocXMLGenerator;
	private final Book book;
	
	public BookTestDataGenerator(Book book) {
		this.testDocXMLGenerator = new TestDocXMLGenerator();
		this.book = book;
	}

	public void generateTestData() {
		List<TestSet> testSets = book.getTestSets();
		for (TestSet testSet : testSets) {
			processTestSet(testSet);
		}
	}

	private void processTestSet(TestSet testSet) {
		log.debug("Generating test data for TestSet: {}", testSet.getSetName());
		List<TestCase> testCases = testSet.getTestCases();
		for (TestCase testCase : testCases) {
			processTestCase(testCase);
		}
	}

	private void processTestCase(TestCase testCase) {
		log.debug("Generating test data for TestCase: {}", testCase.getCaseNum());
		List<TestDoc> testDocs = testCase.getTestDocs();
		for (TestDoc testDoc : testDocs) {
			processTestDoc(testDoc);
		}
	}

	private void processTestDoc(TestDoc testDoc) {
		log.debug("Generating test data for TestCase num {}, TestSet name '{}', column {}", 
				testDoc.getCaseNum(), testDoc.getSetName(), testDoc.getColNum());
		
		testDocXMLGenerator.setDocument(testDoc.getDOMDocument());
		Path path = Paths.get("testfiles/Temp/Output_" + 
				testDoc.getSetName() + "_" + 
				testDoc.getCaseNum() + "_" + 
				testDoc.getColNum() + ".xml");
		testDocXMLGenerator.generateXML(path);
	}
}
