package org.tdc.config.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.modelcustomizer.ModelCustomizerFormat;
import org.tdc.modelcustomizer.ModelCustomizerFormatImpl;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.util.Addr;

/**
 * A {@link ModelConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ModelConfigImpl implements ModelConfig {
	
	// TODO support multi-level addressing rather than just name? can be done by walking the folders to determine when each .config appears
	
	private static final Logger log = LoggerFactory.getLogger(ModelConfigImpl.class);
	private static final String CONFIG_FILE = "TDCModelConfig.xml";

	private final SchemaConfig schemaConfig;
	private final Addr addr;
	private final Path modelConfigRoot;
	private final Path modelConfigFile;
	
	// config file items
	private String schemaRootFile;
	private String schemaRootElementName;
	private String schemaRootElementNamespace;
	private boolean schemaFailOnParserWarning;
	private boolean schemaFailOnParserNonFatalError;
	private ModelCustomizerFormat modelCustomizerFormat;
	private int defaultOccurrenceDepth;
	private Map<String, Integer> occurrenceDepthMap = new HashMap<>();
	
	public ModelConfigImpl(SchemaConfig schemaConfig, String name) {
		this.schemaConfig = schemaConfig;
		this.addr = schemaConfig.getAddr().resolve(name);
		log.info("Creating ModelConfigImpl: {}", addr);
		this.modelConfigRoot = schemaConfig.getSchemaConfigRoot().resolve(name);
		if (!Files.isDirectory(modelConfigRoot)) {
			throw new IllegalStateException("Model config root dir does not exist: " + modelConfigRoot.toString());
		}
		this.modelConfigFile = modelConfigRoot.resolve(CONFIG_FILE);
		loadConfig();
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
	public Path getModelConfigRoot() {
		return modelConfigRoot;
	}

	@Override
	public String getSchemaRootFile() {
		return schemaRootFile;
	}
	
	@Override
	public Path getSchemaRootFileFullPath() {
		return schemaConfig.getSchemaFilesRoot().resolve(schemaRootFile);
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
	public ModelCustomizerFormat getModelCustomizerFormat() {
		return modelCustomizerFormat;
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

	private void loadConfig() {
		XMLConfigWrapper config = new XMLConfigWrapper(modelConfigFile.toFile());
		loadConfigItems(config);
	}
	
	private void loadConfigItems(XMLConfigWrapper config) {
		schemaRootFile = config.getString("SchemaRootFile", null, true);
		schemaRootElementName = config.getString("SchemaRootElementName", null, true);
		schemaRootElementNamespace = config.getString("SchemaRootElementNamespace", null, true);
		schemaFailOnParserWarning = config.getBoolean("SchemaFailOnParserWarning", false , false);
		schemaFailOnParserNonFatalError = config.getBoolean("SchemaFailOnParserNonFatalError", true, false);

		CellStyle defaultNodeStyle = config.getCellStyle("CustomizerFormat.DefaultNodeStyle", null, true);
		CellStyle parentNodeStyle = config.getCellStyle("CustomizerFormat.ParentNodeStyle", defaultNodeStyle, false);
		CellStyle attribNodeStyle = config.getCellStyle("CustomizerFormat.AttribNodeStyle", defaultNodeStyle, false);
		CellStyle compositorNodeStyle = config.getCellStyle("CustomizerFormat.CompositorNodeStyle", defaultNodeStyle, false);
		CellStyle choiceMarkerStyle = config.getCellStyle("CustomizerFormat.ChoiceMarkerStyle", defaultNodeStyle, false);
		int treeStructureColumnCount = config.getInt("CustomizerFormat.TreeStructureColumnCount", 0, true);
		int treeStructureColumnWidth = config.getInt("CustomizerFormat.TreeStructureColumnWidth", 0, true);
		modelCustomizerFormat = new ModelCustomizerFormatImpl(
				defaultNodeStyle, parentNodeStyle, attribNodeStyle,
				compositorNodeStyle, choiceMarkerStyle, 
				treeStructureColumnCount, treeStructureColumnWidth);
		
		defaultOccurrenceDepth = config.getInt("DefaultOccurrenceDepth", 5, false);
		for (int i = 0; i < config.getMaxIndex("OccurrenceDepth"); i++) {
			String baseKey = "OccurrenceDepth(" + i + ")";
			String mpath = config.getString(baseKey, null, true);
			int depth = config.getInt(baseKey + "[@Depth]", 0, true);
			occurrenceDepthMap.put(mpath, depth);
		}
	}
}
