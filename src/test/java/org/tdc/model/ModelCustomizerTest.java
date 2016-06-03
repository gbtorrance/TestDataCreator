package org.tdc.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.excel.ExcelSpreadsheet;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link ModelCustomizerWriter} and {@link ModelCustomizerReader}..
 */
public class ModelCustomizerTest {
	
	// TODO cleanup 
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
	public void test() throws IOException {
		writeCustomizer();
		readCustomizer();
	}
	
	private void writeCustomizer() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet xssfSheet = workbook.createSheet("Customizer");
		
		Spreadsheet sheet = new ExcelSpreadsheet(xssfSheet);
		
		Addr modelAddr = new Addr("Test/TestSchemaV1.0/Model_OldTest_Customized");
		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
		ModelDef modelDef = modelDefFactory.getModelDef(config);
		
		ModelCustomizerWriter writer = new ModelCustomizerWriter(
				modelDef.getRootElement(), config.getModelCustomizerConfig(), sheet);
		writer.writeCustomizer();
		
		File file = new File("testfiles/Temp/TestModelCustomizer.xlsx");
		try (FileOutputStream fileOut = new FileOutputStream(file)) {
			workbook.write(fileOut);
			workbook.close();
		}
		catch(Exception ex) {
			throw new RuntimeException("Unable to create Excel spreadsheet", ex);
		}
	}
	
	private void readCustomizer() {
		Addr modelAddr = new Addr("Test/TestSchemaV1.0/Model_OldTest_Customized");
		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
		ModelDef modelDef = modelDefFactory.getModelDef(config);
		
		Path path = Paths.get("testfiles/Temp/TestModelCustomizer.xlsx");
		Spreadsheet sheet = ExcelSpreadsheet.readExcelSpreadsheetFromPath(path, "Customizer");
		
		ModelCustomizerReader reader = new ModelCustomizerReader(
				modelDef.getRootElement(), config.getModelCustomizerConfig(), sheet);
		reader.readCustomizer();
	}
}
