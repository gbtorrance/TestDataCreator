package org.tdc.config.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.schemaparse.extractor.SchemaExtractor;
import org.tdc.schemaparse.extractor.SchemaExtractorFactory;
import org.tdc.util.Addr;

/**
 * A {@link ModelConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ModelConfigImpl implements ModelConfig {
	
	public static final String CONFIG_FILE = "TDCModelConfig.xml";
	
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
	private final List<SchemaExtractor> schemaExtractors;
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
		this.schemaExtractors = Collections.unmodifiableList(builder.schemaExtractors); // unmodifiable
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
	public List<SchemaExtractor> getSchemaExtractors() {
		return schemaExtractors;
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
		private final XMLConfigWrapper config;
		private final SchemaConfig schemaConfig;
		private final Addr addr;
		private final Path modelConfigRoot;
		private final SchemaExtractorFactory schemaExtractorFactory;
		private final GeneralEvaluatorFactory evaluatorFactory;

		private String schemaRootFile;
		private String schemaRootElementName;
		private String schemaRootElementNamespace;
		private boolean schemaFailOnParserWarning;
		private boolean schemaFailOnParserNonFatalError;
		private int defaultOccursCount;
		private List<SchemaExtractor> schemaExtractors;
		private ModelCustomizerConfig modelCustomizerConfig;
		
		public ModelConfigBuilder(SchemaConfig schemaConfig, String name, 
				SchemaExtractorFactory schemaExtractorFactory, GeneralEvaluatorFactory evaluatorFactory) {
			this.schemaConfig = schemaConfig;
			this.addr = schemaConfig.getAddr().resolve(name);
			log.info("Creating ModelConfigImpl: {}", addr);
			this.modelConfigRoot = schemaConfig.getSchemaConfigRoot().resolve(name);
			if (!Files.isDirectory(modelConfigRoot)) {
				throw new IllegalStateException("ModelConfig root dir does not exist: " + modelConfigRoot.toString());
			}
			this.schemaExtractorFactory = schemaExtractorFactory;
			this.evaluatorFactory = evaluatorFactory;
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
			schemaExtractors = schemaExtractorFactory.createSchemaExtractors(config, "SchemaExtractors");
			modelCustomizerConfig = new ModelCustomizerConfigImpl.ModelCustomizerConfigBuilder(
					config, modelConfigRoot, defaultOccursCount, evaluatorFactory).build();
			return new ModelConfigImpl(this);
		}
	}
}
