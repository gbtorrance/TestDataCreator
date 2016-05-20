package org.tdc.evaluator.factory;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.Evaluator;

/**
 * Defines functionality for factories for {@link Evaluator} objects.
 */
public interface EvaluatorFactory {
	/**
	 * Creates an {@link Evaluator}.
	 *
	 * @param config Contains configuration information for the Evaluator to be created.
	 * @param configKey The key (in Apache Commons Configuration format} 
	 *        to the root element of the new Evaluator.
	 * @return An {@link Evaluator}
	 */
	Evaluator createEvaluator(XMLConfigWrapper config, String configKey);
}
