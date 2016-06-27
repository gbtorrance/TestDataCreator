package org.tdc.spreadsheet;

import java.awt.Color;

/**
 * A {@link CellStyle} implementation.
 */
public class CellStyleImpl implements CellStyle {

	private final String fontName;
	private final double fontHeight;
	private final Color color;
	private final Color fillColor;
	private final boolean italic;
	private final boolean bold;
	private final boolean shrinkToFit;
	
	public CellStyleImpl(
			String fontName, double fontHeight, Color color, Color fillColor,
			boolean italic, boolean bold, boolean shrinkToFit) {
		this.fontName = fontName;
		this.fontHeight = fontHeight;
		this.color = color;
		this.fillColor = fillColor;
		this.italic = italic;
		this.bold = bold;
		this.shrinkToFit = shrinkToFit;
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
		return color == null ? null : getColorRed() + " " + getColorGreen() + " " + getColorBlue();
	}

	@Override
	public int getColorRed() {
		return color == null ? -1 : color.getRed();
	}

	@Override
	public int getColorGreen() {
		return color == null ? -1 : color.getGreen();
	}

	@Override
	public int getColorBlue() {
		return color == null ? -1 : color.getBlue();
	}

	@Override
	public Color getFillColor() {
		return fillColor;
	}
	
	@Override
	public String getFillColorRGB() {
		return fillColor == null ? null : getFillColorRed() + " " + getFillColorGreen() + " " + getFillColorBlue();
	}

	@Override
	public int getFillColorRed() {
		return fillColor == null ? -1 : fillColor.getRed();
	}

	@Override
	public int getFillColorGreen() {
		return fillColor == null ? -1 : fillColor.getGreen();
	}

	@Override
	public int getFillColorBlue() {
		return fillColor == null ? -1 : fillColor.getBlue();
	}

	@Override
	public boolean getItalic() {
		return italic;
	}
	
	@Override
	public boolean getBold() {
		return bold;
	}
	
	@Override
	public boolean getShrinkToFit() {
		return shrinkToFit;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(fontName).append(", ");
		sb.append(fontHeight).append(", ");
		sb.append("color: [").append(getColorRGB()).append("], ");
		sb.append("fillColor: [").append(getFillColorRGB()).append("], ");
		sb.append("italic: ").append(italic).append(", ");
		sb.append("bold: ").append(bold).append(", ");
		sb.append("shrinkToFit: ").append(shrinkToFit);
		sb.append("]");
		return sb.toString();
	}
}
