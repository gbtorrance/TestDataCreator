package org.tdc.config.model;

import java.nio.file.Path;

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
	
	public ModelCustomizerConfigImpl(
			Path filePath,
			CellStyle defaultNodeStyle, CellStyle parentNodeStyle, 
			CellStyle attribNodeStyle, CellStyle compositorNodeStyle, CellStyle choiceMarkerStyle,
			int treeStructureColumnCount, int treeStructureColumnWidth, 
			boolean allowMinMaxInvalidOccursCountOverride, int defaultOccursCount) {
		
		this.filePath = filePath;
		this.defaultNodeStyle = defaultNodeStyle;
		this.parentNodeStyle = parentNodeStyle;
		this.attribNodeStyle = attribNodeStyle;
		this.compositorNodeStyle = compositorNodeStyle;
		this.choiceMarkerStyle = choiceMarkerStyle;
		this.treeStructureColumnCount = treeStructureColumnCount;
		this.treeStructureColumnWidth = treeStructureColumnWidth;
		this.allowMinMaxInvalidOccursCountOverride = allowMinMaxInvalidOccursCountOverride;
		this.defaultOccursCount = defaultOccursCount;
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
}
