package org.tdc.task;

import java.util.ArrayList;
import java.util.List;

import org.tdc.book.Book;
import org.tdc.config.book.TaskConfig;

/**
 * Responsible for processing, in sequence, a series of {@link Task}s
 * against a particular {@link Book}.
 */
public class TaskProcessor {
	private final Book book;
	private final List<Task> tasks;
	
	private TaskProcessor(Builder builder) {
		this.book = builder.book;
		this.tasks = builder.tasks;
	}
	
	public void processTasks() {
		for (Task task : tasks) {
			processTask(task);
		}
	}
	
	private void processTask(Task task) {
		task.process();
	}

	public static class Builder {
		private final TaskFactory taskFactory;
		private final Book book;
		
		private List<Task> tasks;
		
		public Builder(TaskFactory taskFactory, Book book) {
			this.taskFactory = taskFactory;
			this.book = book;
		}
		
		public TaskProcessor build() {
			this.tasks = createTasks();
			return new TaskProcessor(this);
		}
		
		private List<Task> createTasks() {
			List<Task> list = new ArrayList<>();
			List<TaskConfig> taskConfigs = book.getConfig().getTaskConfigs();
			for (TaskConfig taskConfig : taskConfigs) {
				Task task = taskFactory.createTask(taskConfig, book);
				list.add(task);
			}
			return list;
		}
	}
}
