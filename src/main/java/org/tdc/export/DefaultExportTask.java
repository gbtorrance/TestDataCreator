package org.tdc.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.book.Book;
import org.tdc.book.TestCase;
import org.tdc.book.TestDoc;
import org.tdc.book.TestSet;
import org.tdc.config.book.TaskConfig;
import org.tdc.dom.TestDocXMLGenerator;
import org.tdc.result.Message;
import org.tdc.result.Results;
import org.tdc.result.TaskResult;
import org.tdc.task.Task;
import org.tdc.util.Util;

/**
 * Default {@link Task} for exporting the contents of a {@link Book} 
 * as a series of XML files; one for each {@link TestDoc}. 
 */
public class DefaultExportTask implements Task {
	private static final Logger log = LoggerFactory.getLogger(DefaultExportTask.class);

	public final DefaultExportTaskConfig config;
	public final Book book;
	public final TestDocXMLGenerator xmlGenerator;

	public DefaultExportTask(DefaultExportTaskConfig config, Book book, TestDocXMLGenerator xmlGenerator) {
		this.config = config;
		this.book = book;
		this.xmlGenerator = xmlGenerator;
	}
	
	@Override
	public DefaultExportTaskConfig getConfig() {
		return config;
	}
	
	@Override
	public void process() {
		export(book);
	}
	
	public void export(Book book) {
		Path batchDir = createBatchDir();
		// if only one set, and that set is the "default" set, 
		// don't create a sub directory for it
		List<TestSet> testSets = book.getTestSets();
		int seq = 1;
		for (TestSet testSet : testSets) {
			if (testSets.size() == 1 && testSet.getSetName().equals("")) {
				// if we only have a "default" set, won't need an index
				exportTestSet(testSet, batchDir, -1);
			}
			else {
				// if we have multiple sets, use index = 0 for the default,
				// and an incrementing sequence for the rest
				exportTestSet(testSet, batchDir, 
						testSet.getSetName().equals("") ? 0 : seq++);
			}
		}
	}
	
	private void exportTestSet(TestSet testSet, Path batchDir, int seq) {
		log.debug("Exporting TestSet: {}", testSet.getSetName());
		boolean success = false;
		try {
			Path setDir = createSetDir(batchDir, seq, testSet.getSetName());
			List<TestCase> testCases = testSet.getTestCases();
			for (TestCase testCase : testCases) {
				exportTestCase(testCase, setDir);
			}
			success = true;
		}
		finally {
			logResult(testSet.getResults(), "Test Set", success);
		}
	}

	private void exportTestCase(TestCase testCase, Path setDir) {
		log.debug("Exporting TestCase: {}", testCase.getCaseNum());
		boolean success = false;
		try {
			Path caseDir = createCaseDir(setDir, testCase.getCaseNum());
			List<TestDoc> testDocs = testCase.getTestDocs();
			int seq = 1;
			for (TestDoc testDoc : testDocs) {
				exportTestDoc(testDoc, caseDir, seq++);
			}
			success = true;
		}
		finally {
			logResult(testCase.getResults(), "Test Case", success);
		}
	}

	private void exportTestDoc(TestDoc testDoc, Path caseDir, int seq) {
		log.debug("Exporting TestCase num {}, TestSet name '{}', column {}", 
				testDoc.getCaseNum(), testDoc.getSetName(), testDoc.getColNum());
		boolean success = false;
		try {
			String docTypeName = testDoc.getPageConfig().getDocTypeConfig().getDocTypeName();
			String fileName =  seq + "_" + Util.legalizeName(docTypeName) + ".xml";
			Path filePath = caseDir.resolve(fileName);
			xmlGenerator.setDocument(testDoc.getDOMDocument());
			xmlGenerator.generateXML(filePath);
			success = true;
		}
		finally {
			logResult(testDoc.getResults(), "Test Doc", success);
		}
	}

	private void logResult(Results results, String type, boolean success) {
		String taskID = config.getTaskID();
		TaskResult taskResult = new TaskResult(taskID);
		String msg = type + (success ? " exported successfully" : " export failed");
		Message message = new Message.Builder("info", msg).build();
		taskResult.addMessage(message);
		results.setTaskResult(taskID, taskResult);
	}

	private Path createBatchDir() {
		String bookName = book.getConfig().getBookName();
		String batchDirName = Util.legalizeName(bookName) + "_" + 
				LocalDateTime.now().format(Util.EXPORT_DATE_TIME_FORMATTER);
		Path batchDir = config.getExportRoot().resolve(batchDirName);
		createDirectory(batchDir);
		return batchDir;
	}
	
	private Path createSetDir(Path batchDir, int index, String setName) {
		Path setDir = batchDir;
		if (index != -1) {
			// prefix dir with an index value to ensure that there will
			// never be a clash if two 'legalized names' end up 
			// being the same
			String suffix = setName.equals("") ? "DefaultSet" : Util.legalizeName(setName); 
			setDir = setDir.resolve(index + "_" + suffix);
			createDirectory(setDir);
		}
		return setDir;
	}
	
	private Path createCaseDir(Path setDir, int caseNum) {
		String caseNumStr = 
				caseNum < 1000 ? 
				Integer.toString(1000 + caseNum).substring(1) :
				Integer.toString(caseNum);
		Path caseDir = setDir.resolve("Case_" + caseNumStr);
		createDirectory(caseDir);
		return caseDir;
	}

	private void createDirectory(Path dirPath) {
		try {
			Files.createDirectory(dirPath);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to create dir: " + dirPath.toString(), e);
		}
	}
	
	public static Task build(
			TaskConfig taskConfig, Book book, Map<String, String> taskParams) {
		
		if (!(taskConfig instanceof DefaultExportTaskConfig)) {
			throw new IllegalStateException("TaskConfig '" + taskConfig.getTaskID() + 
					"' must be an instance of " + DefaultExportTaskConfig.class.getName());
		}
		TestDocXMLGenerator xmlGenerator = new TestDocXMLGenerator();
		return new DefaultExportTask((DefaultExportTaskConfig)taskConfig, book, xmlGenerator);
	}
}
