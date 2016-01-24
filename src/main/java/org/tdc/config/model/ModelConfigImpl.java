package org.tdc.config.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.util.Addr;

public class ModelConfigImpl implements ModelConfig {
	
	// TODO support multi-level addressing rather than just name? can be done by walking the folders to determine when each .config appears
	
	private static final Logger log = LoggerFactory.getLogger(ModelConfigImpl.class);
	private static final String CONFIG_FOLDER = "tdc.model";
	private static final String CONFIG_FILE = "ModelConfig.xml";

	private SchemaConfig schemaConfig;
	private Addr addr;
	private Path modelRoot;
	private Path modelConfigRoot;
	private Path modelConfigFile;
	
	// config file items
	private String schemaRootFile;
	private String schemaRootElementName;
	private String schemaRootElementNamespace;
	private boolean schemaFailOnParserWarning;
	private boolean schemaFailOnParserNonFatalError;
	private int defaultOccurrenceDepth;
	private Map<String, Integer> occurrenceDepthMap = new HashMap<>();
	
	public ModelConfigImpl(SchemaConfig schemaConfig, String name) {
		this.schemaConfig = schemaConfig;
		this.addr = schemaConfig.getAddr().resolve(name);
		this.modelRoot = schemaConfig.getSchemaRoot().resolve(name);
		this.modelConfigRoot = modelRoot.resolve(CONFIG_FOLDER);
		this.modelConfigFile = modelConfigRoot.resolve(CONFIG_FILE);
		validateDirectories();
		loadConfig();
		log.debug("Creating ModelConfigImpl: {}", addr);
	}
	
	@Override
	public SchemaConfig getSchemaConfig() {
		return schemaConfig;
	}
	
	@Override
	public Addr getAddr() {
		return addr; 
	}
	
	@Override
	public Path getModelRoot() {
		return modelRoot;
	}

	@Override
	public Path getModelConfigRoot() {
		return modelConfigRoot;
	}
	
	@Override
	public String getSchemaRootFile() {
		return schemaRootFile;
	}
	
	@Override
	public Path getSchemaRootFileFullPath() {
		return schemaConfig.getSchemaConfigRoot().resolve(schemaRootFile);
	}

	@Override
	public String getSchemaRootElementName() {
		return schemaRootElementName;
	}

	@Override
	public String getSchemaRootElementNamespace() {
		return schemaRootElementNamespace;
	}

	@Override
	public boolean isFailOnParserWarning() {
		return schemaFailOnParserWarning;
	}

	@Override
	public boolean isFailOnParserNonFatalError() {
		return schemaFailOnParserNonFatalError;
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
		if (!Files.isDirectory(modelRoot)) {
			throw new IllegalStateException("Model dir does not exist: " + modelRoot.toString());
		}
		if (!Files.isDirectory(modelConfigRoot)) {
			throw new IllegalStateException("Model '" + CONFIG_FOLDER + "' dir does not exist: " + modelConfigRoot.toString());
		}
	}
	
	private void loadConfig() {
		XMLConfigWrapper config = new XMLConfigWrapper(modelConfigFile.toFile());
		loadConfigItems(config);
	}
	
	private void loadConfigItems(XMLConfigWrapper config) {
		schemaRootFile = config.getString("SchemaRootFile", true);
		schemaRootElementName = config.getString("SchemaRootElementName", true);
		schemaRootElementNamespace = config.getString("SchemaRootElementNamespace", true);
		schemaFailOnParserWarning = config.getBoolean("SchemaFailOnParserWarning", false , false); // default: false, required: false
		schemaFailOnParserNonFatalError = config.getBoolean("SchemaFailOnParserNonFatalError", true, false); // default: true, required: false
		defaultOccurrenceDepth = config.getInt("DefaultOccurrenceDepth", 5, false);
		for (int i = 0; i < config.getMaxIndex("OccurrenceDepth"); i++) {
			String baseKey = "OccurrenceDepth(" + i + ")";
			String mpath = config.getString(baseKey, true);
			int depth = config.getInt(baseKey + "[@Depth]", true);
			occurrenceDepthMap.put(mpath, depth);
		}
	}
}
