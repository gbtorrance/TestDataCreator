package org.tdc.book;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tdc.config.book.PageConfig;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * A {@link Page} implementation.
 */
public class PageImpl implements Page {
	
	private final PageConfig config;
	private final ModelInstFactory modelInstFactory;
	private final List<TestDoc> testDocs;
	
	private PageImpl(Builder builder) {
		this.config = builder.config;
		this.modelInstFactory = builder.modelInstFactory;
		this.testDocs = Collections.unmodifiableList(builder.testDocs); // unmodifiable
	}

	@Override
	public PageConfig getConfig() {
		return config;
	}
	
	@Override
	public String getName() {
		return config.getPageName();
	}
	
	@Override
	public ModelInst getModelInst() {
		// intentionally not storing a reference to the actual ModelInst;
		// that way we can keep Books/Pages in memory without necessarily
		// keeping the very heavy-weight ModelInsts in memory all the time;
		// allows for better memory management
		return modelInstFactory.getModelInst(config.getModelConfig());
	}
	
	@Override 
	public List<TestDoc> getTestDocs() {
		return testDocs;
	}
	
	public static class Builder {
		private final Map<String, PageConfig> configs;
		private final ModelInstFactory modelInstFactory;
		private final SpreadsheetFile spreadsheetFile;
		
		private PageConfig config;
		private List<TestDoc> testDocs;
		
		public Builder(
				Map<String, PageConfig> configs, 
				ModelInstFactory modelInstFactory, 
				SpreadsheetFile spreadsheetFile) {
			
			this.configs = configs;
			this.modelInstFactory = modelInstFactory;
			this.spreadsheetFile = spreadsheetFile;
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
			testDocs = new TestDocImpl.Builder(config, spreadsheetFile).buildAll();
			return new PageImpl(this);
		}
	}
}
