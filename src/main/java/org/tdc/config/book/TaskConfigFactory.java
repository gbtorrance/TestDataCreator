package org.tdc.config.book;

import java.nio.file.Path;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.util.Addr;

/**
 * Interface defining factory for creating {@link TaskConfig} instances based
 * on information extracted from an XML config file.
 */
public interface TaskConfigFactory {
	TaskConfig createTaskConfig(
			XMLConfigWrapper config, String taskConfigKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName);
	List<TaskConfig> createTaskConfigs(
			XMLConfigWrapper config, String taskConfigsKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName);
}
