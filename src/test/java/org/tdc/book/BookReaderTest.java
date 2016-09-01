package org.tdc.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.book.TaskConfigFactoryImpl;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.schemavalidate.SchemaValidatorFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.task.TaskFactory;
import org.tdc.task.TaskFactoryImpl;
import org.tdc.task.TaskProcessor;
import org.tdc.util.Addr;
import org.tdc.util.Util;

/**
 * Unit tests for reading a {@link Book} from spreadsheet file, 
 * loading its contained {@link TestDoc}s/{@link TestCase}s/{@link TestSet}s, 
 * and then performing validation.
 */
public class BookReaderTest {
	
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;
	private static TaskConfigFactory taskConfigFactory;
	private static BookConfigFactory bookConfigFactory;
	private static SpreadsheetFileFactory spreadsheetFileFactory;
	private static SchemaFactory schemaFactory;
	private static ModelDefFactory modelDefFactory;
	private static ModelInstFactory modelInstFactory;
	private static BookFactory bookFactory;
	private static SchemaValidatorFactory schemaValidatorFactory; 
	private static TaskFactory taskFactory;

	@BeforeClass
	public static void setup() {
		Path schemasRoot = Paths.get("testfiles/TDCFiles/Schemas");
		Path booksRoot = Paths.get("testfiles/TDCFiles/Books");
		
		schemaConfigFactory = new SchemaConfigFactoryImpl(schemasRoot);
		modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		taskConfigFactory = new TaskConfigFactoryImpl();
		bookConfigFactory = new BookConfigFactoryImpl(
				booksRoot, modelConfigFactory, taskConfigFactory);
		
		spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();
		
		schemaFactory = new SchemaFactoryImpl();
		modelDefFactory = new ModelDefFactoryImpl(schemaFactory, spreadsheetFileFactory);
		modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
		bookFactory = new BookFactoryImpl(bookConfigFactory, modelInstFactory);
		
		schemaValidatorFactory = new SchemaValidatorFactoryImpl();
		taskFactory = new TaskFactoryImpl();
	}
	
	@Test
	public void testReadBookAndValidate() throws IOException {
		Path bookFilePath = Paths.get("testfiles/SampleFiles/TestBook.xlsx"); 
		SpreadsheetFile spreadsheetFile = spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(bookFilePath);
		
		Book book = bookFactory.getBook(spreadsheetFile);
		assertThat(book.getConfig().getAddr()).isEqualTo(new Addr("Tax/IndividualIncome2012v1"));
		
		BookTestDataLoader loader = new BookTestDataLoader(book, spreadsheetFile);
		loader.loadTestData();
		
		BookSchemaValidator schemaValidator = new BookSchemaValidator(book, schemaValidatorFactory);
		schemaValidator.validate();
		
		Util.purgeDirectory(Paths.get("testfiles/Temp/ExportRoot"));
		TaskProcessor taskProcessor = new TaskProcessor.Builder(taskFactory, book).build();
		taskProcessor.processTasks();
		
		BookSpreadsheetLogWriter logWriter = new BookSpreadsheetLogWriter(book, spreadsheetFile);
		logWriter.writeLog();
		Path newFile = Paths.get("testfiles/Temp/TestBookWithLog.xlsx");
		Files.deleteIfExists(newFile);
		spreadsheetFile.saveAsNew(newFile);
		assertThat(Files.exists(newFile)).isEqualTo(true);
	}
}
