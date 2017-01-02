package org.tdc.client.profile;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.tdc.client.config.ProfileConfig;
import org.tdc.shared.dto.BookConfigDTO;
import org.tdc.shared.dto.ModelConfigDTO;
import org.tdc.shared.dto.SchemaConfigDTO;
import org.tdc.shared.dto.ServerInfoDTO;

/**
 * A {@link Profile} implementation.
 */
public class ProfileImpl implements Profile {
	private final ProfileConfig config;
	private final Client client;
	private final String urlPrefix;

	private int lastStatus;
	
	private ProfileImpl(Builder builder) {
		this.config = builder.config;
		this.client = builder.client;
		this.urlPrefix = builder.urlPrefix;
	}
	
	@Override
	public ProfileConfig getConfig() {
		return config;
	}
	
	@Override
	public boolean isActive() {
		lastStatus = 0;
		String target = urlPrefix + "/tdc/";
		boolean active = false;
		Response response = null;
		try {
			response = client.target(target).request().get();
			lastStatus = response.getStatus();
			active = lastStatus == Response.Status.OK.getStatusCode();
		}
		catch (Exception e) {
			// intentionally do nothing
		}
		finally {
			if (response != null) {
				response.close();
			}
		}
		return active;
	}
	
	@Override
	public ServerInfoDTO getServerInfo() {
		lastStatus = 0;
		String target = urlPrefix + "/tdc/";
		Response response = client.target(target).request().get();
		lastStatus = response.getStatus();
		ServerInfoDTO serverInfo = response.readEntity(ServerInfoDTO.class);
		response.close();
		return serverInfo;
	}
	
	@Override
	public List<SchemaConfigDTO> getAllSchemaConfigs(Map<String, Exception> errors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ModelConfigDTO> getAllModelConfigs(Map<String, Exception> errors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BookConfigDTO> getAllBookConfigs(Map<String, Exception> errors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLastStatus() {
		return lastStatus;
	}

	@Override
	public void shutdown() {
		client.close();
	}
	
	public static class Builder {
		private final ProfileConfig config;
		
		private Client client;
		private String urlPrefix;

		public Builder(ProfileConfig config) {
			this.config = config;
		}
		
		public Profile build() {
			initClient();
			return new ProfileImpl(this);
		}
		
		private void initClient() {
			client = ClientBuilder.newClient();
			urlPrefix = "http://" + config.getServerHostName() + ":" + config.getServerPort();
		}
	}
}
