package org.tdc.book;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
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
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link BookWriter} an its related classes.
 */
public class BookWriterTest {
	
	// TODO improve test 
	// not true unit testing at this point, since there is no verification of results;
	// however this does exercise the creation of a book spreadsheet file from a Book object
	
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;
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
		bookConfigFactory = new BookConfigFactoryImpl(booksRoot, modelConfigFactory);
		
		spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();
		
		schemaFactory = new SchemaFactoryImpl();
		modelDefFactory = new ModelDefFactoryImpl(schemaFactory);
		modelInstFactory = new ModelInstFactoryImpl(modelDefFactory, spreadsheetFileFactory);
		bookFactory = new BookFactoryImpl(modelInstFactory);
	}
	
	@Test
	public void testWriteBook() {
		Addr bookAddr = new Addr("/Tax/IndividualIncome2012v1");
		BookConfig bookConfig = bookConfigFactory.getBookConfig(bookAddr);
		Book book = bookFactory.getBook(bookConfig);
		SpreadsheetFile bookFile = spreadsheetFileFactory.getSpreadsheetFile();
		
		BookWriter bookWriter =  new BookWriterImpl(book, bookFile);
		bookWriter.write();
		
		Path bookFilePath = Paths.get("testfiles/Temp/TestBook.xlsx");
		bookFile.save(bookFilePath);
	}
	
//	@Test
//	public void initCustomizer() {
//		Addr modelAddr = new Addr("Tax/efile1040x_2012v3.0/Model_1040EZ");
//		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
//		ModelDef modelDef = modelDefFactory.getModelDef(config);
//		
//		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
//		SpreadsheetFile spreadsheetFile = factory.getSpreadsheetFile();
//		
//		ModelCustomizerWriter writer = new ModelCustomizerWriter(
//				modelDef.getRootElement(), config.getModelCustomizerConfig(), spreadsheetFile);
//		writer.writeCustomizer();
//		
//		Path path = Paths.get("testfiles/TDCFiles/Schemas/Tax/efile1040x_2012v3.0/Model_1040EZ/Customizer.xlsx");
//		spreadsheetFile.save(path);
//	}
}