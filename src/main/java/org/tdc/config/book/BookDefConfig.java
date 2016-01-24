package org.tdc.config.book;

import java.util.List;

import org.tdc.util.Addr;

public interface BookDefConfig {
	Addr getAddr();
	List<PageDefConfig> getPageDefConfigs();
}
