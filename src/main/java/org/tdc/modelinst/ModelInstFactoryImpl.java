package org.tdc.modelinst;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.modelcustomizer.ModelCustomizerReader;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheet;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

/**
 * A {@link ModelInstFactory} implementation.
 * 
 * <p>Creates parent-level {@link ModelDef} instances, as necessary.
 * 
 * <p>Caches objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 * 
 * <p>Makes use of a {@link ModelInstBuilder} to do all the heavy lifting of object construction.
 */
public class ModelInstFactoryImpl implements ModelInstFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelInstFactoryImpl.class);
	
	private Cache<ModelInst> cache = new CacheImpl<>();
	private ModelDefFactory modelDefFactory;
	
	public ModelInstFactoryImpl(ModelDefFactory modelDefFactory) {
		this.modelDefFactory = modelDefFactory;
	}
	
	@Override
	public ModelInst getModelInst(ModelConfig config) {
		Addr addr = config.getAddr();
		ModelInst modelInst = cache.get(addr);
		if (modelInst == null) {
			ModelDef modelDef = modelDefFactory.getModelDef(config);
			if (config.hasModelCustomizerConfig()) {
				customizeModelDef(config, modelDef);
			}
			modelInst = buildNewModelInst(modelDef, config.getDefaultOccursCount());
			cache.put(addr, modelInst);
		}
		else {
			log.debug("Found cached ModelInst for: {}", addr);
		}
		return modelInst;
	}
	
	private void customizeModelDef(ModelConfig config, ModelDef modelDef) {
		ModelCustomizerConfig customizerConfig = config.getModelCustomizerConfig();
		Path path = customizerConfig.getFilePath();
		if (!Files.isRegularFile(path)) {
			throw new IllegalStateException("Unable to locate or read customizer spreadsheet file: " + path.toString());
		}
		ExcelSpreadsheet sheet = ExcelSpreadsheet.readExcelSpreadsheetFromPath(path, "Customizer");
		ModelCustomizerReader reader = new ModelCustomizerReader(modelDef.getRootElement(), config.getModelCustomizerConfig(), sheet);
		reader.readCustomizer();
	}
	
	private ModelInst buildNewModelInst(ModelDef modelDef, int defaultOccursCount) {
		// TODO possibly support building from serialized object;
		//      factory to make determination based on info in config
		ModelInstBuilder modelInstBuilder = new ModelInstBuilderImpl(modelDef, defaultOccursCount);
		return modelInstBuilder.build();
	}
}
