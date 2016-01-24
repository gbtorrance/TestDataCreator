package org.tdc.config.modelinst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.modeldef.ModelDefConfig;
import org.tdc.config.modeldef.ModelDefConfigFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class ModelInstConfigFactoryImpl implements ModelInstConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelInstConfigFactoryImpl.class);

	private Cache<ModelInstConfig> cache = new CacheImpl<>();
	private ModelDefConfigFactory modelDefConfigFactory;

	public ModelInstConfigFactoryImpl(ModelDefConfigFactory modelDefConfigFactory) {
		this.modelDefConfigFactory = modelDefConfigFactory;
	}

	@Override
	public ModelInstConfig getModelInstConfig(Addr addr) {
		ModelInstConfig modelInstConfig = cache.get(addr);
		if (modelInstConfig == null) {
			ModelDefConfig modelDefConfig = modelDefConfigFactory.getModelDefConfig(addr.getParentAddr());
			modelInstConfig = new ModelInstConfigImpl(modelDefConfig, addr.getName());
			cache.put(addr, modelInstConfig);
		}
		else {
			log.debug("Found cached ModelInstConfig for: {}", addr);
		}
		return modelInstConfig;
	}
}
