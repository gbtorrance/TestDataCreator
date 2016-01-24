package org.tdc.modelinst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.modelinst.ModelInstConfig;
import org.tdc.model.MPathIndex;
import org.tdc.modeldef.ModelDef;

public class ModelInstImpl implements ModelInst {

	private static final Logger log = LoggerFactory.getLogger(ModelInstImpl.class);

	private ModelInstConfig config;
	private ModelDef modelDef;
	private ElementNodeInst rootElement;
	private MPathIndex<NodeInst> mpathIndex;
	
	public ModelInstImpl(ModelInstConfig config, ModelDef modelDef, ElementNodeInst rootElement, MPathIndex<NodeInst> mpathIndex) {
		this.config = config;
		this.modelDef = modelDef;
		this.rootElement = rootElement;
		this.mpathIndex = mpathIndex;
		log.debug("Creating ModelInstImpl: {}", config.getAddr());
	}
	
	@Override
	public ModelInstConfig getModelInstConfig() {
		return config;
	}
	
	@Override
	public ModelDef getModelDef() {
		return modelDef;
	}

	@Override
	public ElementNodeInst getRootElement() {
		return rootElement;
	}
}
