package org.tdc.spreadsheet;

import java.awt.Color;

/**
 * Defines the various formatting characteristics of a {@link Spreadsheet} cell.
 */
public interface CellStyle {
	String getFontName();
	double getFontHeight();
	Color getColor();
	String getColorRGB();
	int getColorRed();
	int getColorGreen();
	int getColorBlue();
	boolean getItalic();
}
