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
	private final CellStyle defaultNodeDetailColumnStyle;
	private final CellStyle defaultNodeStyle;
	private final CellStyle parentNodeStyle;
	private final CellStyle attribNodeStyle;
	private final CellStyle compositorNodeStyle;
	private final CellStyle choiceMarkerStyle;
	private final int nodeColumnCount;
	private final int nodeColumnWidth;
	private final int headerRowCount;
	private final String[] nodeHeaderLabels;
	private final String readOccursCountOverrideFromVariable;
	private final boolean allowMinMaxInvalidOccursCountOverride;
	private final int defaultOccursCount;
	private final List<ModelCustomizerColumnConfig> nodeDetailcolumns;
	private final int headerRowStart;
	private final int nodeRowStart;
	private final int nodeColStart;
	private final int nodeDetailColStart;
	
	private ModelCustomizerConfigImpl(Builder builder) {
		this.filePath = builder.filePath;
		this.defaultStyle = builder.defaultStyle;
		this.defaultHeaderStyle = builder.defaultHeaderStyle;
		this.defaultNodeDetailColumnStyle = builder.defaultNodeDetailColumnStyle;
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerStyle = builder.choiceMarkerStyle;
		this.nodeColumnCount = builder.nodeColumnCount;
		this.nodeColumnWidth = builder.nodeColumnWidth;
		this.headerRowCount = builder.headerRowCount;
		this.nodeHeaderLabels = builder.nodeHeaderLabels;
		this.readOccursCountOverrideFromVariable = builder.readOccursCountOverrideFromVariable;
		this.allowMinMaxInvalidOccursCountOverride = builder.allowMinMaxInvalidOccursCountOverride;
		this.defaultOccursCount = builder.defaultOccursCount;
		this.nodeDetailcolumns = Collections.unmodifiableList(builder.nodeDetailColumns); // unmodifiable
		this.headerRowStart = builder.headerRowStart;
		this.nodeRowStart = builder.nodeRowStart;
		this.nodeColStart = builder.nodeColStart;
		this.nodeDetailColStart = builder.nodeDetailColStart;
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
	public CellStyle getDefaultNodeDetailColumnStyle() {
		return defaultNodeDetailColumnStyle;
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
	public int getNodeColumnCount() {
		return nodeColumnCount;
	}

	@Override
	public int getNodeColumnWidth() {
		return nodeColumnWidth;
	}
	
	@Override
	public int getHeaderRowCount() {
		return headerRowCount;
	}
	
	@Override
	public String getNodeHeaderLabel(int headerRowNum) {
		return nodeHeaderLabels[headerRowNum-1];
	}

	@Override
	public String getReadOccursCountOverrideFromVariable() {
		return readOccursCountOverrideFromVariable;
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
	public List<ModelCustomizerColumnConfig> getNodeDetailColumns() {
		return nodeDetailcolumns;
	}

	@Override
	public int getHeaderRowStart() {
		return headerRowStart;
	}

	@Override
	public int getNodeRowStart() {
		return nodeRowStart;
	}

	@Override
	public int getNodeColStart() {
		return nodeColStart;
	}

	@Override
	public int getNodeDetailColStart() {
		return nodeDetailColStart;
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = "Customizer";

		private final XMLConfigWrapper config;
		private final Path modelConfigRoot;
		private final int defaultOccursCount;
		private final GeneralEvaluatorFactory evaluatorFactory;
		private final int headerRowStart;
		private final int nodeColStart;

		private Path filePath;
		private CellStyle defaultStyle;
		private CellStyle defaultHeaderStyle;
		private CellStyle defaultNodeDetailColumnStyle;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerStyle;
		private int nodeColumnCount;
		private int nodeColumnWidth;
		private int headerRowCount;
		private String[] nodeHeaderLabels;
		private String readOccursCountOverrideFromVariable;
		private boolean allowMinMaxInvalidOccursCountOverride;
		private int nodeDetailColStart;
		private List<ModelCustomizerColumnConfig> nodeDetailColumns; 
		private int nodeRowStart;
		
		public Builder(
				XMLConfigWrapper config, Path modelConfigRoot, int defaultOccursCount, 
				GeneralEvaluatorFactory evaluatorFactory) {
			this.config = config;
			this.modelConfigRoot = modelConfigRoot;
			this.defaultOccursCount = defaultOccursCount;
			this.evaluatorFactory = evaluatorFactory;
			this.headerRowStart = 1;
			this.nodeColStart = 1;
		}
	
		public ModelCustomizerConfig build() {
			ModelCustomizerConfig custConfig = null;
			if (config.hasNode(CONFIG_PREFIX)) {
				String fileName = config.getString(CONFIG_PREFIX + ".FileName", null, true);
				filePath = modelConfigRoot.resolve(fileName);
				defaultStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultStyle", null, true);
				defaultHeaderStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultHeaderStyle", defaultStyle, false);
				defaultNodeDetailColumnStyle = config.getCellStyle(
						CONFIG_PREFIX + ".DefaultNodeDetailColumnStyle", defaultStyle, false);
				defaultNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultNodeStyle", defaultStyle, false);
				parentNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".ParentNodeStyle", defaultNodeStyle, false);
				attribNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".AttribNodeStyle", defaultNodeStyle, false);
				compositorNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".CompositorNodeStyle", defaultNodeStyle, false);
				choiceMarkerStyle = config.getCellStyle(CONFIG_PREFIX + ".ChoiceMarkerStyle", defaultNodeStyle, false);
				nodeColumnCount = config.getInt(CONFIG_PREFIX + ".NodeColumnCount", 0, true);
				nodeColumnWidth = config.getInt(CONFIG_PREFIX + ".NodeColumnWidth", 0, true);
				headerRowCount = config.getInt(CONFIG_PREFIX + ".HeaderRowCount", 1, false);
				nodeHeaderLabels = config.getHeaderLabels(
						CONFIG_PREFIX + ".NodeHeaderLabels", headerRowCount);
				readOccursCountOverrideFromVariable = config.getString(
						CONFIG_PREFIX + ".ReadOccursCountOverrideFromVariable", null, true);
				allowMinMaxInvalidOccursCountOverride = config.getBoolean(
						CONFIG_PREFIX + ".AllowMinMaxInvalidOccursCountOverride", false, false);
				nodeDetailColStart = nodeColStart + nodeColumnCount;
				nodeDetailColumns = new ModelCustomizerColumnConfigImpl.Builder(
						config, evaluatorFactory, headerRowCount, 
						defaultNodeDetailColumnStyle, nodeDetailColStart).buildAll();
				nodeRowStart = headerRowStart + headerRowCount;
				custConfig = new ModelCustomizerConfigImpl(this);
			}
			return custConfig;
		}
	}
}
