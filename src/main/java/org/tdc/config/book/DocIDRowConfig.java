package org.tdc.config.book;

/**
 * Defines the information needed to configure a Doc ID row.
 */
public interface DocIDRowConfig {
	DocIDType getType();
	String getDocVariableName();
	String getLabel();
	int getIndex();
	int getRowNum();
}
