package org.tdc.server.process;

import java.io.File;
import java.util.List;

import org.tdc.shared.dto.BookConfigDTO;
import org.tdc.shared.dto.BookDTO;
import org.tdc.shared.dto.ModelConfigDTO;
import org.tdc.shared.dto.SchemaConfigDTO;

public interface ServerProcessor {
	List<SchemaConfigDTO> getSchemaConfigDTOs();
	List<ModelConfigDTO> getModelConfigDTOs();
	List<BookConfigDTO> getBookConfigDTOs();
	String uploadBookFile(File bookFile);
	BookDTO getBookInfo(String bookID);
}
