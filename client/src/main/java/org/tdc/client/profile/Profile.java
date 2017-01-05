package org.tdc.client.profile;

import org.tdc.client.config.ProfileConfig;
import org.tdc.shared.dto.BookConfigsDTO;
import org.tdc.shared.dto.ModelConfigsDTO;
import org.tdc.shared.dto.SchemaConfigsDTO;
import org.tdc.shared.dto.ServerInfoDTO;

/**
 * Defines the operations that can be performed against a server profile.
 * 
 * <p>Implementations are responsible for REST communications with the server.
 */
public interface Profile {
	ProfileConfig getConfig();
	boolean isActive();
	ServerInfoDTO getServerInfo();
	SchemaConfigsDTO getAllSchemaConfigs();
	ModelConfigsDTO getAllModelConfigs();
	BookConfigsDTO getAllBookConfigs();
	int getLastStatus();
	void shutdown();
}
