package org.tdc.modeldef;

import org.tdc.config.model.ModelConfig;

public interface ModelDefFactory {
	ModelDef getModelDef(ModelConfig config);
}
