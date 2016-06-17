package org.tdc.evaluator.factory;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.CoalesceEvaluator;
import org.tdc.evaluator.Evaluator;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link CoalesceEvaluator} objects.
 */
public class CoalesceEvaluatorFactory implements TypeEvaluatorFactory {

	private static final String TYPE = "coalesce";
	
	private final GeneralEvaluatorFactory generalEvaluatorFactory;
	
	public CoalesceEvaluatorFactory(GeneralEvaluatorFactory generalEvaluatorFactory) {
		this.generalEvaluatorFactory = generalEvaluatorFactory;
	}

	@Override
	public Evaluator createEvaluator(XMLConfigWrapper config, String configKey) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String subKey = configKey + ".Evaluator";
		List<Evaluator> evaluators = new ArrayList<>();
		for (int i = 0; i < config.getCount(subKey); i++) {
			Evaluator evaluator = generalEvaluatorFactory.createEvaluator(config, subKey + "(" + i + ")");
			evaluators.add(evaluator);
		}
		return new CoalesceEvaluator(evaluators);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}