package org.tdc.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.config.model.ModelConfig;
import org.tdc.rest.dto.ModelConfigDTO;

/**
 * REST Resource for providing Model Config info to the client.
 */
@Path("/tdc/config/models")
public class ConfigModelsResource {
	
	private final List<ModelConfig> modelConfigs;
	private final List<ModelConfigDTO> modelConfigDTOList;
	
	public ConfigModelsResource(List<ModelConfig> modelConfigs) {
		this.modelConfigs = modelConfigs;
		this.modelConfigDTOList = createModelConfigDTOList();
	}
	
	private final List<ModelConfigDTO> createModelConfigDTOList() {
		List<ModelConfigDTO> list = new ArrayList<>();
		for (ModelConfig modelConfig : modelConfigs) {
			String addrStr = modelConfig.getAddr().toString();
			ModelConfigDTO dto = new ModelConfigDTO(addrStr);
			list.add(dto);
		}
		return list;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public List<ModelConfigDTO> createSampleObjectList() {
		return modelConfigDTOList;
	}
}
