package org.tdc.config.model;

import java.nio.file.Path;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link ModelCustomizerConfig} implementation.
 */
public class ModelCustomizerConfigImpl implements ModelCustomizerConfig {
	
	private final Path filePath;
	private final CellStyle defaultNodeStyle;
	private final CellStyle parentNodeStyle;
	private final CellStyle attribNodeStyle;
	private final CellStyle compositorNodeStyle;
	private final CellStyle choiceMarkerStyle;
	private final int treeStructureColumnCount;
	private final int treeStructureColumnWidth;
	private final boolean allowMinMaxInvalidOccursCountOverride;
	private final int defaultOccursCount;
	
	private ModelCustomizerConfigImpl(ModelCustomizerConfigBuilder builder) {
		this.filePath = builder.filePath;
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerStyle = builder.choiceMarkerStyle;
		this.treeStructureColumnCount = builder.treeStructureColumnCount;
		this.treeStructureColumnWidth = builder.treeStructureColumnWidth;
		this.allowMinMaxInvalidOccursCountOverride = builder.allowMinMaxInvalidOccursCountOverride;
		this.defaultOccursCount = builder.defaultOccursCount;
	}
	
	@Override
	public Path getFilePath() {
		return filePath;
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
	public int getTreeStructureColumnCount() {
		return treeStructureColumnCount;
	}

	@Override
	public int getTreeStructureColumnWidth() {
		return treeStructureColumnWidth;
	}
	
	@Override
	public boolean getAllowMinMaxInvalidOccursCountOverride() {
		return allowMinMaxInvalidOccursCountOverride;
	}
	
	@Override
	public int getDefaultOccursCount() {
		return defaultOccursCount;
	}
	
	public static class ModelCustomizerConfigBuilder {
		private static final String CONFIG_PREFIX = "Customizer";

		private final XMLConfigWrapper config;
		private final Path modelConfigRoot;
		private final int defaultOccursCount;

		private Path filePath;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerStyle;
		private int treeStructureColumnCount;
		private int treeStructureColumnWidth;
		private boolean allowMinMaxInvalidOccursCountOverride;
		
		public ModelCustomizerConfigBuilder(XMLConfigWrapper config, Path modelConfigRoot, int defaultOccursCount) {
			this.config = config;
			this.modelConfigRoot = modelConfigRoot;
			this.defaultOccursCount = defaultOccursCount;
		}
	
		public ModelCustomizerConfig build() {
			ModelCustomizerConfig custConfig = null;
			if (config.hasNode(CONFIG_PREFIX)) {
				String fileName = config.getString(CONFIG_PREFIX + ".FileName", null, true);
				filePath = modelConfigRoot.resolve(fileName);
				defaultNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".DefaultNodeStyle", null, true);
				parentNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".ParentNodeStyle", defaultNodeStyle, false);
				attribNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".AttribNodeStyle", defaultNodeStyle, false);
				compositorNodeStyle = config.getCellStyle(CONFIG_PREFIX + ".CompositorNodeStyle", defaultNodeStyle, false);
				choiceMarkerStyle = config.getCellStyle(CONFIG_PREFIX + ".ChoiceMarkerStyle", defaultNodeStyle, false);
				treeStructureColumnCount = config.getInt(CONFIG_PREFIX + ".TreeStructureColumnCount", 0, true);
				treeStructureColumnWidth = config.getInt(CONFIG_PREFIX + ".TreeStructureColumnWidth", 0, true);
				allowMinMaxInvalidOccursCountOverride = config.getBoolean(CONFIG_PREFIX + ".AllowMinMaxInvalidOccursCountOverride", false, false);
				custConfig = new ModelCustomizerConfigImpl(this);
			}
			return custConfig;
		}
	}
}
