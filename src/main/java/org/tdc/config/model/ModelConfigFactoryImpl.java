package org.tdc.config.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.schemaparse.extractor.SchemaExtractorFactory;
import org.tdc.schemaparse.extractor.SchemaExtractorFactoryImpl;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

/**
 * A {@link ModelConfigFactory} implementation.
 * 
 * <p>Creates parent-level {@link SchemaConfig} instances, as necessary.
 * 
 * <p>Caches configuration objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class ModelConfigFactoryImpl implements ModelConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelConfigFactoryImpl.class);

	private Cache<ModelConfig> cache = new CacheImpl<>();
	private SchemaConfigFactory schemaConfigFactory;

	public ModelConfigFactoryImpl(SchemaConfigFactory schemaConfigFactory) {
		this.schemaConfigFactory = schemaConfigFactory;
	}

	@Override
	public synchronized ModelConfig getModelConfig(Addr addr) {
		ModelConfig modelConfig = cache.get(addr);
		if (modelConfig == null) {
			SchemaConfig schemaConfig = schemaConfigFactory.getSchemaConfig(addr.getParentAddr());
			SchemaExtractorFactory schemaExtractorFactory = new SchemaExtractorFactoryImpl();
			modelConfig = new ModelConfigImpl.ModelConfigBuilder(
					schemaConfig, addr.getName(), schemaExtractorFactory).build();
			cache.put(addr, modelConfig);
		}
		else {
			log.debug("Found cached ModelConfig for: {}", addr);
		}
		return modelConfig;
	}
}
