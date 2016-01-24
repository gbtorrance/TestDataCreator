package org.tdc.modelinst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.modelinst.ModelInstConfig;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class ModelInstFactoryImpl implements ModelInstFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelInstFactoryImpl.class);
	
	private Cache<ModelInst> cache = new CacheImpl<>();
	private ModelDefFactory modelDefFactory;
	
	public ModelInstFactoryImpl(ModelDefFactory modelDefFactory) {
		this.modelDefFactory = modelDefFactory;
	}
	
	@Override
	public ModelInst getModelInst(ModelInstConfig config) {
		Addr addr = config.getAddr();
		ModelInst modelInst = cache.get(addr);
		if (modelInst == null) {
			ModelDef modelDef = modelDefFactory.getModelDef(config.getModelDefConfig());
			modelInst = buildNewModelInst(modelDef, config);
			cache.put(addr, modelInst);
		}
		else {
			log.debug("Found cached ModelInst for: {}", addr);
		}
		return modelInst;
	}
	
	private ModelInst buildNewModelInst(ModelDef modelDef, ModelInstConfig config) {
		// TODO possibly support building from serialized object;
		//      factory to make determination based on info in config
		ModelInstBuilder modelInstBuilder = new ModelInstBuilderImpl(config, modelDef);
		return modelInstBuilder.build();
	}
}
