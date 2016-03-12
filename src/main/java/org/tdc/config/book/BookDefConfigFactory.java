package org.tdc.config.book;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link BookDefConfig} instances.
 */
public interface BookDefConfigFactory {
	BookDefConfig getBookDefConfig(Addr addr);
}
