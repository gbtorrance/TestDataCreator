package org.tdc.config.book;

import java.nio.file.Path;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.util.Addr;

/**
 * Interface defining factory for creating {@link FilterConfig} instances based
 * on information extracted from an XML config file.
 */
public interface FilterConfigFactory {
	FilterConfig createFilterConfig(
			XMLConfigWrapper config, String taskConfigKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName);
	List<FilterConfig> createFilterConfigs(
			XMLConfigWrapper config, String filterConfigsKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName);
}
