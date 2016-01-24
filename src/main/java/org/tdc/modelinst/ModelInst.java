package org.tdc.modelinst;

import org.tdc.config.model.ModelConfig;
import org.tdc.modeldef.ModelDef;

public interface ModelInst {
	ModelConfig getModelConfig();
	ModelDef getModelDef();
	ElementNodeInst getRootElement();
}
