package org.tdc.config;

import java.io.File;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLConfigWrapper {
	
	private static final Logger log = LoggerFactory.getLogger(XMLConfigWrapper.class);
	
	private XMLConfiguration config;
	
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
	
	public String getString(String key, boolean required) {
		return getString(key, null, required);
	}
	
	public String getString(String key, String defaultValue, boolean required) {
		validateRequired(key, required);
		String result = config.getString(key, defaultValue);
		log.info(">: " + key + ", " + result);
		return result;
	}

	public int getInt(String key, boolean required) {
		return getInt(key, 0, required);
	}

	public int getInt(String key, int defaultValue, boolean required) {
		validateRequired(key, required);
		int result = config.getInt(key, defaultValue);
		log.info(">: " + key + ", " + result);
		return result;
	}
	
	public int getMaxIndex(String key) {
		return config.getMaxIndex(key);
	}
	
	// choosing to not implement this method, since choosing a reasonable default is tricky
	// public boolean getBoolean(String key, boolean required) { ... }
	
	public boolean getBoolean(String key, boolean defaultValue, boolean required) {
		validateRequired(key, required);
		boolean result = config.getBoolean(key, defaultValue);
		log.info(">: " + key + ", " + result);
		return result;
	}
	
	private void validateRequired(String key, boolean required) {
		if (required && !config.containsKey(key)) {
			throw new NoSuchElementException("Required config item '" + key + "' not found in: " + config.getFile().toString());
		}
	}
}
