package org.tdc.rest.process;

import java.util.Collections;
import java.util.List;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.rest.dto.BookConfigDTO;
import org.tdc.rest.dto.ModelConfigDTO;
import org.tdc.rest.dto.SchemaConfigDTO;
import org.tdc.rest.mapper.BookConfigMapper;
import org.tdc.rest.mapper.ModelConfigMapper;
import org.tdc.rest.mapper.SchemaConfigMapper;

/**
 * Implementation of {@link ConfigsProcessor}.
 */
public class ConfigsProcessorImpl implements ConfigsProcessor {
	private final List<SchemaConfig> schemaConfigs;
	private final List<ModelConfig> modelConfigs;
	private final List<BookConfig> bookConfigs;
	private final List<SchemaConfigDTO> schemaConfigDTOs;
	private final List<ModelConfigDTO> modelConfigDTOs;
	private final List<BookConfigDTO> bookConfigDTOs;
	
	private ConfigsProcessorImpl(Builder builder) {
		this.schemaConfigs = builder.schemaConfigs;
		this.modelConfigs = builder.modelConfigs;
		this.bookConfigs = builder.bookConfigs;
		this.schemaConfigDTOs = builder.schemaConfigDTOs;
		this.modelConfigDTOs = builder.modelConfigDTOs;
		this.bookConfigDTOs = builder.bookConfigDTOs;
	}
	
	@Override
	public List<SchemaConfigDTO> getSchemaConfigDTOs() {
		return schemaConfigDTOs;
	}

	@Override
	public List<ModelConfigDTO> getModelConfigDTOs() {
		return modelConfigDTOs;
	}

	@Override
	public List<BookConfigDTO> getBookConfigDTOs() {
		return bookConfigDTOs;
	}
	
	public static class Builder {
		private final SchemaConfigFactory schemaConfigFactory;
		private final ModelConfigFactory modelConfigFactory;
		private final BookConfigFactory bookConfigFactory;
		
		private List<SchemaConfig> schemaConfigs;
		private List<ModelConfig> modelConfigs;
		private List<BookConfig> bookConfigs;
		private List<SchemaConfigDTO> schemaConfigDTOs;
		private List<ModelConfigDTO> modelConfigDTOs;
		private List<BookConfigDTO> bookConfigDTOs;
		
		public Builder(
				SchemaConfigFactory schemaConfigFactory,
				ModelConfigFactory modelConfigFactory,
				BookConfigFactory bookConfigFactory) {
			
			this.schemaConfigFactory = schemaConfigFactory;
			this.modelConfigFactory = modelConfigFactory;
			this.bookConfigFactory = bookConfigFactory;
		}
		
		public ConfigsProcessor build() {
			schemaConfigs = schemaConfigFactory.getAllSchemaConfigs(null); // TODO handle error list
			modelConfigs = modelConfigFactory.getAllModelConfigs(null); // TODO handle error list
			bookConfigs = bookConfigFactory.getAllBookConfigs(null); // TODO handle error list
			schemaConfigDTOs = SchemaConfigMapper.INSTANCE.schemaConfigsToSchemaConfigDTOs(schemaConfigs);
			modelConfigDTOs = ModelConfigMapper.INSTANCE.modelConfigsToModelConfigDTOs(modelConfigs);
			bookConfigDTOs = BookConfigMapper.INSTANCE.bookConfigsToBookConfigDTOs(bookConfigs);
			schemaConfigs = Collections.unmodifiableList(schemaConfigs); // unmodifiable
			modelConfigs = Collections.unmodifiableList(modelConfigs); // unmodifiable
			bookConfigs = Collections.unmodifiableList(bookConfigs); // unmodifiable
			schemaConfigDTOs = Collections.unmodifiableList(schemaConfigDTOs); // unmodifiable
			modelConfigDTOs = Collections.unmodifiableList(modelConfigDTOs); // unmodifiable
			bookConfigDTOs = Collections.unmodifiableList(bookConfigDTOs); // unmodifiable
			return new ConfigsProcessorImpl(this);
		}
	}
}
