package org.tdc.config.book;

import java.util.List;
import java.util.Map;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link BookConfig} instances.
 */
public interface BookConfigFactory {
	BookConfig getBookConfig(Addr addr);
	List<BookConfig> getAllBookConfigs();
	List<BookConfig> getAllBookConfigs(Map<Addr, Exception> errors);
}
