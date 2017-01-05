package org.tdc.client.profile;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.tdc.client.config.ProfileConfig;
import org.tdc.shared.dto.BookConfigsDTO;
import org.tdc.shared.dto.ModelConfigsDTO;
import org.tdc.shared.dto.SchemaConfigsDTO;
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
	public SchemaConfigsDTO getAllSchemaConfigs() {
		lastStatus = 0;
		String target = urlPrefix + "/tdc/config/schemas";
		Response response = client.target(target).request().get();
		lastStatus = response.getStatus();
		SchemaConfigsDTO configs = response.readEntity(SchemaConfigsDTO.class);
		response.close();
		return configs;
	}

	@Override
	public ModelConfigsDTO getAllModelConfigs() {
		lastStatus = 0;
		String target = urlPrefix + "/tdc/config/models";
		Response response = client.target(target).request().get();
		lastStatus = response.getStatus();
		ModelConfigsDTO configs = response.readEntity(ModelConfigsDTO.class);
		response.close();
		return configs;
	}

	@Override
	public BookConfigsDTO getAllBookConfigs() {
		lastStatus = 0;
		String target = urlPrefix + "/tdc/config/books";
		Response response = client.target(target).request().get();
		lastStatus = response.getStatus();
		BookConfigsDTO configs = response.readEntity(BookConfigsDTO.class);
		response.close();
		return configs;
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
