package org.tdc.server.process;

import java.io.File;

import org.tdc.shared.dto.BookConfigsDTO;
import org.tdc.shared.dto.BookDTO;
import org.tdc.shared.dto.ModelConfigsDTO;
import org.tdc.shared.dto.SchemaConfigsDTO;
import org.tdc.shared.dto.ServerInfoDTO;

/**
 * Defines core server operations.
 */
public interface ServerProcessor {
	ServerInfoDTO getServerInfoDTO();
	SchemaConfigsDTO getSchemaConfigsDTO();
	ModelConfigsDTO getModelConfigsDTO();
	BookConfigsDTO getBookConfigsDTO();
	String uploadBookFile(File bookFile);
	BookDTO getBookInfo(String bookID);
}
