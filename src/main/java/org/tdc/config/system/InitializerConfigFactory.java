package org.tdc.config.system;

import java.nio.file.Path;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;

/**
 * Interface defining factory for creating {@link InitializerConfig} instances based
 * on information extracted from an XML config file.
 */
public interface InitializerConfigFactory {
	InitializerConfig createInitializerConfig(
			XMLConfigWrapper config, String initConfigKey, Path systemConfigRoot);
	List<InitializerConfig> createInitializerConfigs(
			XMLConfigWrapper config, String initConfigsKey, Path systemConfigRoot);
}
