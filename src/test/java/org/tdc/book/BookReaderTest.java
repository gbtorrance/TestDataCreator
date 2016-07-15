package org.tdc.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
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
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link BookReader} an its related classes.
 */
public class BookReaderTest {
	
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
		modelDefFactory = new ModelDefFactoryImpl(schemaFactory, spreadsheetFileFactory);
		modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
		bookFactory = new BookFactoryImpl(bookConfigFactory, modelInstFactory, spreadsheetFileFactory);
	}
	
	@Test
	public void testReadBook() {
		Path bookFilePath = Paths.get("testfiles/SampleFiles/TestBook.xlsx"); 
		Book book = bookFactory.getBook(bookFilePath);
		assertThat(book.getConfig().getAddr()).isEqualTo(new Addr("Tax/IndividualIncome2012v1"));
	}
}
