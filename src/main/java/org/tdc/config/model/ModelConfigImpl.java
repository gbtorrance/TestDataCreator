package org.tdc.config.model;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.schema.SchemaConfig;
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
	private int defaultOccursCount;
	private ModelCustomizerConfig modelCustomizerConfig;
	
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
	public boolean getFailOnParserWarning() {
		return schemaFailOnParserWarning;
	}

	@Override
	public boolean getFailOnParserNonFatalError() {
		return schemaFailOnParserNonFatalError;
	}
	
	@Override
	public int getDefaultOccursCount() {
		return defaultOccursCount;
	}
	
	@Override
	public ModelCustomizerConfig getModelCustomizerConfig() {
		return modelCustomizerConfig;
	}
	
	@Override
	public boolean hasModelCustomizerConfig() {
		return modelCustomizerConfig != null;
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
		defaultOccursCount = config.getInt("DefaultOccursCount", 5, false);
		
		if (config.hasNode("Customizer")) {
			String fileName = config.getString("Customizer.FileName", null, true);
			CellStyle defaultNodeStyle = config.getCellStyle("Customizer.DefaultNodeStyle", null, true);
			CellStyle parentNodeStyle = config.getCellStyle("Customizer.ParentNodeStyle", defaultNodeStyle, false);
			CellStyle attribNodeStyle = config.getCellStyle("Customizer.AttribNodeStyle", defaultNodeStyle, false);
			CellStyle compositorNodeStyle = config.getCellStyle("Customizer.CompositorNodeStyle", defaultNodeStyle, false);
			CellStyle choiceMarkerStyle = config.getCellStyle("Customizer.ChoiceMarkerStyle", defaultNodeStyle, false);
			int treeStructureColumnCount = config.getInt("Customizer.TreeStructureColumnCount", 0, true);
			int treeStructureColumnWidth = config.getInt("Customizer.TreeStructureColumnWidth", 0, true);
			boolean allowMinMaxInvalidOccursCountOverride = config.getBoolean("Customizer.AllowMinMaxInvalidOccursCountOverride", false, false);
			modelCustomizerConfig = new ModelCustomizerConfigImpl(
					modelConfigRoot.resolve(fileName), 
					defaultNodeStyle, parentNodeStyle, attribNodeStyle,
					compositorNodeStyle, choiceMarkerStyle, 
					treeStructureColumnCount, treeStructureColumnWidth, 
					allowMinMaxInvalidOccursCountOverride, defaultOccursCount);
		}
	}
}
