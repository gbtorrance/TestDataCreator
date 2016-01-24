package org.tdc.modeldef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.modeldef.ModelDefConfig;
import org.tdc.schema.Schema;
import org.tdc.schema.SchemaFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class ModelDefFactoryImpl implements ModelDefFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelDefFactoryImpl.class);

	private Cache<ModelDef> cache = new CacheImpl<>();
	private SchemaFactory schemaFactory;
	
	public ModelDefFactoryImpl(SchemaFactory schemaFactory) {
		this.schemaFactory = schemaFactory;
	}
	
	@Override
	public ModelDef getModelDef(ModelDefConfig config) {
		Addr addr = config.getAddr();
		ModelDef modelDef = cache.get(addr);
		if (modelDef == null) {
			Schema schema = schemaFactory.getSchema(config.getSchemaConfig());
			modelDef = buildNewModelDef(config, schema);
			cache.put(config.getAddr(), modelDef);
		}
		else {
			log.debug("Found cached ModelDef for: {}", addr);
		}
		return modelDef;
	}
	
	private ModelDef buildNewModelDef(ModelDefConfig config, Schema schema) {
		// TODO possibly support building from serialized object;
		//      factory to make determination based on info in config
		ModelDefBuilder modelDefBuilder = new ModelDefBuilderImpl(config, schema);
		return modelDefBuilder.build();
	}
}
