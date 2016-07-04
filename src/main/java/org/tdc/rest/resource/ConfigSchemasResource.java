package org.tdc.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.rest.dto.SchemaConfigDTO;

/**
 * REST Resource for providing Schema Config info to the client.
 */
@Path("/tdc/config/schemas")
public class ConfigSchemasResource {
	
	private final List<SchemaConfig> schemaConfigs;
	private final List<SchemaConfigDTO> schemaConfigDTOList;
	
	public ConfigSchemasResource(List<SchemaConfig> schemaConfigs) {
		this.schemaConfigs = schemaConfigs;
		this.schemaConfigDTOList = createSchemaConfigDTOList();
	}
	
	private final List<SchemaConfigDTO> createSchemaConfigDTOList() {
		List<SchemaConfigDTO> list = new ArrayList<>();
		for (SchemaConfig schemaConfig : schemaConfigs) {
			String addrStr = schemaConfig.getAddr().toString();
			SchemaConfigDTO dto = new SchemaConfigDTO(addrStr);
			list.add(dto);
		}
		return list;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public List<SchemaConfigDTO> createSampleObjectList() {
		return schemaConfigDTOList;
	}
}
