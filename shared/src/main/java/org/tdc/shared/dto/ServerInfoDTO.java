package org.tdc.shared.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class ServerInfoDTO {
	private String serverStartTime;
	
	public String getServerStartTime() {
		return serverStartTime;
	}

	public void setServerStartTime(String serverStartTime) {
		this.serverStartTime = serverStartTime;
	}
}
