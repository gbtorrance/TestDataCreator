package org.tdc.book;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.model.ModelConfig;
import org.tdc.modelcustomizer.ModelCustomizerWriter;
import org.tdc.modeldef.ModelDef;
import org.tdc.process.Processor;
import org.tdc.process.ProcessorImpl;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link BookFileWriter} an its related classes.
 */
public class BookFileWriterTest {
	
	// TODO improve test 
	// not true unit testing at this point, since there is no verification of results;
	// however this does exercise the creation of a book spreadsheet file from a Book object
	
	private static Processor processor;

	@BeforeClass
	public static void setup() {
		Path schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		Path booksConfigRoot = Paths.get("testfiles/TDCFiles/Books");
		processor = new ProcessorImpl
				.Builder(schemasConfigRoot, booksConfigRoot)
				.defaultFactories()
				.build();
	}
	
	@Test
	public void testWriteBook() {
		Addr bookAddr = new Addr("/Tax/IndividualIncome2012v1");
		Path targetPath = Paths.get("testfiles/Temp/TestBook.xlsm");
		processor.createBook(bookAddr, targetPath, null, true);
	}
	
	@Test
	public void testWriteBookBasedOnExistingBook() {
		Addr bookAddr = new Addr("/Tax/IndividualIncome2012v1");
		Path targetPath = Paths.get("testfiles/Temp/TestBookWithExistingData.xlsm");
		Path basedOnBookPath = Paths.get("testfiles/SampleFiles/TestBook.xlsx");
		processor.createBook(bookAddr, targetPath, basedOnBookPath, true);
	}
	
	@Test
	public void initCustomizer() {
		Addr modelAddr = new Addr("Tax/efile1040x_2012v3.0/Model_1040A");
		ModelConfig config = processor.getModelConfig(modelAddr);
		ModelDef modelDef = processor.getModelDefFactory().getModelDef(config);
		
		SpreadsheetFileFactory factory = processor.getSpreadsheetFileFactory();
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
		ModelConfig config = processor.getModelConfig(modelAddr);
		ModelDef modelDef = processor.getModelDefFactory().getModelDef(config);

		Addr prevModelAddr = new Addr("Tax/efile1040x_2011v3.0/Model_1040A");
		ModelConfig prevConfig = processor.getModelConfig(prevModelAddr);
		ModelDef prevModelDef = processor.getModelDefFactory().getModelDef(prevConfig);

		SpreadsheetFileFactory factory = processor.getSpreadsheetFileFactory();
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
//		ModelConfig config = processor.getModelConfig(modelAddr);
//		ModelDef modelDef = processor.getModelDefFactory().getModelDefSkipCustomization(config);
//		
//		SpreadsheetFileFactory factory = processor.getSpreadsheetFileFactory();
//		SpreadsheetFile spreadsheetFile =  factory.createNewSpreadsheetFile();
//		
//		ModelCustomizerWriter writer = new ModelCustomizerWriter(
//				modelDef.getRootElement(), config.getModelCustomizerConfig(), spreadsheetFile, null);
//		writer.writeCustomizer();
//		
//		Path path = Paths.get("testfiles/TDCFiles/Schemas/Tax/efile1040x_2011v3.0/Model_1040A/Customizer.xlsx");
//		spreadsheetFile.save(path);
//	}
}
