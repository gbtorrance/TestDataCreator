package org.tdc.client.profile;

import java.util.List;
import java.util.Map;

import org.tdc.client.config.ProfileConfig;
import org.tdc.shared.dto.BookConfigDTO;
import org.tdc.shared.dto.ModelConfigDTO;
import org.tdc.shared.dto.SchemaConfigDTO;
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
	List<SchemaConfigDTO> getAllSchemaConfigs(Map<String, Exception> errors);
	List<ModelConfigDTO> getAllModelConfigs(Map<String, Exception> errors);
	List<BookConfigDTO> getAllBookConfigs(Map<String, Exception> errors);
	int getLastStatus();
	void shutdown();
}
