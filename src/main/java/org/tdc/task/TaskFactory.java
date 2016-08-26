package org.tdc.task;

import org.tdc.book.Book;
import org.tdc.config.book.TaskConfig;

/**
 * Interface defining a factory for creating {@link Task} instances 
 * based on {@link TaskConfig} parameters.
 */
public interface TaskFactory {
	Task createTask(TaskConfig config, Book book);
}