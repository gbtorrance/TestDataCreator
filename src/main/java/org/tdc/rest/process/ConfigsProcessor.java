package org.tdc.rest.process;

import java.util.List;

import org.tdc.rest.dto.BookConfigDTO;
import org.tdc.rest.dto.ModelConfigDTO;
import org.tdc.rest.dto.SchemaConfigDTO;

/**
 * Defines server-specific functionality for processing various Config objects 
 * (that is distinct from communication-specific REST functionality).
 */
public interface ConfigsProcessor {
	List<SchemaConfigDTO> getSchemaConfigDTOs();
	List<ModelConfigDTO> getModelConfigDTOs();
	List<BookConfigDTO> getBookConfigDTOs();
}
