package org.tdc.evaluator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.evaluator.factory.GeneralEvaluatorFactoryImpl;
import org.tdc.evaluator.result.ValuePlusStyleResult;
import org.tdc.evaluator.result.ValueResult;

/**
 * Unit tests for {@link Evaluator} and its related classes.
 */
public class EvaluatorTest {
	
	private static XMLConfigWrapper config; 
	
	@BeforeClass
	public static void beforeClass() {
		ClassLoader classLoader = EvaluatorTest.class.getClassLoader();
		File file = new File(classLoader.getResource("EvaluatorConfigTest.xml").getFile());
		config = new XMLConfigWrapper(file);
	}
	
	@Test
	public void testEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestEmpty.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testSpaceDefaultSizeOne() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceDefaultSizeOne.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo(" ");
	}

	@Test
	public void testSpaceSizeZero() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceSizeZero.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testSpaceSizeOne() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceSizeOne.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo(" ");
	}

	@Test
	public void testSpaceSizeFive() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceSizeFive.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("     ");
	}

	@Test
	public void testLiteral() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestLiteral.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Abc123");
	}

	@Test
	public void testLiteralEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestLiteralEmpty.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCompound() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCompound.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("AbCd - Ef");
	}

	@Test
	public void testCompoundNoMatch() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCompoundNoMatch.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCompoundEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCompoundEmpty.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCoalesce() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCoalesce.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Ab");
	}

	@Test
	public void testCoalesceNoMatch() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCoalesceNoMatch.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCoalesceEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCoalesceEmpty.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testValuePlusStyle() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestValuePlusStyle.Evaluator");
		ValuePlusStyleResult result = (ValuePlusStyleResult)evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Abc");
		assertThat(result.getCellStyle().getFontName()).isEqualTo("Calibri");
		assertThat(result.getCellStyle().getFontHeight()).isEqualTo(11);
	}

	@Test
	public void testValuePlusStyleInsideIf() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestValuePlusStyleInsideIf.Evaluator");
		ValuePlusStyleResult result = (ValuePlusStyleResult)evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Then block");
		assertThat(result.getCellStyle().getFontName()).isEqualTo("Calibri");
		assertThat(result.getCellStyle().getFontHeight()).isEqualTo(11);
	}

	@Test
	public void testValuePlusStyleInsideCoalesce() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestValuePlusStyleInsideCoalesce.Evaluator");
		ValuePlusStyleResult result = (ValuePlusStyleResult)evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Inside coalesce");
		assertThat(result.getCellStyle().getFontName()).isEqualTo("Calibri");
		assertThat(result.getCellStyle().getFontHeight()).isEqualTo(11);
	}

	@Test
	public void testIfEqualsOperatorEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfEqualsOperatorEquals.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Then block");
	}

	@Test
	public void testIfEqualsOperatorNotEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfEqualsOperatorNotEquals.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Else block");
	}

	@Test
	public void testIfNotEqualsOperatorEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfNotEqualsOperatorEquals.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Else block");
	}

	@Test
	public void testIfNotEqualsOperatorNotEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfNotEqualsOperatorNotEquals.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Then block");
	}
	
	@Test
	public void testIfEqualsOperatorNotEqualsAndNoElseBlock() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfEqualsOperatorNotEqualsAndNoElseBlock.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testIfInsideIf() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfInsideIf.Evaluator");
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Inner Else block");
	}
}