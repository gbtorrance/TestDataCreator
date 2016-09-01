package org.tdc.task;

import org.tdc.book.Book;
import org.tdc.config.book.TaskConfig;

/**
 * Defines core functionality for custom Tasks that can be performed
 * on {@link Book}s. These Tasks may include such things as rule validation
 * and file generation, exporting, etc..
 */
public interface Task {
	TaskConfig getConfig();
	void process();
}
