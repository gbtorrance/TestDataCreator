package org.tdc.modelinst;

import org.tdc.config.modelinst.ModelInstConfig;
import org.tdc.modeldef.ModelDef;

public interface ModelInst {
	ModelInstConfig getModelInstConfig();
	ModelDef getModelDef();
	ElementNodeInst getRootElement();
}
