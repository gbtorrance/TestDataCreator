package org.tdc.config.book;

import java.util.List;

import org.tdc.config.XMLConfigWrapper;

/**
 * Interface defining factory for creating {@link TaskConfig} instances based
 * on information extracted from an XML config file.
 */
public interface TaskConfigFactory {
	TaskConfig createTaskConfig(XMLConfigWrapper config, String taskConfigKey);
	List<TaskConfig> createTaskConfigs(XMLConfigWrapper config, String taskConfigsKey);
}
