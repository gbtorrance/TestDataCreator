package org.tdc.server.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.server.process.ServerProcessor;
import org.tdc.shared.dto.SchemaConfigsDTO;

/**
 * REST Resource for providing Schema Config info to the client.
 */
@Path("/tdc/config/schemas")
public class ConfigSchemasResource {
	private final ServerProcessor processor;
	
	public ConfigSchemasResource(ServerProcessor processor) {
		this.processor = processor;
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public SchemaConfigsDTO getConfigs() {
		return processor.getSchemaConfigsDTO();
	}
}
