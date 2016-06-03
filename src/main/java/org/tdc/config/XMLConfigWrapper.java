package org.tdc.config;

import java.awt.Color;
import java.io.File;
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
	private static final String ITEM_STYLE_ITALIC = "Italic";
	
	private final XMLConfiguration config;
	
	public XMLConfigWrapper(File file) {
		try {
			this.config = new XMLConfiguration(file);
		} 
		catch (ConfigurationException e) {
			throw new IllegalStateException("Unable to read configuration file: " + file, e);
		}
	}
	
	public XMLConfigWrapper(XMLConfiguration config) {
		this.config = config;
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
	
	public double getDouble(String key, int defaultValue, boolean required) {
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
		
		// check is we have enough info to create a style;
		// and if one is required to be specified and we don't, throw an error
		boolean found = true;
		found &= validateRequired(key + "." + ITEM_STYLE_FONT_NAME, required); 
		found &= validateRequired(key + "." + ITEM_STYLE_FONT_HEIGHT, required);
		
		CellStyle result = defaultValue;
		if (found) {
			// have enough to create a cell style
			String fontName = getString(key + "." + ITEM_STYLE_FONT_NAME, null, true);
			double fontHeight = getDouble(key + "." + ITEM_STYLE_FONT_HEIGHT, 0, true);
			String colorRGBDefault = defaultValue == null ? "0 0 0" : defaultValue.getColorRGB();
			String colorRGB = getString(key + "." + ITEM_STYLE_COLOR_RGB, colorRGBDefault, false);
			Color color = createColorFromRGB(colorRGB);
			boolean italicDefault = defaultValue == null ? false : defaultValue.getItalic();
			boolean italic = getBoolean(key + "." + ITEM_STYLE_ITALIC, italicDefault, false);
			result = new CellStyleImpl(fontName, fontHeight, color, italic);
		}
		logKeyValue(key, result.toString(), !found);
		
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
			throw new RuntimeException(ITEM_STYLE_COLOR_RGB + " must contain 3 non-negative integer values separated by spaces");
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
			throw new NoSuchElementException("Required config item '" + key + "' not found in: " + config.getFile().toString());
		}
		return found;
	}
	
	private void logKeyValue(String key, String value, boolean isDefault) {
		log.info(">: {}, {} {}", key, value, isDefault ? " ** DEFAULT **" : "");	
	}
}
