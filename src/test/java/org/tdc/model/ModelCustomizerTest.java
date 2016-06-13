package org.tdc.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.modelcustomizer.ModelCustomizerReader;
import org.tdc.modelcustomizer.ModelCustomizerWriter;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link ModelCustomizerWriter} and {@link ModelCustomizerReader}.
 */
public class ModelCustomizerTest {
	
	// TODO improve test 
	// not true unit testing at this point, since there is no verification of results;
	// however this does verify that a created model customizer spreadsheet can subsequently be read
	
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;

	private static SchemaFactory schemaFactory;
	private static ModelDefFactory modelDefFactory;
	
	@BeforeClass
	public static void setup() {
		Path schemaRoot = Paths.get("testfiles/TDCFiles/Schemas");

		schemaConfigFactory = new SchemaConfigFactoryImpl(schemaRoot);
		modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);

		schemaFactory = new SchemaFactoryImpl();
		modelDefFactory = new ModelDefFactoryImpl(schemaFactory);
	}
	
	@Test
	public void testCustomizerWriteThenRead() throws IOException {
		writeCustomizer();
		readCustomizer();
	}
	
	private void writeCustomizer() {
		Addr modelAddr = new Addr("Test/TestSchemaV1.0/Model_OldTest_Customized");
		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
		ModelDef modelDef = modelDefFactory.getModelDef(config);
		
		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = factory.getSpreadsheetFile();
		
		ModelCustomizerWriter writer = new ModelCustomizerWriter(
				modelDef.getRootElement(), config.getModelCustomizerConfig(), spreadsheetFile);
		writer.writeCustomizer();
		
		Path path = Paths.get("testfiles/Temp/TestModelCustomizer.xlsx");
		spreadsheetFile.save(path);
	}
	
	private void readCustomizer() {
		Addr modelAddr = new Addr("Test/TestSchemaV1.0/Model_OldTest_Customized");
		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
		ModelDef modelDef = modelDefFactory.getModelDef(config);
		
		Path path = Paths.get("testfiles/Temp/TestModelCustomizer.xlsx");
		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = factory.getSpreadsheetFileFromPath(path);
		
		ModelCustomizerReader reader = new ModelCustomizerReader(
				modelDef.getRootElement(), config.getModelCustomizerConfig(), spreadsheetFile);
		reader.readCustomizer();
	}
}
