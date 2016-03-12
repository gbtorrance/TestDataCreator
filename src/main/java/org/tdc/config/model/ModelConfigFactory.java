package org.tdc.config.model;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link ModelConfig} instances.
 */
public interface ModelConfigFactory {
	ModelConfig getModelConfig(Addr addr);
}
