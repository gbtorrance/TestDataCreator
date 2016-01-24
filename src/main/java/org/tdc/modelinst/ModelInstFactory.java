package org.tdc.modelinst;

import org.tdc.config.modelinst.ModelInstConfig;

public interface ModelInstFactory {
	ModelInst getModelInst(ModelInstConfig config);
}
