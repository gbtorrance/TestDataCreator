package org.tdc.config.model;

import java.nio.file.Path;

import org.tdc.spreadsheet.CellStyle;

/**
 * Defines the information needed to configure a Model Customizer.
 */
public interface ModelCustomizerConfig {
	Path getFilePath();
	CellStyle getDefaultNodeStyle();
	CellStyle getParentNodeStyle();
	CellStyle getAttribNodeStyle();
	CellStyle getCompositorNodeStyle();
	CellStyle getChoiceMarkerStyle();
	int getTreeStructureColumnCount();
	int getTreeStructureColumnWidth();
	boolean getAllowMinMaxInvalidOccursCountOverride();
	int getDefaultOccursCount();
}
