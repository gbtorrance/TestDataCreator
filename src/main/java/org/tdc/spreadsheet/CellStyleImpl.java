package org.tdc.spreadsheet;

import java.awt.Color;

/**
 * A {@link CellStyle} implementation.
 */
public class CellStyleImpl implements CellStyle {

	private final String fontName;
	private final double fontHeight;
	private final Color color;
	private final boolean italic;
	
	public CellStyleImpl(String fontName, double fontHeight, Color color, boolean italic) {
		this.fontName = fontName;
		this.fontHeight = fontHeight;
		this.color = color;
		this.italic = italic;
	}

	@Override
	public String getFontName() {
		return fontName;
	}

	@Override
	public double getFontHeight() {
		return fontHeight;
	}

	@Override
	public Color getColor() {
		return color;
	}
	
	@Override
	public String getColorRGB() {
		return getColorRed() + " " + getColorGreen() + " " + getColorBlue();
	}

	@Override
	public int getColorRed() {
		return color.getRed();
	}

	@Override
	public int getColorGreen() {
		return color.getGreen();
	}

	@Override
	public int getColorBlue() {
		return color.getBlue();
	}

	@Override
	public boolean getItalic() {
		return italic;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(fontName).append(", ");
		sb.append(fontHeight).append(", ");
		sb.append("color: [").append(getColorRGB()).append("], ");
		sb.append("italic: ").append(italic);
		sb.append("]");
		return sb.toString();
	}
}
