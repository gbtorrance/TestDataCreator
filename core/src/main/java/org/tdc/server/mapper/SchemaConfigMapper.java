package org.tdc.server.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.shared.dto.AddrErrorDTO;
import org.tdc.shared.dto.SchemaConfigDTO;
import org.tdc.shared.dto.SchemaConfigsDTO;
import org.tdc.util.Addr;

/**
 * MapStruct Mapper to map {@link SchemaConfig} to {@link SchemaConfigDTO}.
 */
@Mapper(uses = UtilMapper.class)
public abstract class SchemaConfigMapper {
	public static SchemaConfigMapper INSTANCE = Mappers.getMapper(SchemaConfigMapper.class);
	
	public SchemaConfigsDTO schemaConfigsToSchemaConfigsDTO(
			List<SchemaConfig> schemaConfigs, Map<Addr, Exception> exceptions) {
		
		List<SchemaConfigDTO> schemaConfigDTOs = toSchemaConfigDTOs(schemaConfigs);
		List<AddrErrorDTO> addrErrorDTOs = UtilMapper.exceptionsToAddrErrorDTOs(exceptions);
		
		SchemaConfigsDTO schemaConfigsDTO = new SchemaConfigsDTO();
		schemaConfigsDTO.setSchemaConfigs(schemaConfigDTOs);
		schemaConfigsDTO.setErrors(addrErrorDTOs);
		return schemaConfigsDTO;
	}
	
	public abstract List<SchemaConfigDTO> toSchemaConfigDTOs(List<SchemaConfig> schemaConfigs);

	@Mapping(source = "addr", target = "schemaAddress")
	public abstract SchemaConfigDTO schemaConfigToSchemaConfigDTO(SchemaConfig schemaConfig);
}
