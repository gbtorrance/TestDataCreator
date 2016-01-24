package org.tdc.config.modeldef;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.util.Addr;

public class ModelDefConfigImpl implements ModelDefConfig {
	
	// TODO support multi-level addressing rather than just name? can be done by walking the folders to determine when each .config appears
	
	private static final Logger log = LoggerFactory.getLogger(ModelDefConfigImpl.class);
	private static final String DOT_CONFIG = ".config_md";
	private static final String CONFIG_FILE = "ModelDefConfig.xml";

	private SchemaConfig schemaConfig;
	private Addr addr;
	private Path modelDefRoot;
	private Path modelDefConfigRoot;
	private Path modelDefConfigFile;
	
	// config file items
	private String schemaRootFile;
	private String schemaRootElementName;
	private String schemaRootElementNamespace;
	private boolean schemaFailOnParserWarning;
	private boolean schemaFailOnParserNonFatalError;
	
	public ModelDefConfigImpl(SchemaConfig schemaConfig, String name) {
		this.schemaConfig = schemaConfig;
		this.addr = schemaConfig.getAddr().resolve(name);
		this.modelDefRoot = schemaConfig.getSchemaRoot().resolve(name);
		this.modelDefConfigRoot = modelDefRoot.resolve(DOT_CONFIG);
		this.modelDefConfigFile = modelDefConfigRoot.resolve(CONFIG_FILE);
		validateDirectories();
		loadConfig();
		log.debug("Creating ModelDefConfigImpl: {}", addr);
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
	public Path getModelDefRoot() {
		return modelDefRoot;
	}

	@Override
	public Path getModelDefConfigRoot() {
		return modelDefConfigRoot;
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
	
	private void validateDirectories() {
		if (!Files.isDirectory(modelDefRoot)) {
			throw new IllegalStateException("ModelDef dir does not exist: " + modelDefRoot.toString());
		}
		if (!Files.isDirectory(modelDefConfigRoot)) {
			throw new IllegalStateException("ModelDef '" + DOT_CONFIG + "' dir does not exist: " + modelDefConfigRoot.toString());
		}
	}
	
	private void loadConfig() {
		XMLConfigWrapper config = new XMLConfigWrapper(modelDefConfigFile.toFile());
		loadConfigItems(config);
	}
	
	private void loadConfigItems(XMLConfigWrapper config) {
		schemaRootFile = config.getString("SchemaRootFile", true);
		schemaRootElementName = config.getString("SchemaRootElementName", true);
		schemaRootElementNamespace = config.getString("SchemaRootElementNamespace", true);
		schemaFailOnParserWarning = config.getBoolean("SchemaFailOnParserWarning", false , false); // default: false, required: false
		schemaFailOnParserNonFatalError = config.getBoolean("SchemaFailOnParserNonFatalError", true, false); // default: true, required: false
	}
}
