package org.tdc.evaluator.factory;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.ValuePlusStyleEvaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link ValuePlusStyleEvaluator} objects.
 */
public class ValuePlusStyleEvaluatorFactory implements TypeEvaluatorFactory {

	private static final String TYPE = "value-plus-style";
	
	private final GeneralEvaluatorFactory generalEvaluatorFactory;
	
	public ValuePlusStyleEvaluatorFactory(GeneralEvaluatorFactory generalEvaluatorFactory) {
		this.generalEvaluatorFactory = generalEvaluatorFactory;
	}

	@Override
	public Evaluator createEvaluator(XMLConfigWrapper config, String configKey) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String evaluatorKey = configKey + ".Evaluator";
		String styleKey = configKey + ".Style";

		if (config.getCount(evaluatorKey) != 1) {
			throw new RuntimeException("ValuePlusStyle element '" + configKey + "' expected to contain exactly 1 Evaluator");
		}
		if (config.getCount(styleKey) != 1) {
			throw new RuntimeException("ValuePlusStyle element '" + configKey + "' expected to contain exactly 1 Style");
		}
		
		Evaluator evaluator = generalEvaluatorFactory.createEvaluator(config, evaluatorKey);
		CellStyle style = config.getCellStyle(styleKey, null, true);

		return new ValuePlusStyleEvaluator(evaluator, style);
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
