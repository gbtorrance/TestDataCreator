package org.tdc.server.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.server.process.ServerProcessor;
import org.tdc.shared.dto.ModelConfigsDTO;

/**
 * REST Resource for providing Model Config info to the client.
 */
@Path("/tdc/config/models")
public class ConfigModelsResource {
	private final ServerProcessor processor;
	
	public ConfigModelsResource(ServerProcessor processor) {
		this.processor = processor;
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public ModelConfigsDTO getConfigs() {
		return processor.getModelConfigsDTO();
	}
}
