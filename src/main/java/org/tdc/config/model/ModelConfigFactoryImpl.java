package org.tdc.config.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class ModelConfigFactoryImpl implements ModelConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelConfigFactoryImpl.class);

	private Cache<ModelConfig> cache = new CacheImpl<>();
	private SchemaConfigFactory schemaConfigFactory;

	public ModelConfigFactoryImpl(SchemaConfigFactory schemaConfigFactory) {
		this.schemaConfigFactory = schemaConfigFactory;
	}

	@Override
	public ModelConfig getModelConfig(Addr addr) {
		ModelConfig modelConfig = cache.get(addr);
		if (modelConfig == null) {
			SchemaConfig schemaConfig = schemaConfigFactory.getSchemaConfig(addr.getParentAddr());
			modelConfig = new ModelConfigImpl(schemaConfig, addr.getName());
			cache.put(addr, modelConfig);
		}
		else {
			log.debug("Found cached ModelConfig for: {}", addr);
		}
		return modelConfig;
	}
}
