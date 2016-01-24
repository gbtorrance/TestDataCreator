package org.tdc.config.modeldef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class ModelDefConfigFactoryImpl implements ModelDefConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelDefConfigFactoryImpl.class);

	private Cache<ModelDefConfig> cache = new CacheImpl<>();
	private SchemaConfigFactory schemaConfigFactory;

	public ModelDefConfigFactoryImpl(SchemaConfigFactory schemaConfigFactory) {
		this.schemaConfigFactory = schemaConfigFactory;
	}

	@Override
	public ModelDefConfig getModelDefConfig(Addr addr) {
		ModelDefConfig modelDefConfig = cache.get(addr);
		if (modelDefConfig == null) {
			SchemaConfig schemaConfig = schemaConfigFactory.getSchemaConfig(addr.getParentAddr());
			modelDefConfig = new ModelDefConfigImpl(schemaConfig, addr.getName());
			cache.put(addr, modelDefConfig);
		}
		else {
			log.debug("Found cached ModelDefConfig for: {}", addr);
		}
		return modelDefConfig;
	}
}
