package org.tdc.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.config.book.BookConfig;
import org.tdc.rest.dto.BookConfigDTO;

/**
 * REST Resource for providing Book Config info to the client.
 */
@Path("/tdc/config/books")
public class ConfigBooksResource {
	
	private final List<BookConfig> bookConfigs;
	private final List<BookConfigDTO> bookConfigDTOList;
	
	public ConfigBooksResource(List<BookConfig> bookConfigs) {
		this.bookConfigs = bookConfigs;
		this.bookConfigDTOList = createBookConfigDTOList();
	}
	
	private final List<BookConfigDTO> createBookConfigDTOList() {
		List<BookConfigDTO> list = new ArrayList<>();
		for (BookConfig bookConfig : bookConfigs) {
			String addrStr = bookConfig.getAddr().toString();
			BookConfigDTO dto = new BookConfigDTO(addrStr);
			list.add(dto);
		}
		return list;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public List<BookConfigDTO> createSampleObjectList() {
		return bookConfigDTOList;
	}
}
