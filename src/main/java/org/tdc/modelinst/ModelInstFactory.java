package org.tdc.modelinst;

import org.tdc.config.model.ModelConfig;

public interface ModelInstFactory {
	ModelInst getModelInst(ModelConfig config);
}
