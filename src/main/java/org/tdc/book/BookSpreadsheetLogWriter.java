package org.tdc.book;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.TaskConfig;
import org.tdc.result.Message;
import org.tdc.result.Result;
import org.tdc.result.Results;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Writes all {@link Result} / {@link Message} information to a Log 
 * {@link Spreadsheet} in the provided {@link SpreadsheetFile}. 
 */
public class BookSpreadsheetLogWriter {
	private static final Logger log = LoggerFactory.getLogger(BookSpreadsheetLogWriter.class);
	private static final String LOG_SPREADSHEET_NAME = "Log"; // TODO configuration
	
	private final Book book;
	private final SpreadsheetFile spreadsheetFile;
	private final CellStyle boldStyle;
	private final CellStyle defaulStyle;
	
	private Spreadsheet logSheet;
	private int rowNum;
	private int colNum;
	
	
	public BookSpreadsheetLogWriter(Book book, SpreadsheetFile spreadsheetFile) {
		this.book = book;
		this.spreadsheetFile = spreadsheetFile;
		this.boldStyle = book.getConfig().getNodeHeaderStyle(); // TODO log-specific style configuration
		this.defaulStyle = book.getConfig().getDefaultStyle(); // TODO log-specific style configuration 
	}
	
	public void writeLog() {
		rowNum = 1;
		colNum = 1;
		initLogSpreadsheet();
		writeBook();
	}

	private void initLogSpreadsheet() {
		logSheet = spreadsheetFile.getSpreadsheet(LOG_SPREADSHEET_NAME);
		if (logSheet != null) {
			spreadsheetFile.deleteSpreadsheet(LOG_SPREADSHEET_NAME);
		}
		logSheet = spreadsheetFile.createSpreadsheet(LOG_SPREADSHEET_NAME);
	}

	private void writeBook() {
		List<TestSet> testSets = book.getTestSets();
		for (TestSet testSet : testSets) {
			writeTestSet(testSet);
		}
	}

	private void writeTestSet(TestSet testSet) {
		String setName = testSet.getSetName().equals("") ? "{default}" : testSet.getSetName(); 
		String setLabel = "Test Set: " + setName;
		logSheet.setCellValue(setLabel, rowNum, colNum, boldStyle);
		rowNum++;
		colNum++;
		List<TestCase> testCases = testSet.getTestCases();
		for (TestCase testCase : testCases) {
			writeTestCase(testCase);
		}
		writeResults(testSet.getResults());
		colNum--;
	}

	private void writeTestCase(TestCase testCase) {
		String setLabel = testCase.getSetName().equals("") ? 
				"" : " [Set Name: " + testCase.getSetName() + "]";
		String caseLabel = "Test Case: " + testCase.getCaseNum() + setLabel;
		logSheet.setCellValue(caseLabel, rowNum, colNum, boldStyle);
		rowNum++;
		colNum++;
		List<TestDoc> testDocs = testCase.getTestDocs();
		for (TestDoc testDoc : testDocs) {
			writeTestDoc(testDoc);
		}
		writeResults(testCase.getResults());
		colNum--;
	}

	private void writeTestDoc(TestDoc testDoc) {
		String docLabel = "Document: " + 
				testDoc.getPageConfig().getPageName() + 
				" [Column: " + testDoc.getColLetter() + ", Type: " + 
				testDoc.getPageConfig().getDocTypeConfig().getDocTypeName() + "]";
		logSheet.setCellValue(docLabel, rowNum, colNum, boldStyle);
		rowNum++;
		colNum++;
		writeResults(testDoc.getResults());
		colNum--;
	}

	private void writeResults(Results results) {
		results.getTestLoadResult().ifPresent(r -> writePhase(r, "Load"));
		results.getSchemaValidateResult().ifPresent(r -> writePhase(r, "Schema"));
		for (TaskConfig task : book.getConfig().getTaskConfigs()) {
			results.getTaskResult(task.getTaskID()).ifPresent(r -> writePhase(r, "Task: " + task.getTaskID()));
		}
	}

	private void writePhase(Result result, String phaseName) {
		for (Message message : result.getMessages()) {
			logSheet.setCellValue(phaseName, rowNum, colNum, defaulStyle);
			colNum++;
			writeMessage(message);
			colNum--;
		}
	}

	private void writeMessage(Message message) {
		logSheet.setCellValue(message.getMessage(), rowNum, colNum + 1, defaulStyle);
		rowNum++;
		// TODO add linking to error location
		// TODO add detailed message information
		// TODO add message "type"
		// TODO add counts
		// TODO possibly add summary info (with linking to details)
	}
}
