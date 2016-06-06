package org.tdc.config.model;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.util.Addr;

/**
 * A {@link ModelConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ModelConfigImpl implements ModelConfig {
	
	private static final Logger log = LoggerFactory.getLogger(ModelConfigImpl.class);

	private final SchemaConfig schemaConfig;
	private final Addr addr;
	private final Path modelConfigRoot;
	private final String schemaRootFile;
	private final String schemaRootElementName;
	private final String schemaRootElementNamespace;
	private final boolean schemaFailOnParserWarning;
	private final boolean schemaFailOnParserNonFatalError;
	private final int defaultOccursCount;
	private final ModelCustomizerConfig modelCustomizerConfig;
	
	private ModelConfigImpl(ModelConfigBuilder builder) {
		this.schemaConfig = builder.schemaConfig;
		this.addr = builder.addr;
		this.modelConfigRoot = builder.modelConfigRoot;
		this.schemaRootFile = builder.schemaRootFile;
		this.schemaRootElementName = builder.schemaRootElementName;
		this.schemaRootElementNamespace = builder.schemaRootElementNamespace;
		this.schemaFailOnParserWarning = builder.schemaFailOnParserWarning;
		this.schemaFailOnParserNonFatalError = builder.schemaFailOnParserNonFatalError;
		this.defaultOccursCount = builder.defaultOccursCount;
		this.modelCustomizerConfig = builder.modelCustomizerConfig;
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
	
	public static class ModelConfigBuilder {
		private static final String CONFIG_FILE = "TDCModelConfig.xml";
		
		private final XMLConfigWrapper config;
		private final SchemaConfig schemaConfig;
		private final Addr addr;
		private final Path modelConfigRoot;

		private String schemaRootFile;
		private String schemaRootElementName;
		private String schemaRootElementNamespace;
		private boolean schemaFailOnParserWarning;
		private boolean schemaFailOnParserNonFatalError;
		private int defaultOccursCount;
		private ModelCustomizerConfig modelCustomizerConfig;
		
		public ModelConfigBuilder(SchemaConfig schemaConfig, String name) {
			this.schemaConfig = schemaConfig;
			this.addr = schemaConfig.getAddr().resolve(name);
			log.info("Creating ModelConfigImpl: {}", addr);
			this.modelConfigRoot = schemaConfig.getSchemaConfigRoot().resolve(name);
			if (!Files.isDirectory(modelConfigRoot)) {
				throw new IllegalStateException("ModelConfig root dir does not exist: " + modelConfigRoot.toString());
			}
			Path modelConfigFile = modelConfigRoot.resolve(CONFIG_FILE);
			this.config = new XMLConfigWrapper(modelConfigFile);
		}

		public ModelConfig build() {
			schemaRootFile = config.getString("SchemaRootFile", null, true);
			schemaRootElementName = config.getString("SchemaRootElementName", null, true);
			schemaRootElementNamespace = config.getString("SchemaRootElementNamespace", null, true);
			schemaFailOnParserWarning = config.getBoolean("SchemaFailOnParserWarning", false , false);
			schemaFailOnParserNonFatalError = config.getBoolean("SchemaFailOnParserNonFatalError", true, false);
			defaultOccursCount = config.getInt("DefaultOccursCount", 5, false);
			modelCustomizerConfig = new ModelCustomizerConfigImpl.ModelCustomizerConfigBuilder(
					config, modelConfigRoot, defaultOccursCount).build();
			return new ModelConfigImpl(this);
		}
	}
}
