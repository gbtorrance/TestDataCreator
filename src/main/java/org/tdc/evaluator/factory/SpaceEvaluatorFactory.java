package org.tdc.evaluator.factory;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.SpaceEvaluator;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link SpaceEvaluator} objects.
 */
public class SpaceEvaluatorFactory implements TypeEvaluatorFactory {
	
	private static final String TYPE = "space"; 
	
	@Override
	public Evaluator createEvaluator(XMLConfigWrapper config, String configKey) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		int spaceSize = config.getInt(configKey + "[@size]", 1, false);
		return new SpaceEvaluator(spaceSize);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
