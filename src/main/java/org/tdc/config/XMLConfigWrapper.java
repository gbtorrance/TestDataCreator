package org.tdc.config;

import java.awt.Color;
import java.io.File;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;

/**
 * Wrapper for Apache Commons {@link XMLConfiguration} class.
 * 
 * <p>Implements various helper getter methods, and ensures that, if a required
 * configuration item is not specified, a {@link NoSuchElementException} will be thrown.  
 */
public class XMLConfigWrapper {
	
	private static final Logger log = LoggerFactory.getLogger(XMLConfigWrapper.class);
	
	private static final String ITEM_STYLE_FONT_NAME = "FontName"; 
	private static final String ITEM_STYLE_FONT_HEIGHT = "FontHeight"; 
	private static final String ITEM_STYLE_COLOR_RGB = "ColorRGB";
	private static final String ITEM_STYLE_FILL_COLOR_RGB = "FillColorRGB";
	private static final String ITEM_STYLE_ITALIC = "Italic";
	private static final String ITEM_STYLE_BOLD = "Bold";
	private static final String ITEM_STYLE_SHRINK_TO_FIT = "ShrinkToFit";
	
	private final XMLConfiguration config;
	
	public XMLConfigWrapper(Path path) {
		this(path.toFile());
	}
	
	public XMLConfigWrapper(File file) {
		if (!file.exists()) {
			throw new IllegalStateException("Configuration file does not exist: " + file); 
		}
		try {
			this.config = new XMLConfiguration(file);
		} 
		catch (ConfigurationException e) {
			throw new IllegalStateException("Unable to read configuration file: " + file, e);
		}
	}
	
	public XMLConfiguration getXMLConfiguration() {
		return config;
	}
	
	public String getString(String key, String defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		String result = config.getString(key, defaultValue);
		logKeyValue(key, result, !found);
		return result;
	}

	public int getInt(String key, int defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		int result = config.getInt(key, defaultValue);
		logKeyValue(key, Integer.toString(result), !found);
		return result;
	}
	
	public double getDouble(String key, double defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		double result = config.getDouble(key, defaultValue);
		logKeyValue(key, Double.toString(result), !found);
		return result;
	}

	public boolean getBoolean(String key, boolean defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		boolean result = config.getBoolean(key, defaultValue);
		logKeyValue(key, Boolean.toString(result), !found);
		return result;
	}
	
	public boolean hasNode(String key) {
		// returns whether a node exists, regardless of whether there is a value associated with that node;
		// suitable for detecting whether a parent node exists
		return config.configurationsAt(key).size() > 0;
	}
	
	public int getMaxIndex(String key) {
		return config.getMaxIndex(key);
	}
	
	public int getCount(String key) {
		return config.getMaxIndex(key) + 1;
	}
	
	public CellStyle getCellStyle(String key, CellStyle defaultValue, boolean required) {
		CellStyle result = null;
		if (hasNode(key)) {
			// style element exists;
			// extract info from style
			String defaultFontName = defaultValue == null ? null : defaultValue.getFontName();
			String fontName = getString(key + "." + ITEM_STYLE_FONT_NAME, defaultFontName, false);
			
			double defaultFontHeight = defaultValue == null ? 0 : defaultValue.getFontHeight();
			double fontHeight = getDouble(key + "." + ITEM_STYLE_FONT_HEIGHT, defaultFontHeight, false);
			
			String colorRGBDefault = defaultValue == null ? null : defaultValue.getColorRGB();
			String colorRGB = getString(key + "." + ITEM_STYLE_COLOR_RGB, colorRGBDefault, false);
			Color color = colorRGB == null ? null : createColorFromRGB(colorRGB);
			
			String fillColorRGBDefault = defaultValue == null ? null : defaultValue.getFillColorRGB();
			String fillColorRGB = getString(key + "." + ITEM_STYLE_FILL_COLOR_RGB, fillColorRGBDefault, false);
			Color fillColor = fillColorRGB == null ? null : createColorFromRGB(fillColorRGB);
			
			boolean italicDefault = defaultValue == null ? false : defaultValue.getItalic();
			boolean italic = getBoolean(key + "." + ITEM_STYLE_ITALIC, italicDefault, false);
			
			boolean boldDefault = defaultValue == null ? false : defaultValue.getBold();
			boolean bold = getBoolean(key + "." + ITEM_STYLE_BOLD, boldDefault, false);
			
			boolean shrinkToFitDefault = defaultValue == null ? false : defaultValue.getShrinkToFit();
			boolean shrinkToFit = getBoolean(key + "." + ITEM_STYLE_SHRINK_TO_FIT, shrinkToFitDefault, false);
			
			// ensure we have the bare minimum needed to create a style
			if (fontName == null || fontHeight == 0) {
				String msg = "Style '" + key + "' (or associated default style) must have at least a " +
						ITEM_STYLE_FONT_NAME + " and a " + ITEM_STYLE_FONT_HEIGHT + " element";
				throw new NoSuchElementException(msg);
			}
			
			// create style
			result = new CellStyleImpl(fontName, fontHeight, color, fillColor, italic, bold, shrinkToFit);
		}
		else {
			// style parent element does NOT exist
			if (required) {
				// at least some style information is required;
				// even if it's just an empty style element; error
				throwConfigItemNotFoundException(key);
			}
			else {
				// return default value, whether it's null or not; 
				// don't care; style info is not required
				result = defaultValue;
			}
		}
		logKeyValue(key, result.toString(), result == defaultValue);
		return result;
	}
	
	private Color createColorFromRGB(String colorRGB) {
		Color color = null;
		String[] colorSplit = colorRGB.split("[ ]+");
		boolean validFormat = colorSplit.length == 3;
		if (validFormat) { 
			try {
				int red = Integer.parseUnsignedInt(colorSplit[0]);
				int green = Integer.parseUnsignedInt(colorSplit[1]);
				int blue = Integer.parseUnsignedInt(colorSplit[2]);
				color = new Color(red, green, blue);
			}
			catch (NumberFormatException ex) {
				validFormat = false;
			}
		}
	
		if (!validFormat) {
			throw new RuntimeException(ITEM_STYLE_COLOR_RGB + 
					" must contain 3 non-negative integer values separated by spaces; '" + 
					colorRGB + "' is not valid");
		}
		
		return color;
	}

	/**
	 * Checks to see if an item with a particular key is found;
	 * if it is required, and NOT found, throw an error;
	 * in all other cases, return whether or not it was found
	 */
	private boolean validateRequired(String key, boolean required) {
		boolean found = config.containsKey(key);
		if (required && !found) {
			throwConfigItemNotFoundException(key);
		}
		return found;
	}
	
	private void throwConfigItemNotFoundException(String key) {
		throw new NoSuchElementException("Required config item '" + key + "' not found in: " + config.getFile().toString());
	}
	
	private void logKeyValue(String key, String value, boolean isDefault) {
		log.info(">: {}, {} {}", key, value, isDefault ? " ** DEFAULT **" : "");	
	}
}
