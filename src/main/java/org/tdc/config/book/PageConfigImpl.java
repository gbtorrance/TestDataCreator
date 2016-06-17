package org.tdc.config.book;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.util.Addr;

/**
 * A {@link PageConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class PageConfigImpl implements PageConfig {
	
	private final String pageName;
	private final ModelConfig modelConfig;
	private final DocTypeConfig docTypeConfig;
	
	private PageConfigImpl(PageConfigBuilder builder) {
		this.pageName = builder.pageName;
		this.modelConfig = builder.modelConfig;
		this.docTypeConfig = builder.docTypeConfig;
	}

	@Override
	public String getPageName() {
		return pageName;
	}

	@Override
	public ModelConfig getModelConfig() {
		return modelConfig;
	}
	
	@Override
	public DocTypeConfig getDocTypeConfig() {
		return docTypeConfig;
	}
	
	public static class PageConfigBuilder {
		private static final String CONFIG_PREFIX = "Pages.Page";

		private final XMLConfigWrapper config;
		private final Map<String, DocTypeConfig> docTypeConfigs;

		private String pageName;
		private ModelConfig modelConfig;
		private DocTypeConfig docTypeConfig;
		private ModelConfigFactory modelConfigFactory;
		
		public PageConfigBuilder(XMLConfigWrapper config, 
				Map<String, DocTypeConfig> docTypeConfigs, ModelConfigFactory modelConfigFactory) {
			this.config = config;
			this.docTypeConfigs = docTypeConfigs;
			this.modelConfigFactory = modelConfigFactory;
		}
		
		public Map<String, PageConfig> buildAll() {
			Map<String, PageConfig> map = new LinkedHashMap<>();
			int count = config.getCount(CONFIG_PREFIX);
			for (int i = 0; i < count; i++) {
				PageConfig pageConfig = build(i);
				if (map.containsKey(pageConfig.getPageName())) {
					throw new IllegalStateException("Page with name '" + pageConfig.getPageName() + 
							"' already exists in this Book");
				}
				map.put(pageConfig.getPageName(), pageConfig);
			}
			return map;
		}

		private PageConfig build(int index) {
			String indexPrefix = CONFIG_PREFIX + "(" + index + ").";
			pageName = config.getString(indexPrefix + "PageName", null, true);
			
			Addr modelAddr = new Addr(config.getString(indexPrefix + "ModelAddress", null, true));
			try {
				modelConfig = modelConfigFactory.getModelConfig(modelAddr);
			}
			catch (Exception ex) {
				throw new IllegalStateException("Unable to locate ModelConfig '" + 
						modelAddr.toString() + "' for Page '" + pageName + "'", ex);
			}
			
			String docTypeName = config.getString(indexPrefix + "DocTypeName", null, true);
			docTypeConfig = docTypeConfigs.get(docTypeName);
			if (docTypeConfig == null) {
				throw new IllegalStateException("Unable to locate DocType '" + docTypeName + 
						"' for Page '" + pageName + "'");
			}
			return new PageConfigImpl(this);
		}
	}
}