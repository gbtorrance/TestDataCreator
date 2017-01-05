package org.tdc.server.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.tdc.shared.dto.AddrErrorDTO;
import org.tdc.util.Addr;
import org.tdc.util.Util;

/**
 * MapStruct Mapper containing various helper methods.
 */
@Mapper
public class UtilMapper {
	public String addressToString(Addr addr) {
		return addr.toString();
	}
	
	public <T> T optionalResultToResult(Optional<T> optional) {
		return optional.isPresent() ? optional.get() : null;
	}
	
	public static List<AddrErrorDTO> exceptionsToAddrErrorDTOs(Map<Addr, Exception> exceptions) {
		List<AddrErrorDTO> errors = new ArrayList<>();
		for (Addr addr : exceptions.keySet()) {
			String errorMessage = Util.getAllMessages(exceptions.get(addr));
			AddrErrorDTO addrErrorDTO = new AddrErrorDTO(addr.toString(), errorMessage);
			errors.add(addrErrorDTO);
		}
		return errors;
	}
}
