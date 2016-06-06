package org.tdc.config.book;

/**
 * Defines getters for configuration items applicable to DocTypes.
 */
public interface DocTypeConfig {
	public String getDocTypeName();
	public int getMinPerTestCase();
	public int getMaxPerTestCase();
}
