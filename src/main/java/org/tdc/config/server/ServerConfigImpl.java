package org.tdc.config.server;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;

/**
 * A {@link ServerConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ServerConfigImpl implements ServerConfig {
	public static final String CONFIG_FILE = "TDCServerConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(ServerConfigImpl.class);
	private static final String BOOKS_WORKING_DIR = "Books";

	private final Path serverConfigRoot;
	private final Path schemasConfigRoot;
	private final Path booksConfigRoot;
	private final Path workingRoot;
	private final Path booksWorkingRoot;
	private final int serverPort;
	
	private ServerConfigImpl(Builder builder) {
		this.serverConfigRoot = builder.serverConfigRoot;
		this.schemasConfigRoot = builder.schemasConfigRoot;
		this.booksConfigRoot = builder.booksConfigRoot;
		this.workingRoot = builder.workingRoot;
		this.booksWorkingRoot = builder.booksWorkingRoot;
		this.serverPort = builder.serverPort;
	}
	
	@Override
	public Path getServerConfigRoot() {
		return serverConfigRoot;
	}
	
	@Override
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}

	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	@Override
	public Path getWorkingRoot() {
		return workingRoot;
	}
	
	@Override
	public Path getBooksWorkingRoot() {
		return booksWorkingRoot;
	}
	
	@Override
	public int getServerPort() {
		return serverPort;
	}

	public static class Builder {
		private final XMLConfigWrapper config;
		private final Path serverConfigRoot;
		
		private Path schemasConfigRoot;
		private Path booksConfigRoot;
		private Path workingRoot;
		private Path booksWorkingRoot;
		private int serverPort;
		
		public Builder(Path serverConfigRoot) {
			log.info("Creating ServerConfig: {}", serverConfigRoot);
			this.serverConfigRoot = serverConfigRoot;
			if (!Files.isDirectory(serverConfigRoot)) {
				throw new IllegalStateException("ServerConfigRoot dir does not exist: " + serverConfigRoot.toString());
			}
			Path serverConfigFile = serverConfigRoot.resolve(CONFIG_FILE);
			this.config = new XMLConfigWrapper(serverConfigFile);
		}

		public ServerConfig build() {
			schemasConfigRoot = serverConfigRoot.resolve(config.getString("SchemasConfigRoot", null, true));
			if (!Files.isDirectory(schemasConfigRoot)) {
				throw new IllegalStateException("SchemasConfigRoot dir does not exist: " + schemasConfigRoot.toString());
			}
			booksConfigRoot = serverConfigRoot.resolve(config.getString("BooksConfigRoot", null, true));
			if (!Files.isDirectory(booksConfigRoot)) {
				throw new IllegalStateException("BooksConfigRoot dir does not exist: " + booksConfigRoot.toString());
			}
			workingRoot = serverConfigRoot.resolve(config.getString("WorkingRoot", null, true));
			if (!Files.isDirectory(workingRoot)) {
				throw new IllegalStateException("WorkingRoot dir does not exist: " + workingRoot.toString());
			}
			booksWorkingRoot = workingRoot.resolve(BOOKS_WORKING_DIR); // will be created as needed
			serverPort = config.getInt("ServerPort", 0, true);
			return new ServerConfigImpl(this);
		}
	}
}
