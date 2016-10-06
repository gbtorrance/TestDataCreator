package org.tdc.config.system;

import java.nio.file.Path;

/**
 * Defines getters for configuration items applicable to the system as a whole (i.e. global).
 */
public interface SystemConfig {
	Path getSystemConfigRoot();
	Path getSchemasConfigRoot();
	Path getBooksConfigRoot();
}
