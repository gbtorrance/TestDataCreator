package org.tdc.book;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.result.Result;
import org.tdc.schemavalidate.SchemaValidator;
import org.tdc.schemavalidate.SchemaValidatorFactory;

/**
 * Schema validates all {@link TestDoc}s in a particular {@link Book}.
 */
public class BookSchemaValidator {
	
	private static final Logger log = LoggerFactory.getLogger(BookSchemaValidator.class);
	
	private final Book book;
	private final SchemaValidatorFactory schemaValidatorFactory;
	
	public BookSchemaValidator(Book book, SchemaValidatorFactory schemaValidatorFactory) {
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
		Optional<Result> schemaValidateResult = testDoc.getResults().getSchemaValidateResult();
		if (schemaValidateResult.isPresent()) {
			log.debug("Schema validation already complete for this TestDoc; skipping!");
		}
		else {
			ModelConfig modelConfig = testDoc.getPageConfig().getModelConfig(); 
			SchemaValidator schemaValidator = schemaValidatorFactory.getSchemaValidator(modelConfig);
			testDoc.getResults().setSchemaValidateResult(new Result());
			schemaValidator.validate(testDoc);
		}
	}
}
