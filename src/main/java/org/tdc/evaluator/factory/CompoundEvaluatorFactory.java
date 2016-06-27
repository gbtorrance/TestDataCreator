package org.tdc.evaluator.factory;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.CompoundEvaluator;
import org.tdc.evaluator.Evaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link CompoundEvaluator} objects.
 */
public class CompoundEvaluatorFactory implements TypeEvaluatorFactory {

	private static final String TYPE = "compound";
	
	private final GeneralEvaluatorFactory generalEvaluatorFactory;
	
	public CompoundEvaluatorFactory(GeneralEvaluatorFactory generalEvaluatorFactory) {
		this.generalEvaluatorFactory = generalEvaluatorFactory;
	}

	@Override
	public Evaluator createEvaluator(XMLConfigWrapper config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String subKey = configKey + ".Evaluator";
		List<Evaluator> evaluators = new ArrayList<>();
		for (int i = 0; i < config.getCount(subKey); i++) {
			Evaluator evaluator = generalEvaluatorFactory.createEvaluator(config, subKey + "(" + i + ")", defaultStyle);
			evaluators.add(evaluator);
		}
		return new CompoundEvaluator(evaluators);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
