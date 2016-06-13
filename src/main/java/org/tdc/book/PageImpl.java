package org.tdc.book;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tdc.config.book.PageConfig;
import org.tdc.modelinst.ModelInstFactory;

/**
 * A {@link Page} implementation.
 */
public class PageImpl implements Page {
	
	private final PageConfig config;
	private final ModelInstFactory modelInstFactory;
	
	private PageImpl(PageBuilder builder) {
		this.config = builder.config;
		this.modelInstFactory = builder.modelInstFactory;
	}
	
	@Override
	public String getName() {
		return config.getPageName();
	}
	
	public static class PageBuilder {
		private final Map<String, PageConfig> configs;
		private final ModelInstFactory modelInstFactory;
		
		private PageConfig config;
		
		public PageBuilder(Map<String, PageConfig> configs, ModelInstFactory modelInstFactory) {
			this.configs = configs;
			this.modelInstFactory = modelInstFactory;
		}
		
		public Map<String, Page> buildAll() {
			Map<String, Page> pages = new LinkedHashMap<>();
			Collection<PageConfig> pageConfigs = configs.values(); 
			for (PageConfig pageConfig : pageConfigs) {
				pages.put(pageConfig.getPageName(), build(pageConfig));
			}
			return pages;
		}

		private Page build(PageConfig config) {
			this.config = config;
			return new PageImpl(this);
		}
	}
}
