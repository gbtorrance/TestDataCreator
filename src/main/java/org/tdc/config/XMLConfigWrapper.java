package org.tdc.config;

import java.io.File;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.spreadsheet.CellAlignment;
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
	private static final String ITEM_STYLE_ALIGNMENT = "Alignment";
	private static final String ITEM_STYLE_FORMAT = "Format";
	
	private final XMLConfiguration config;
	
	public XMLConfigWrapper(Path path) {
		this(path.toFile());
	}
	
	public XMLConfigWrapper(File file) {
		if (!file.exists()) {
			throw new IllegalStateException("Configuration file does not exist: " + file); 
		}
		try {
			this.config = new XMLConfiguration();
			config.setDelimiterParsingDisabled(true);
			config.load(file);
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
	
	public Integer getInt(String key, Integer defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		Integer result = config.getInt(key, defaultValue);
		logKeyValue(key, "" + result, !found);
		return result;
	}
	
	public double getDouble(String key, double defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		double result = config.getDouble(key, defaultValue);
		logKeyValue(key, Double.toString(result), !found);
		return result;
	}

	public Double getDouble(String key, Double defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		Double result = config.getDouble(key, defaultValue);
		logKeyValue(key, "" + result, !found);
		return result;
	}

	public boolean getBoolean(String key, boolean defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		boolean result = config.getBoolean(key, defaultValue);
		logKeyValue(key, Boolean.toString(result), !found);
		return result;
	}
	
	public Boolean getBoolean(String key, Boolean defaultValue, boolean required) {
		boolean found = validateRequired(key, required);
		Boolean result = config.getBoolean(key, defaultValue);
		logKeyValue(key, "" + result, !found);
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
	
	public String[] getHeaderLabels(String key, int rowCount) {
		final String labelKey = key + ".Label";
		String[] labels = new String[rowCount];
		int labelCount = getCount(labelKey);
		if (labelCount > rowCount) {
			throw new IllegalStateException("Number of " + labelKey + " entries (" + labelCount + 
					") exceeds maximum of (" + rowCount + ")");
		}
		for (int i = 0; i < rowCount; i++) {
			labels[i] = rowCount - labelCount > i ? 
					"" :  
					getString(labelKey + "(" + (i - rowCount + labelCount) + ")", null, true);
		}
		return labels;
	}
	
	public CellStyle getCellStyle(String key, CellStyle defaultValue, boolean required) {
		if (required && !hasNode(key)) {
			throwConfigItemNotFoundException(key);
		}
		String alignmentStr = getString(key + "." + ITEM_STYLE_ALIGNMENT, null, false);
		CellAlignment alignment = alignmentStr == null ? 
				null : CellAlignment.getCellAlignmentByConfigType(alignmentStr);
		CellStyle style = new CellStyleImpl.Builder()
				.setFrom(defaultValue)
				.setFontNameIfProvided(getString(key + "." + ITEM_STYLE_FONT_NAME, null, false))
				.setFontHeightIfProvided(getDouble(key + "." + ITEM_STYLE_FONT_HEIGHT, null, false))
				.setColorIfProvided(getString(key + "." + ITEM_STYLE_COLOR_RGB, null, false))
				.setFillColorIfProvided(getString(key + "." + ITEM_STYLE_FILL_COLOR_RGB, null, false))
				.setItalicIfProvided(getBoolean(key + "." + ITEM_STYLE_ITALIC, null, false))
				.setBoldIfProvided(getBoolean(key + "." + ITEM_STYLE_BOLD, null, false))
				.setShrinkToFitIfProvided(getBoolean(key + "." + ITEM_STYLE_SHRINK_TO_FIT, null, false))
				.setAlignmentIfProvided(alignment)
				.setFormatIfProvided(getString(key + "." + ITEM_STYLE_FORMAT, null, false))
				.build();
		logKeyValue(key, style.toString(), style.equals(defaultValue));
		return style;
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
