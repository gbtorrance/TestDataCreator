package org.tdc.config.model;

import java.nio.file.Path;
import java.util.List;

import org.tdc.spreadsheet.CellStyle;

/**
 * Defines the information needed to configure a Model Customizer.
 */
public interface ModelCustomizerConfig {
	Path getFilePath();
	CellStyle getDefaultStyle();
	CellStyle getDefaultHeaderStyle();
	CellStyle getDefaultColumnStyle();
	CellStyle getDefaultNodeStyle();
	CellStyle getParentNodeStyle();
	CellStyle getAttribNodeStyle();
	CellStyle getCompositorNodeStyle();
	CellStyle getChoiceMarkerStyle();
	int getTreeStructureColumnCount();
	int getTreeStructureColumnWidth();
	int getHeaderRowCount();
	String getTreeStructureHeaderLabel(int headerRowNum);
	String getReadOccursCountOverrideFromVariable();
	boolean getAllowMinMaxInvalidOccursCountOverride();
	int getDefaultOccursCount();
	List<ModelCustomizerColumnConfig> getColumns();
}
