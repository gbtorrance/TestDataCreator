package org.tdc.modelinst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.model.MPathIndex;
import org.tdc.modeldef.ModelDef;

public class ModelInstImpl implements ModelInst {

	private static final Logger log = LoggerFactory.getLogger(ModelInstImpl.class);

	private ModelDef modelDef;
	private ElementNodeInst rootElement;
	private MPathIndex<NodeInst> mpathIndex;
	
	public ModelInstImpl(ModelDef modelDef, ElementNodeInst rootElement, MPathIndex<NodeInst> mpathIndex) {
		this.modelDef = modelDef;
		this.rootElement = rootElement;
		this.mpathIndex = mpathIndex;
		log.debug("Creating ModelInstImpl: {}", getModelConfig().getAddr());
	}
	
	@Override
	public ModelConfig getModelConfig() {
		return modelDef.getModelConfig();
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
