package org.tdc.config.model;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link ModelCustomizerColumnConfig} implementation.
 */
public class ModelCustomizerColumnConfigImpl implements ModelCustomizerColumnConfig {
	
	private final String[] headerLabels;
	private final int width;
	private final CellStyle style;
	private final Evaluator initAsNewEvaluator;
	private final Evaluator initFromPrevEvaluator;
	private final String storeValueWithVariableName;
	
	private ModelCustomizerColumnConfigImpl(ModelCustomizerColumnConfigBuilder builder) {
		this.headerLabels = builder.headerLabels;
		this.width = builder.width;
		this.style = builder.style;
		this.initAsNewEvaluator = builder.initAsNewEvaluator;
		this.initFromPrevEvaluator = builder.initFromPrevEvaluator;
		this.storeValueWithVariableName = builder.storeValueWithVariableName;
	}

	@Override
	public String getHeaderLabel(int headerRowNum) {
		return headerLabels[headerRowNum-1];
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public CellStyle getStyle() {
		return style;
	}

	@Override
	public Evaluator getInitAsNewEvaluator() {
		return initAsNewEvaluator;
	}

	@Override
	public Evaluator getInitFromPrevEvaluator() {
		return initFromPrevEvaluator;
	}

	@Override
	public String getStoreValueWithVariableName() {
		return storeValueWithVariableName;
	}

	public static class ModelCustomizerColumnConfigBuilder {
		private static final String CONFIG_PREFIX = "Customizer.Columns.Column";

		private final XMLConfigWrapper config;
		private final GeneralEvaluatorFactory evaluatorFactory;
		private final int headerRowCount;
		private final CellStyle defaultColumnStyle;
		
		private String[] headerLabels;
		private int width;
		private CellStyle style;
		private Evaluator initAsNewEvaluator;
		private Evaluator initFromPrevEvaluator;
		private String storeValueWithVariableName;
		
		public ModelCustomizerColumnConfigBuilder(XMLConfigWrapper config, 
				GeneralEvaluatorFactory evaluatorFactory, int headerRowCount, CellStyle defaultColumnStyle) {
			this.config = config;
			this.evaluatorFactory = evaluatorFactory;
			this.headerRowCount = headerRowCount;
			this.defaultColumnStyle = defaultColumnStyle;
		}
		
		public List<ModelCustomizerColumnConfig> buildAll() {
			List<ModelCustomizerColumnConfig> columns = new ArrayList<>();
			int count = config.getCount(CONFIG_PREFIX);
			for (int i = 0; i < count; i++) {
				ModelCustomizerColumnConfig column = build(i);
				columns.add(column);
			}
			return columns;
		}
	
		private ModelCustomizerColumnConfig build(int index) {
			String indexPrefix = CONFIG_PREFIX + "(" + index + ").";
			headerLabels = config.getHeaderLabels(
					indexPrefix + "HeaderLabels", headerRowCount);
			width = config.getInt(indexPrefix + "Width", 0, true);
			style = config.getCellStyle(indexPrefix + "Style", defaultColumnStyle, false);
			initAsNewEvaluator = evaluatorFactory.createEvaluator(
					config, indexPrefix + "InitializeAsNew.Evaluator", style);
			initFromPrevEvaluator = evaluatorFactory.createEvaluator(
					config, indexPrefix + "InitializeFromPrevious.Evaluator", style);
			storeValueWithVariableName = config.getString(
					indexPrefix + "Read.StoreValueWithVariableName", null, false);
			return new ModelCustomizerColumnConfigImpl(this);
		}
	}
}
