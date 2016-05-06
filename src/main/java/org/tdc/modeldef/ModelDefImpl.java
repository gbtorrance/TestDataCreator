package org.tdc.modeldef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.model.MPathIndex;
import org.tdc.schema.Schema;

/**
 * A {@link ModelDef} implementation.
 * 
 * <p>This light-weight object's primary purpose is as a holder for the ModelDef's root {@link ElementNodeDef}.
 */
public class ModelDefImpl implements ModelDef {

	private static final Logger log = LoggerFactory.getLogger(ModelDefImpl.class);

	private ModelConfig config;
	private Schema schema;
	private ElementNodeDef rootElement;
	private MPathIndex<NodeDef> mpathIndex;
	
	public ModelDefImpl(ModelConfig config, Schema schema, ElementNodeDef rootElement, MPathIndex<NodeDef> mpathIndex) {
		log.info("Creating ModelDefImpl: {}", config.getAddr());
		this.config = config;
		this.schema = schema;
		this.rootElement = rootElement;
		this.mpathIndex = mpathIndex;
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
