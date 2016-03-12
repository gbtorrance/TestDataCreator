package org.tdc.modelinst;

import org.tdc.config.model.ModelConfig;

/**
 * Factory for creating {@link ModelInst} instances.
 * @author greg
 *
 */
public interface ModelInstFactory {
	ModelInst getModelInst(ModelConfig config);
}
