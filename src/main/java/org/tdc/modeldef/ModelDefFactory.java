package org.tdc.modeldef;

import org.tdc.config.modeldef.ModelDefConfig;

public interface ModelDefFactory {
	ModelDef getModelDef(ModelDefConfig config);
}
