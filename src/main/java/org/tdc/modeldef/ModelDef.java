package org.tdc.modeldef;

import org.tdc.config.modeldef.ModelDefConfig;
import org.tdc.schema.Schema;

public interface ModelDef {
	ModelDefConfig getModelDefConfig();
	Schema getSchema();
	ElementNodeDef getRootElement();
}
