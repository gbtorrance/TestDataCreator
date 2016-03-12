package org.tdc.config.book;

import java.util.List;

import org.tdc.util.Addr;

/**
 * Defines getters for configuration items applicable to BookDefs.
 */
public interface BookDefConfig {
	Addr getAddr();
	List<PageDefConfig> getPageDefConfigs();
}
