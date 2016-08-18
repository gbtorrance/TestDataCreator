package org.tdc.config.server;

import java.nio.file.Path;

import org.tdc.rest.TDCServer;

/**
 * Defines getters for configuration items applicable to {@link TDCServer}.
 */
public interface ServerConfig {
	Path getServerConfigRoot();
	Path getSchemasConfigRoot();
	Path getBooksConfigRoot();
	Path getWorkingRoot();
	int getServerPort();
}
