package org.tdc.modeldef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.model.MPathIndex;
import org.tdc.schema.Schema;

public class ModelDefImpl implements ModelDef {

	private static final Logger log = LoggerFactory.getLogger(ModelDefImpl.class);

	private ModelConfig config;
	private Schema schema;
	private ElementNodeDef rootElement;
	private MPathIndex<NodeDef> mpathIndex;
	
	public ModelDefImpl(ModelConfig config, Schema schema, ElementNodeDef rootElement, MPathIndex<NodeDef> mpathIndex) {
		this.config = config;
		this.schema = schema;
		this.rootElement = rootElement;
		this.mpathIndex = mpathIndex;
		log.debug("Creating ModelDefImpl: {}", config.getAddr());
	}
	
	@Override
	public ModelConfig getModelConfig() {
		return config;
	}
	
	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public ElementNodeDef getRootElement() {
		return rootElement;
	}
}
