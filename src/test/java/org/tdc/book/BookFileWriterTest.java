package org.tdc.book;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.book.TaskConfigFactoryImpl;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.modelcustomizer.ModelCustomizerWriter;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link BookFileWriter} an its related classes.
 */
public class BookFileWriterTest {
	
	// TODO improve test 
	// not true unit testing at this point, since there is no verification of results;
	// however this does exercise the creation of a book spreadsheet file from a Book object
	
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;
	private static TaskConfigFactory taskConfigFactory;
	private static BookConfigFactory bookConfigFactory;
	private static SpreadsheetFileFactory spreadsheetFileFactory;
	private static SchemaFactory schemaFactory;
	private static ModelDefFactory modelDefFactory;
	private static ModelInstFactory modelInstFactory;
	private static BookFactory bookFactory;

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
	}
	
	@Test
	public void testWriteBook() {
		Addr bookAddr = new Addr("/Tax/IndividualIncome2012v1");
		BookConfig bookConfig = bookConfigFactory.getBookConfig(bookAddr);
		SpreadsheetFile bookFile = spreadsheetFileFactory.createNewSpreadsheetFile();
		
		BookFileWriter bookFileWriter =  new BookFileWriter(bookConfig, bookFile, modelInstFactory);
		bookFileWriter.write();
		
		Path bookFilePath = Paths.get("testfiles/Temp/TestBook.xlsx");
		bookFile.save(bookFilePath);
	}
	
	@Test
	public void testWriteBookBasedOnExistingBook() {
		Addr bookAddr = new Addr("/Tax/IndividualIncome2012v1");
		BookConfig bookConfig = bookConfigFactory.getBookConfig(bookAddr);
		SpreadsheetFile bookFile = spreadsheetFileFactory.createNewSpreadsheetFile();
		
		Path basedOnBookPath = Paths.get("testfiles/SampleFiles/TestBook.xlsx");
		SpreadsheetFile basedOnBookSpreadsheetFile = 
				spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(basedOnBookPath);
		Book basedOnBook = bookFactory.getBook(basedOnBookSpreadsheetFile);
		BookTestDataLoader loader = new BookTestDataLoader(basedOnBook, basedOnBookSpreadsheetFile);
		loader.loadTestData();
		
		BookFileWriter bookFileWriter =  new BookFileWriter(bookConfig, bookFile, modelInstFactory);
		bookFileWriter.writeWithTestDataFromExistingBook(basedOnBook);
		
		Path bookFilePath = Paths.get("testfiles/Temp/TestBookWithExistingData.xlsx");
		bookFile.save(bookFilePath);
	}
	
	@Test
	public void initCustomizer() {
		Addr modelAddr = new Addr("Tax/efile1040x_2012v3.0/Model_1040A");
		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
		ModelDef modelDef = modelDefFactory.getModelDef(config);
		
		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = factory.createNewSpreadsheetFile();
		
		ModelCustomizerWriter writer = new ModelCustomizerWriter(
				modelDef.getRootElement(), config.getModelCustomizerConfig(), spreadsheetFile, null);
		writer.writeCustomizer();
		
		Path path = Paths.get("testfiles/Temp/Customizer2.xlsx");
		spreadsheetFile.save(path);
	}

	@Test
	public void initCustomizerFromPrevious() {
		Addr modelAddr = new Addr("Tax/efile1040x_2012v3.0/Model_1040A");
		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
		ModelDef modelDef = modelDefFactory.getModelDef(config);

		Addr prevModelAddr = new Addr("Tax/efile1040x_2011v3.0/Model_1040A");
		ModelConfig prevConfig = modelConfigFactory.getModelConfig(prevModelAddr);
		ModelDef prevModelDef = modelDefFactory.getModelDef(prevConfig);

		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = factory.createNewSpreadsheetFile();
		
		ModelCustomizerWriter writer = new ModelCustomizerWriter(
				modelDef.getRootElement(), config.getModelCustomizerConfig(), spreadsheetFile, prevModelDef.getMPathIndex());
		writer.writeCustomizer();
		
		Path path = Paths.get("testfiles/Temp/Customizer3.xlsx");
		spreadsheetFile.save(path);
	}

//	@Test
//	public void initCustomizer2011() {
//		Addr modelAddr = new Addr("Tax/efile1040x_2011v3.0/Model_1040A");
//		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
//		ModelDef modelDef = modelDefFactory.getModelDefSkipCustomization(config);
//		
//		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
//		SpreadsheetFile spreadsheetFile = factory.getSpreadsheetFile();
//		
//		ModelCustomizerWriter writer = new ModelCustomizerWriter(
//				modelDef.getRootElement(), config.getModelCustomizerConfig(), spreadsheetFile, null);
//		writer.writeCustomizer();
//		
//		Path path = Paths.get("testfiles/TDCFiles/Schemas/Tax/efile1040x_2011v3.0/Model_1040A/Customizer.xlsx");
//		spreadsheetFile.save(path);
//	}
}
