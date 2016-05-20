package org.tdc.evaluator.factory;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.LiteralEvaluator;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link LiteralEvaluator} objects.
 */
public class LiteralEvaluatorFactory implements TypeEvaluatorFactory {
	
	private static final String TYPE = "literal"; 
	
	@Override
	public Evaluator createEvaluator(XMLConfigWrapper config, String configKey) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String literal = config.getString(configKey, "", false);
		return new LiteralEvaluator(literal);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
