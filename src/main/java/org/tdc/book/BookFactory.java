package org.tdc.book;

import java.nio.file.Path;

/**
 * Factory for creating {@link Book} instances.
 */
public interface BookFactory {
	Book getBook(Path bookFile);
}
