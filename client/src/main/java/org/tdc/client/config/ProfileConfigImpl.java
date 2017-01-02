package org.tdc.client.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.shared.config.Config;

/**
 * A {@link ProfileConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ProfileConfigImpl implements ProfileConfig {
	private static final Logger log = LoggerFactory.getLogger(ProfileConfigImpl.class);
	
	private final String profileName;
	private final String serverHostName;
	private final int serverPort;
	
	private ProfileConfigImpl(Builder builder) {
		this.profileName = builder.profileName;
		this.serverHostName = builder.serverHostName;
		this.serverPort = builder.serverPort;
	}
	
	@Override
	public String getProfileName() {
		return profileName;
	}
	
	@Override
	public String getServerHostName() {
		return serverHostName;
	}
	
	@Override
	public int getServerPort() {
		return serverPort;
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = "Profiles.Profile";
		
		private final Config config;
		
		private String profileName;
		private String serverHostName;
		private int serverPort;
		
		public Builder(Config config) {
			this.config = config;
		}
		
		public Map<String, ProfileConfig> buildAll() {
			Map<String, ProfileConfig> map = new LinkedHashMap<>();
			int count = config.getCount(CONFIG_PREFIX);
			for (int i = 0; i < count; i++) {
				ProfileConfig profileConfig = build(i);
				map.put(profileConfig.getProfileName(), profileConfig);
			}
			return map;
		}
		
		public ProfileConfig build(int index) {
			String indexPrefix = CONFIG_PREFIX + "(" + index + ")";
			profileName = config.getString(indexPrefix + ".ProfileName", null, true);
			serverHostName = config.getString(indexPrefix + ".ServerHostName", null, true);
			serverPort = config.getInt(indexPrefix + ".ServerPort", 0, true);
			return new ProfileConfigImpl(this);
		}
	}
}
