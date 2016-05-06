package org.tdc.modelcustomizer;

import org.tdc.spreadsheet.CellStyle;

/**
 * Defines the information needed for formatting a Model Customizer.
 */
public interface ModelCustomizerFormat {
	CellStyle getDefaultNodeStyle();
	CellStyle getParentNodeStyle();
	CellStyle getAttribNodeStyle();
	CellStyle getCompositorNodeStyle();
	CellStyle getChoiceMarkerStyle();
	int getTreeStructureColumnCount();
	int getTreeStructureColumnWidth();
}
