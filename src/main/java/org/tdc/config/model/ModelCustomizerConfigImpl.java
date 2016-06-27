package org.tdc.config.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link ModelCustomizerConfig} implementation.
 */
public class ModelCustomizerConfigImpl implements ModelCustomizerConfig {
	
	private final Path filePath;
	private final CellStyle defaultStyle;
	private final CellStyle defaultHeaderStyle;
	private final CellStyle defaultColumnStyle;
	private final CellStyle defaultNodeStyle;
	private final CellStyle parentNodeStyle;
	private final CellStyle attribNodeStyle;
	private final CellStyle compositorNodeStyle;
	private final CellStyle choiceMarkerStyle;
	private final int treeStructureColumnCount;
	private final int treeStructureColumnWidth;
	private final int headerRowCount;
	private final String[] treeStructureHeaderLabels;
	private final String[] occursHeaderLabels;
	private final String[] occursOverrideHeaderLabels;
	private final boolean allowMinMaxInvalidOccursCountOverride;
	private final int defaultOccursCount;
	private final List<ModelCustomizerColumnConfig> columns;
	
	private ModelCustomizerConfigImpl(ModelCustomizerConfigBuilder builder) {
		this.filePath = builder.filePath;
		this.defaultStyle = builder.defaultStyle;
		this.defaultHeaderStyle = builder.defaultHeaderStyle;
		this.defaultColumnStyle = builder.defaultColumnStyle;
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerStyle = builder.choiceMarkerStyle;
		this.treeStructureColumnCount = builder.treeStructureColumnCount;
		this.treeStructureColumnWidth = builder.treeStructureColumnWidth;
		this.headerRowCount = builder.headerRowCount;
		this.treeStructureHeaderLabels = builder.treeStructureHeaderLabels;
		this.occursHeaderLabels = builder.occursHeaderLabels;
		this.occursOverrideHeaderLabels = builder.occursOverrideHeaderLabels;
		this.allowMinMaxInvalidOccursCountOverride = builder.allowMinMaxInvalidOccursCountOverride;
		this.defaultOccursCount = builder.defaultOccursCount;
		this.columns = Collections.unmodifiableList(builder.columns); // unmodifiable
	}
	
	@Override
	public Path getFilePath() {
		return filePath;
	}
	
	@Override
	public CellStyle getDefaultStyle() {
		return defaultStyle;
	}
	
	@Override
	public CellStyle getDefaultHeaderStyle() {
		return defaultHeaderStyle;
	}
	
	@Override
	public CellStyle getDefaultColumnStyle() {
		return defaultColumnStyle;
	}
	
	@Override
	public CellStyle getDefaultNodeStyle() {
		return defaultNodeStyle;
	}
	
	@Override
	public CellStyle getParentNodeStyle() {
		return parentNodeStyle;
	}
	
	@Override
	public CellStyle getAttribNodeStyle() {
		return attribNodeStyle;
	}
	
	@Override
	public CellStyle getCompositorNodeStyle() {
		return compositorNodeStyle;
	}
	
	@Override
	public CellStyle getChoiceMarkerStyle() {
		return choiceMarkerStyle;
	}
	
	@Override
	public int getHeaderRowCount() {
		return headerRowCount;
	}
	
	@Override
	public int getTreeStructureColumnCount() {
		return treeStructureColumnCount;
	}

	@Override
	public int getTreeStructureColumnWidth() {
		return treeStructureColumnWidth;
	}
	
	@Override
	public String getTreeStructureHeaderLabel(int headerRowNum) {
		return treeStructureHeaderLabels[headerRowNum-1];
	}

	@Override
	public String getOccursHeaderLabel(int headerRowNum) {
		return occursHeaderLabels[headerRowNum-1];
	}

	@Override
	public String getOccursOverrideHeaderLabel(int headerRowNum) {
		return occursOverrideHeaderLabels[headerRowNum-1];
	}

	@Override
	public boolean getAllowMinMaxInvalidOccursCountOverride() {
		return allowMinMaxInvalidOccursCountOverride;
	}
	
	@Override
	public int getDefaultOccursCount() {
		return defaultOccursCount;
	}

	@Override
	public List<ModelCustomizerColumnConfig> getColumns() {
		return columns;
	}
	
	public static String[] getHeaderLabels(XMLConfigWrapper config, String labelsKey, int rowCount) {
		final String labelKey = labelsKey + ".Label";
		String[] labels = new String[rowCount];
		int labelCount = config.getCount(labelKey);
		if (labelCount > rowCount) {
			throw new IllegalStateException("Number of " + labelKey + " entries (" + labelCount + 
					") cannot be more than HeaderRowCount (" + rowCount + ")");
		}
		for (int i = 0; i < rowCount; i++) {
			labels[i] = rowCount - labelCount > i ? 
					"" :  
					config.getString(labelKey + "(" + (i - rowCount + labelCount) + ")", null, true);
		}
		return labels;
	}

	public static class ModelCustomizerConfigBuilder {
		private static final String CONFIG_PREFIX = "Customizer";

		private final XMLConfigWrapper config;
		private final Path modelConfigRoot;
		private final int defaultOccursCount;
		private final GeneralEvaluatorFactory evaluatorFactory;

		private Path filePath;
		private CellStyle defaultStyle;
		private CellStyle defaultHeaderStyle;
		private CellStyle defaultColumnStyle;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerStyle;
		private int treeStructureColumnCount;
		private int treeStructureColumnWidth;
		private int headerRowCount;
		private String[] treeStructureHeaderLabels;
		private String[] occursHeaderLabels;
		private String[] occursOverrideHeaderLabels;
		private boolean allowMinMaxInvalidOccursCountOverride;
		private List<ModelCustomizerColumnConfig> columns; 
		
		public ModelCustomizerConfigBuilder(
				XMLConfigWrapper config, Path modelConfigRoot, int defaultOccursCount, 
				GeneralEvaluatorFactory evaluatorFactory) {
			this.config = config;
			this.modelConfigRoot = modelConfigRoot;
			this.defaultOccursCount = defaultOccursCount;
			this.evaluatorFactory = evaluatorFactory;
		}
	
		public ModelCustomizerConfig build() {
			ModelCustomizerConfig custConfig = null;
			if (config.hasNode(CONFIG_PREFIX)) {
				String fileName = config.getString(CONFIG_PREFIX + ".FileName", null, true);
				filePath = modelConfigRoot.resolve(fileName);
				defaultStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultStyle", null, true);
				defaultHeaderStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultHeaderStyle", defaultStyle, false);
				defaultColumnStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultColumnStyle", defaultStyle, false);
				defaultNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultNodeStyle", defaultStyle, false);
				parentNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".ParentNodeStyle", defaultNodeStyle, false);
				attribNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".AttribNodeStyle", defaultNodeStyle, false);
				compositorNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".CompositorNodeStyle", defaultNodeStyle, false);
				choiceMarkerStyle = config.getCellStyle(CONFIG_PREFIX + ".ChoiceMarkerStyle", defaultNodeStyle, false);
				treeStructureColumnCount = config.getInt(CONFIG_PREFIX + ".TreeStructureColumnCount", 0, true);
				treeStructureColumnWidth = config.getInt(CONFIG_PREFIX + ".TreeStructureColumnWidth", 0, true);
				headerRowCount = config.getInt(CONFIG_PREFIX + ".HeaderRowCount", 1, false);
				treeStructureHeaderLabels = getHeaderLabels(
						config, CONFIG_PREFIX + ".TreeStructureHeaderLabels", headerRowCount);
				occursHeaderLabels = getHeaderLabels(
						config, CONFIG_PREFIX + ".OccursHeaderLabels", headerRowCount);
				occursOverrideHeaderLabels = getHeaderLabels(
						config, CONFIG_PREFIX + ".OccursOverrideHeaderLabels", headerRowCount);
				allowMinMaxInvalidOccursCountOverride = config.getBoolean(
						CONFIG_PREFIX + ".AllowMinMaxInvalidOccursCountOverride", false, false);
				columns = new ModelCustomizerColumnConfigImpl.ModelCustomizerColumnConfigBuilder(
						config, evaluatorFactory, headerRowCount, defaultColumnStyle).buildAll();
				custConfig = new ModelCustomizerConfigImpl(this);
			}
			return custConfig;
		}
	}
}
