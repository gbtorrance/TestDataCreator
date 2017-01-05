package org.tdc.server.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.tdc.config.model.ModelConfig;
import org.tdc.shared.dto.AddrErrorDTO;
import org.tdc.shared.dto.ModelConfigDTO;
import org.tdc.shared.dto.ModelConfigsDTO;
import org.tdc.util.Addr;

/**
 * MapStruct Mapper to map {@link ModelConfig} to {@link ModelConfigDTO}.
 */
@Mapper(uses = UtilMapper.class)
public abstract class ModelConfigMapper {
	public static ModelConfigMapper INSTANCE = Mappers.getMapper(ModelConfigMapper.class);
	
	public ModelConfigsDTO modelConfigsToModelConfigsDTO(
			List<ModelConfig> modelConfigs, Map<Addr, Exception> exceptions) {
		
		List<ModelConfigDTO> modelConfigDTOs = toModelConfigDTOs(modelConfigs);
		List<AddrErrorDTO> addrErrorDTOs = UtilMapper.exceptionsToAddrErrorDTOs(exceptions);
		
		ModelConfigsDTO modelConfigsDTO = new ModelConfigsDTO();
		modelConfigsDTO.setModelConfigs(modelConfigDTOs);
		modelConfigsDTO.setErrors(addrErrorDTOs);
		return modelConfigsDTO;
	}
	
	public abstract List<ModelConfigDTO> toModelConfigDTOs(List<ModelConfig> modelConfigs);
	
	@Mapping(source = "addr", target = "modelAddress")
	public abstract ModelConfigDTO modelConfigToModelConfigDTO(ModelConfig modelConfig);
}
