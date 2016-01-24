package org.tdc.config.model;

import org.tdc.util.Addr;

public interface ModelConfigFactory {
	ModelConfig getModelConfig(Addr addr);
}
