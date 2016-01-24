package org.tdc.config.modelinst;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.modeldef.ModelDefConfig;
import org.tdc.util.Addr;

public class ModelInstConfigImpl implements ModelInstConfig {
	
	// TODO support multi-level addressing rather than just name? can be done by walking the folders to determine when each .config appears

	private static final Logger log = LoggerFactory.getLogger(ModelInstConfigImpl.class);
	private static final String CONFIG_FOLDER = "tdc.modelinst";
	private static final String CONFIG_FILE = "ModelInstConfig.xml";

	private ModelDefConfig modelDefConfig;
	private Addr addr;
	private Path modelInstRoot;
	private Path modelInstConfigRoot;
	private Path modelInstConfigFile;
	
	// config file items
	private int defaultOccurrenceDepth;
	private Map<String, Integer> occurrenceDepthMap = new HashMap<>();
	
	public ModelInstConfigImpl(ModelDefConfig modelDefConfig, String name) {
		this.modelDefConfig = modelDefConfig;
		this.addr = modelDefConfig.getAddr().resolve(name);
		this.modelInstRoot = modelDefConfig.getModelDefRoot().resolve(name);
		this.modelInstConfigRoot = modelInstRoot.resolve(CONFIG_FOLDER);
		this.modelInstConfigFile = modelInstConfigRoot.resolve(CONFIG_FILE);
		validateDirectories();
		loadConfig();
		log.debug("Creating ModelInstConfigImpl: {}", addr);
	}
	
	@Override
	public ModelDefConfig getModelDefConfig() {
		return modelDefConfig;
	}
	
	@Override
	public Addr getAddr() {
		return addr; 
	}
	
	@Override
	public Path getModelInstRoot() {
		return modelInstRoot;
	}

	@Override
	public Path getModelInstConfigRoot() {
		return modelInstConfigRoot;
	}
	
	@Override
	public int getDefaultOccurrenceDepth() {
		return defaultOccurrenceDepth;
	}

	@Override
	public int getMPathOccurrenceDepth(String mpath) {
		// TODO cleanup terminology here; not sure I'm happy with the naming
		int depth = defaultOccurrenceDepth;
		if (occurrenceDepthMap.containsKey(mpath)) {
			depth = occurrenceDepthMap.get(mpath).intValue();
		}
		return depth;
	}
	
	private void validateDirectories() {
		if (!Files.isDirectory(modelInstRoot)) {
			throw new IllegalStateException("ModelInst dir does not exist: " + modelInstRoot.toString());
		}
		if (!Files.isDirectory(modelInstConfigRoot)) {
			throw new IllegalStateException("ModelInst '" + CONFIG_FOLDER + "' dir does not exist: " + modelInstConfigRoot.toString());
		}
	}
	
	private void loadConfig() {
		XMLConfigWrapper config = new XMLConfigWrapper(modelInstConfigFile.toFile());
		loadConfigItems(config);
	}
	
	private void loadConfigItems(XMLConfigWrapper config) {
		defaultOccurrenceDepth = config.getInt("DefaultOccurrenceDepth", 5, false);
		for (int i = 0; i < config.getMaxIndex("OccurrenceDepth"); i++) {
			String baseKey = "OccurrenceDepth(" + i + ")";
			String mpath = config.getString(baseKey, true);
			int depth = config.getInt(baseKey + "[@Depth]", true);
			occurrenceDepthMap.put(mpath, depth);
		}
	}
}
