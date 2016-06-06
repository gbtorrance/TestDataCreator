package org.tdc.config.book;

import org.tdc.util.Addr;

/**
 * Defines getters for configuration items applicable to {@link org.tdc.book.Page Pages}.
 */
public interface PageConfig {
	public String getPageName();
	public Addr getModelAddr();
	public DocTypeConfig getDocTypeConfig();
}
