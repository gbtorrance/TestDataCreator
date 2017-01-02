package org.tdc.client.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.shared.config.Config;
import org.tdc.shared.config.ConfigImpl;

/**
 * A {@link ClientConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ClientConfigImpl implements ClientConfig {
	private static final Logger log = LoggerFactory.getLogger(ClientConfigImpl.class);
	private static final String CONFIG_FILE = "TDCConfig.xml";

	private final Path clientConfigRoot;
	private final Map<String, ProfileConfig> profileConfigs;
	
	private ClientConfigImpl(Builder builder) {
		this.clientConfigRoot = builder.clientConfigRoot;
		this.profileConfigs = Collections.unmodifiableMap(builder.profileConfigs); // unmodifiable
	}
	
	@Override
	public Path getClientConfigRoot() {
		return clientConfigRoot;
	}
	
	@Override
	public Map<String, ProfileConfig> getProfileConfigs() {
		return profileConfigs;
	}

	public static class Builder {
		private final Path clientConfigRoot;
		private final Config config;
		
		private Map<String, ProfileConfig> profileConfigs;
		
		public Builder(Path clientConfigRoot) {
			log.info("Creating ClientConfig: {}", clientConfigRoot);
			this.clientConfigRoot = clientConfigRoot;
			if (!Files.isDirectory(clientConfigRoot)) {
				throw new IllegalStateException("ClientConfigRoot dir does not exist: " + 
						clientConfigRoot.toString());
			}
			Path clientConfigFile = clientConfigRoot.resolve(CONFIG_FILE);
			this.config = new ConfigImpl
					.Builder(clientConfigFile)
					.build();
		}

		public ClientConfig build() {
			profileConfigs = new ProfileConfigImpl.Builder(config).buildAll();
			config.ensureNoUnprocessedKeys();
			return new ClientConfigImpl(this);
		}
	}
}
