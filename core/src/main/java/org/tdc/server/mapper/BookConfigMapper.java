package org.tdc.server.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.tdc.config.book.BookConfig;
import org.tdc.shared.dto.AddrErrorDTO;
import org.tdc.shared.dto.BookConfigDTO;
import org.tdc.shared.dto.BookConfigsDTO;
import org.tdc.util.Addr;

/**
 * MapStruct Mapper to map {@link BookConfig} to {@link BookConfigDTO}.
 */
@Mapper(uses = UtilMapper.class)
public abstract class BookConfigMapper {
	public static BookConfigMapper INSTANCE = Mappers.getMapper(BookConfigMapper.class);
	
	public BookConfigsDTO bookConfigsToBookConfigsDTO(
			List<BookConfig> bookConfigs, Map<Addr, Exception> exceptions) {
		
		List<BookConfigDTO> bookConfigDTOs = toBookConfigDTOs(bookConfigs);
		List<AddrErrorDTO> addrErrorDTOs = UtilMapper.exceptionsToAddrErrorDTOs(exceptions);
		
		BookConfigsDTO bookConfigsDTO = new BookConfigsDTO();
		bookConfigsDTO.setBookConfigs(bookConfigDTOs);
		bookConfigsDTO.setErrors(addrErrorDTOs);
		return bookConfigsDTO;
	}
	
	public abstract List<BookConfigDTO> toBookConfigDTOs(List<BookConfig> bookConfigs);
	
	@Mapping(source = "addr", target = "bookAddress")
	public abstract BookConfigDTO bookConfigToBookConfigDTO(BookConfig bookConfig);
}
