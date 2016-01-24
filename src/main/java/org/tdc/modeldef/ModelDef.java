package org.tdc.modeldef;

import org.tdc.config.model.ModelConfig;
import org.tdc.schema.Schema;

public interface ModelDef {
	ModelConfig getModelConfig();
	Schema getSchema();
	ElementNodeDef getRootElement();
}
