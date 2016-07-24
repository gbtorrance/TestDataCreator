package org.tdc.book;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.schemavalidate.SchemaValidator;
import org.tdc.schemavalidate.SchemaValidatorFactory;

/**
 * Validates all {@link TestDoc}s in a particular {@link Book}.
 */
public class BookValidator {
	
	private static final Logger log = LoggerFactory.getLogger(BookValidator.class);
	
	private final Book book;
	private final SchemaValidatorFactory schemaValidatorFactory;
	
	public BookValidator(Book book, SchemaValidatorFactory schemaValidatorFactory) {
		this.book = book;
		this.schemaValidatorFactory = schemaValidatorFactory;
	}
	
	public void validate() {
		List<TestSet> testSets = book.getTestSets();
		for (TestSet testSet : testSets) {
			validateTestSet(testSet);
		}
	}

	private void validateTestSet(TestSet testSet) {
		log.debug("Schema validating TestSet: {}", testSet.getSetName());
		List<TestCase> testCases = testSet.getTestCases();
		for (TestCase testCase : testCases) {
			validateTestCase(testCase);
		}
	}

	private void validateTestCase(TestCase testCase) {
		log.debug("Schema validating TestCase: {}", testCase.getCaseNum());
		List<TestDoc> testDocs = testCase.getTestDocs();
		for (TestDoc testDoc : testDocs) {
			validateTestDoc(testDoc);
		}
	}

	private void validateTestDoc(TestDoc testDoc) {
		log.debug("Schema validating TestCase num {}, TestSet name '{}', column {}", 
				testDoc.getCaseNum(), testDoc.getSetName(), testDoc.getColNum());
		ModelConfig modelConfig = testDoc.getPageConfig().getModelConfig(); 
		SchemaValidator schemaValidator = schemaValidatorFactory.getSchemaValidator(modelConfig);
		schemaValidator.validate(testDoc);
	}
}
