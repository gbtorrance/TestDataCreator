package org.tdc.server.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.server.process.ServerProcessor;
import org.tdc.shared.dto.ServerInfoDTO;

/**
 * REST Resource for providing general server info to the client.
 */
@Path("/tdc")
public class ServerResource {
	private final ServerProcessor processor;
	
	public ServerResource(ServerProcessor processor) {
		this.processor = processor;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public ServerInfoDTO getServerInfo() {
		return processor.getServerInfoDTO();
	}
}
