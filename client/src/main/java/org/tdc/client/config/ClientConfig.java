package org.tdc.client.config;

import java.nio.file.Path;
import java.util.Map;

/**
 * Defines getters for client configuration items.
 */
public interface ClientConfig {
	Path getClientConfigRoot();
	Map<String, ProfileConfig> getProfileConfigs();
}
