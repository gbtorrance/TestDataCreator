package org.tdc.evaluator.factory;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.EmptyEvaluator;
import org.tdc.evaluator.Evaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link EmptyEvaluator} objects.
 */
public class EmptyEvaluatorFactory implements TypeEvaluatorFactory {
	
	private static final String TYPE = "empty"; 
	private static final EmptyEvaluator EMPTY_EVALUATOR = new EmptyEvaluator();
	
	@Override
	public Evaluator createEvaluator(XMLConfigWrapper config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		return EMPTY_EVALUATOR;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
