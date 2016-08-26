package org.tdc.rest.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class TaskResultDTO extends ResultDTO {
	public String taskID;

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
}
