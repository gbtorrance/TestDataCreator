package org.tdc.extension.mef.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

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
 * MeF (Modernized eFile) {@link Task} for exporting MeF "Submissions". 
 * Submissions are represented as {@link TestCase}s in a {@link Book}.
 * Each of these Submissions will be exported to a directory 
 * (named with the Submission ID) and will contain a set of one or more
 * XML files (corresponding to the {@link TestDoc}s in the {@link TestCase}).
 */
public class MeFExportTask implements Task {
	private static final Logger log = LoggerFactory.getLogger(MeFExportTask.class);

	public final MeFExportTaskConfig config;
	public final Book book;
	public final TestDocXMLGenerator xmlGenerator;

	public MeFExportTask(MeFExportTaskConfig config, Book book, TestDocXMLGenerator xmlGenerator) {
		this.config = config;
		this.book = book;
		this.xmlGenerator = xmlGenerator;
	}
	
	@Override
	public MeFExportTaskConfig getConfig() {
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
			StringBuilder submissionIDs = new StringBuilder();
			Path setDir = createSetDir(batchDir, seq, testSet.getSetName());
			List<TestCase> testCases = testSet.getTestCases();
			for (TestCase testCase : testCases) {
				String submissionID = exportTestCase(testCase, setDir);
				submissionIDs.append(submissionID).append(System.lineSeparator());
			}
			writeSubmissionIDsToFile(setDir, submissionIDs.toString());
			success = true;
		}
		finally {
			logResult(testSet.getResults(), "Test Set", success);
		}
	}

	private void writeSubmissionIDsToFile(Path setDir, String submissionIDs) {
		try {
			Files.write(setDir.resolve("SubmissionIDs.txt"), 
					submissionIDs.getBytes(), StandardOpenOption.CREATE_NEW);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to create 'SubmissionIDs.txt' file in: " + 
					setDir.toString(), e);
		}
	}

	private String exportTestCase(TestCase testCase, Path setDir) {
		log.debug("Exporting TestCase: {}", testCase.getCaseNum());
		boolean success = false;
		try {
			String submissionID = getSubmissionID(testCase);
			Path caseDir = createCaseDir(setDir, submissionID);
			List<TestDoc> testDocs = testCase.getTestDocs();
			for (TestDoc testDoc : testDocs) {
				exportTestDoc(testDoc, caseDir, submissionID);
			}
			success = true;
			return submissionID;
		}
		finally {
			logResult(testCase.getResults(), "Test Case", success);
		}
	}

	private void exportTestDoc(TestDoc testDoc, Path caseDir, String submissionID) {
		log.debug("Exporting TestCase num {}, TestSet name '{}', column {}", 
				testDoc.getCaseNum(), testDoc.getSetName(), testDoc.getColNum());
		boolean success = false;
		try {
			xmlGenerator.setDocument(testDoc.getDOMDocument());
			String docType = testDoc.getPageConfig().getDocTypeConfig().getDocTypeName();
			if (docType.equals(config.getStateDocTypeName())) {
				Path filePath = caseDir.resolve("xml").resolve(submissionID + ".xml");
				exportTestDocAndCheckExistence(docType, filePath);
			}
			else if (docType.equals(config.getManifestDocTypeName())) {
				Path filePath = caseDir.resolve("manifest").resolve("manifest.xml");
				exportTestDocAndCheckExistence(docType, filePath);
			}
			else if (docType.equals(config.getFederalDocTypeName())) {
				Path filePath = caseDir.resolve("irs").resolve("xml").resolve("federal.xml");
				exportTestDocAndCheckExistence(docType, filePath);
			}
			else {
				throw new RuntimeException("DocType '" + docType + 
						"' is an unknown for MeF; unable to export");
			}
			success = true;
		}
		finally {
			logResult(testDoc.getResults(), "Test Doc", success);
		}
	}

	private void exportTestDocAndCheckExistence(String docType, Path filePath) {
		if (Files.exists(filePath)) {
			throw new RuntimeException("File '" + filePath.toString() + 
					"' of DocType '" + docType + 
					"' already exists; only one document of each type is allowed");
		}
		xmlGenerator.generateXML(filePath);
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
	
	private String getSubmissionID(TestCase testCase) {
		String subIDVar = config.getSubmissionIDVariable();
		String subID = testCase.getCaseVariables().getOrDefault(subIDVar, "").trim();
		if (subID.length() == 0) {
			throw new RuntimeException("A Submission ID must exist for Test Case " + 
					testCase.getCaseNum() + " in Test Set '" + testCase.getSetName() + "'");
		}
		return subID;
	}

	private Path createCaseDir(Path setDir, String submissionID) {
		Path caseDir = setDir.resolve(submissionID);
		createDirectory(caseDir);
		createDirectory(caseDir.resolve("xml"));
		createDirectory(caseDir.resolve("manifest"));
		createDirectory(caseDir.resolve("irs"));
		createDirectory(caseDir.resolve("irs").resolve("xml"));
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
	
	public static Task build(TaskConfig taskConfig, Book book) {
		if (!(taskConfig instanceof MeFExportTaskConfig)) {
			throw new IllegalStateException("TaskConfig '" + taskConfig.getTaskID() + 
					"' must be an instance of " + MeFExportTaskConfig.class.getName());
		}
		TestDocXMLGenerator xmlGenerator = new TestDocXMLGenerator();
		return new MeFExportTask((MeFExportTaskConfig)taskConfig, book, xmlGenerator);
	}
}
