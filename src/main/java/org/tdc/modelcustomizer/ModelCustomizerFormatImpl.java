package org.tdc.modelcustomizer;

import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link ModelCustomizerFormat} implementation.
 */
public class ModelCustomizerFormatImpl implements ModelCustomizerFormat {
	
	private final CellStyle defaultNodeStyle;
	private final CellStyle parentNodeStyle;
	private final CellStyle attribNodeStyle;
	private final CellStyle compositorNodeStyle;
	private final CellStyle choiceMarkerStyle;
	private final int treeStructureColumnCount;
	private final int treeStructureColumnWidth;
	
	public ModelCustomizerFormatImpl(
			CellStyle defaultNodeStyle, CellStyle parentNodeStyle, 
			CellStyle attribNodeStyle, CellStyle compositorNodeStyle, CellStyle choiceMarkerStyle,
			int treeStructureColumnCount, int treeStructureColumnWidth) {
		
		this.defaultNodeStyle = defaultNodeStyle;
		this.parentNodeStyle = parentNodeStyle;
		this.attribNodeStyle = attribNodeStyle;
		this.compositorNodeStyle = compositorNodeStyle;
		this.choiceMarkerStyle = choiceMarkerStyle;
		this.treeStructureColumnCount = treeStructureColumnCount;
		this.treeStructureColumnWidth = treeStructureColumnWidth;
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
}
