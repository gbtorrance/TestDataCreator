package org.tdc.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.modeldef.ModelDefConfig;
import org.tdc.config.modeldef.ModelDefConfigFactory;
import org.tdc.config.modeldef.ModelDefConfigFactoryImpl;
import org.tdc.config.modelinst.ModelInstConfig;
import org.tdc.config.modelinst.ModelInstConfigFactory;
import org.tdc.config.modelinst.ModelInstConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.util.Addr;
import org.tdc.util.MPathModelWriterForTesting;
import org.tdc.util.SummaryModelWriterForTesting;

public class ModelTest {
	
	private static final int INDENT_SIZE = 3;
	
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelDefConfigFactory modelDefConfigFactory;
	private static ModelInstConfigFactory modelInstConfigFactory;

	private static SchemaFactory schemaFactory;
	private static ModelDefFactory modelDefFactory;
	private static ModelInstFactory modelInstFactory;
	
/*
	 test modelDef = modelDef
	 test modelInst = modelInst
	 test mpath modelDef = mpath modelDef
	 test mpath modelInst = mpath modelInst
	 test size modelDef = size modelDef
	 test size modelInst = size modelInst
	 test mpath modelDef = mpath modelInst (for those records that exist) (how??)
	
	 === SCHEMA SCENARIOS ===
	 
	 - broken schema
	 - repeating elements
	 - root vs. regular elements
	 - sequence
	 - choice
	 - all
	 - group
	 - complex
	 - simple
	 ------------
	 
	 - root element is sequence repeating
	 - root element is choice
	 - contains group
	 - extension
	 - all
	 - enumeration
	 
	 - flattening situations
	 - ensure default repeating does not exceed maximum for type in question
	 
	
	

		// flatten compositor if:
		// - it is a sequence with one and only one occurrence (and)
		//   - its parent is an element (or)
		//   - its parent is a compositor which is NOT a choice
		//     (because if it's a choice, it needs to be clearly marked as such in the output, so it can't be flattened)
 */
	
	
	@BeforeClass
	public static void setup() {
		Path schemaRoot = Paths.get("testfiles/TDCFiles/Schemas");

		schemaConfigFactory = new SchemaConfigFactoryImpl(schemaRoot);
		modelDefConfigFactory = new ModelDefConfigFactoryImpl(schemaConfigFactory);
		modelInstConfigFactory = new ModelInstConfigFactoryImpl(modelDefConfigFactory);

		schemaFactory = new SchemaFactoryImpl();
		modelDefFactory = new ModelDefFactoryImpl(schemaFactory);
		modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
	}
	
	@Test
	public void
	test1() {
//		String mdAddr = "Test/TestSchemaV1.0/ModelDef_Test1";
//		String miAddr = mdAddr + "/ModelInst1";
		String mdAddr = "Test/TestSchemaV1.0/ModelDef_OldTest";
		String miAddr = mdAddr + "/ModelInst1";
//		System.out.println("--------------------------------------------------");
//		outputModelDefSummary(mdAddr);
//		System.out.println("--------------------------------------------------");
//		outputModelDefMPath(mdAddr);
//		System.out.println("--------------------------------------------------");
//		outputModelInstSummary(miAddr);
//		System.out.println("--------------------------------------------------");
//		outputModelInstMPath(miAddr);
		compareModelDefSummary(mdAddr, "ModelDefSummary.txt");
		compareModelDefMPath(mdAddr, "ModelDefMPath.txt");
		compareModelInstSummary(miAddr, "ModelInstSummary.txt");
		compareModelInstMPath(miAddr, "ModelInstMPath.txt");
	}
	
	@Test
	public void
	oldTest_modelDef_structure_created_correctly() {
		compareModelDefSummary("Test/TestSchemaV1.0/ModelDef_OldTest", "ModelDefSummary.txt");
	}
	
	@Test
	public void
	oldTest_modelDef_mpath_created_correctly() {
		compareModelDefMPath("Test/TestSchemaV1.0/ModelDef_OldTest", "ModelDefMPath.txt");
	}
	
	@Test
	public void
	oldTest_modelInst_structure_created_correctly() {
		compareModelInstSummary("Test/TestSchemaV1.0/ModelDef_OldTest/ModelInst1", "ModelInstSummary.txt");
	}
	
	@Test
	public void
	oldTest_modelInst_mpath_created_correctly() {
		compareModelInstMPath("Test/TestSchemaV1.0/ModelDef_OldTest/ModelInst1", "ModelInstMPath.txt");
	}

	// ------------------------------
	
	private void compareModelDefSummary(String addrStr, String resourceName) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		compareSummary(rootNode, resourceName);
	}
	
	private void compareModelDefMPath(String addrStr, String resourceName) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		compareMPath(rootNode, resourceName);
	}
	
	private void compareModelInstSummary(String addrStr, String resourceName) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		compareSummary(rootNode, resourceName);
	}
	
	private void compareModelInstMPath(String addrStr, String resourceName) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		compareMPath(rootNode, resourceName);
	}
	
	// ------------------------------
	
	// for when it's necessary to output a ModelDef summary
	private void outputModelDefSummary(String addrStr) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		outputSummary(rootNode, addrStr);
	}

	// for when it's necessary to output a ModelDef mpath list
	private void outputModelDefMPath(String addrStr) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		outputMPath(rootNode, addrStr);
	}

	// for when it's necessary to output a ModelInst summary
	private void outputModelInstSummary(String addrStr) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		outputSummary(rootNode, addrStr);
	}

	// for when it's necessary to output a ModelInst mpath list
	private void outputModelInstMPath(String addrStr) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		outputMPath(rootNode, addrStr);
	}
	
	// ------------------------------
	
	private ModelDef getModelDef(String addrStr) {
		Addr modelDefAddr = new Addr(addrStr);
		ModelDefConfig modelDefConfig = modelDefConfigFactory.getModelDefConfig(modelDefAddr);
		return modelDefFactory.getModelDef(modelDefConfig);
	}
	
	private ModelInst getModelInst(String addrStr) {
		Addr modelInstAddr = new Addr(addrStr);
		ModelInstConfig modelInstConfig = modelInstConfigFactory.getModelInstConfig(modelInstAddr);
		return modelInstFactory.getModelInst(modelInstConfig);
	}
	
	private void compareSummary(TDCNode rootNode, String resourceName) {
		SummaryModelWriterForTesting writer = new SummaryModelWriterForTesting(rootNode, INDENT_SIZE);
		List<String> actualLines = writer.writeToStringList();
		List<String> expectedLines = readResourceToStringList(resourceName);
		assertThat(actualLines).isEqualTo(expectedLines);
	}
	
	private void compareMPath(TDCNode rootNode, String resourceName) {
		MPathModelWriterForTesting writer = new MPathModelWriterForTesting(rootNode, 0);
		List<String> actualLines = writer.writeToStringList();
		List<String> expectedLines = readResourceToStringList(resourceName);
		assertThat(actualLines).isEqualTo(expectedLines);
	}
	
	private void outputSummary(TDCNode rootNode, String addrStr) {
		SummaryModelWriterForTesting writer = new SummaryModelWriterForTesting(rootNode, INDENT_SIZE);
		String actualLines = writer.writeToString();
		System.out.println("Actual lines for Addr: " + addrStr);
		System.out.println(actualLines);
	}

	private void outputMPath(TDCNode rootNode, String addrStr) {
		MPathModelWriterForTesting writer = new MPathModelWriterForTesting(rootNode, 0);
		String actualLines = writer.writeToString();
		System.out.println("Actual lines for Addr: " + addrStr);
		System.out.println(actualLines);
	}

	private List<String> readResourceToStringList(String resourceName) {
		List<String> lines = new ArrayList<>();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(resourceName).getFile());
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lines.add(line);
			}
		} 
		catch (FileNotFoundException e) {
			// wrapping as RuntimeException; impossible to code for 'fixing' this issue at runtime
			throw new RuntimeException("Unable to read resource file: " + file.toString(), e);
		}
		return lines;
	}
}
