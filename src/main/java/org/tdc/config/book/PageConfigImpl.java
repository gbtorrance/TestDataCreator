package org.tdc.config.book;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.util.Addr;

/**
 * A {@link PageConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class PageConfigImpl implements PageConfig {
	
	private final String pageName;
	private final Addr modelAddr;
	private final DocTypeConfig docTypeConfig;
	
	private PageConfigImpl(PageConfigBuilder builder) {
		this.pageName = builder.pageName;
		this.modelAddr = builder.modelAddr;
		this.docTypeConfig = builder.docTypeConfig;
	}

	@Override
	public String getPageName() {
		return pageName;
	}

	@Override
	public Addr getModelAddr() {
		return modelAddr;
	}
	
	@Override
	public DocTypeConfig getDocTypeConfig() {
		return docTypeConfig;
	}
	
	public static class PageConfigBuilder {
		private static final String CONFIG_PREFIX = "Page";

		private final XMLConfigWrapper config;
		private final Map<String, DocTypeConfig> docTypeConfigs;

		private String pageName;
		private Addr modelAddr;
		private DocTypeConfig docTypeConfig;
		
		public PageConfigBuilder(XMLConfigWrapper config, Map<String, DocTypeConfig> docTypeConfigs) {
			this.config = config;
			this.docTypeConfigs = docTypeConfigs;
		}
		
		public Map<String, PageConfig> buildAll() {
			Map<String, PageConfig> map = new LinkedHashMap<>();
			int count = config.getCount(CONFIG_PREFIX);
			for (int i = 0; i < count; i++) {
				PageConfig pageConfig = build(i);
				map.put(pageConfig.getPageName(), pageConfig);
			}
			return map;
		}

		private PageConfig build(int index) {
			String indexPrefix = CONFIG_PREFIX + "(" + index + ").";
			pageName = config.getString(indexPrefix + "PageName", null, true);
			modelAddr = new Addr(config.getString(indexPrefix + "ModelAddress", null, true));
			String docTypeName = config.getString(indexPrefix + "DocTypeName", null, true);
			docTypeConfig = docTypeConfigs.get(docTypeName);
			if (docTypeConfig == null) {
				throw new IllegalStateException("Unable to locate DocType '" + docTypeName + "' for Page '" + pageName + "'");
			}
			return new PageConfigImpl(this);
		}
	}
}
