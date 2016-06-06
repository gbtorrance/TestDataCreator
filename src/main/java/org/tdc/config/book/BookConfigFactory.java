package org.tdc.config.book;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link BookConfig} instances.
 */
public interface BookConfigFactory {
	BookConfig getBookConfig(Addr addr);
}
