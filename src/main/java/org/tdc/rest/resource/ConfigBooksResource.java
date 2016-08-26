package org.tdc.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.rest.dto.BookConfigDTO;
import org.tdc.rest.process.ConfigsProcessor;

/**
 * REST Resource for providing Book Config info to the client.
 */
@Path("/tdc/config/books")
public class ConfigBooksResource {
	private final ConfigsProcessor processor;
	
	public ConfigBooksResource(ConfigsProcessor processor) {
		this.processor = processor;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public List<BookConfigDTO> getConfigs() {
		return processor.getBookConfigDTOs();
	}
}
