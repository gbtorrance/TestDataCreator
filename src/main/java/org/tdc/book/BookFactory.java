package org.tdc.book;

import org.tdc.config.book.BookConfig;

/**
 * Factory for creating {@link Book} instances.
 */
public interface BookFactory {
	Book getBook(BookConfig config);
}
