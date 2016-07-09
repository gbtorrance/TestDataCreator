package org.tdc.rest.process;

import java.io.File;

import org.tdc.rest.resource.BooksResource;

/**
 * Defines server-specific functionality for processing Books 
 * (that is distinct from communication-specific REST functionality,
 * that will be handled by {@link BooksResource}).
 */
public interface BooksProcessor {
	int uploadBookFile(File bookFile);
}
