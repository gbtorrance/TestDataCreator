package org.tdc.config.book;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.util.Config;
import org.tdc.spreadsheet.CellStyle;
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
	private final List<PageNodeDetailColumnConfig> nodeDetailColumnConfigs;
	private final List<DocIDRowConfig> docIDRowConfigs;
	private final DocIDRowConfig caseNumDocIDRowConfig;
	private final DocIDRowConfig setNameDocIDRowConfig;
	private final List<DocIDRowConfig> varDocIDRowConfigs;
	private final int docIDRowStart;
	private final int docIDRowLabelCol;
	private final int headerRowStart;
	private final int nodeRowStart;
	private final int nodeColStart;
	private final int nodeDetailColStart;
	private final int testDocColStart;
	
	private PageConfigImpl(Builder builder) {
		this.pageName = builder.pageName;
		this.modelConfig = builder.modelConfig;
		this.docTypeConfig = builder.docTypeConfig;
		this.nodeDetailColumnConfigs = Collections.unmodifiableList(builder.nodeDetailColumnConfigs); // unmodifiable
		this.docIDRowConfigs = Collections.unmodifiableList(builder.docIDRowConfigs); // unmodifiable
		this.caseNumDocIDRowConfig = builder.caseNumDocIDRowConfig;
		this.setNameDocIDRowConfig = builder.setNameDocIDRowConfig;
		this.varDocIDRowConfigs = Collections.unmodifiableList(builder.varDocIDRowConfigs); // unmodifiable
		this.docIDRowStart = builder.docIDRowStart;
		this.docIDRowLabelCol = builder.docIDRowLabelCol;
		this.headerRowStart = builder.headerRowStart;
		this.nodeRowStart = builder.nodeRowStart;
		this.nodeColStart = builder.nodeColStart;
		this.nodeDetailColStart = builder.nodeDetailColStart;
		this.testDocColStart = builder.testDocColStart;
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
	
	@Override
	public List<PageNodeDetailColumnConfig> getNodeDetailColumnConfigs() {
		return nodeDetailColumnConfigs;
	}
	
	@Override
	public List<DocIDRowConfig> getDocIDRowConfigs() {
		return docIDRowConfigs;
	}
	
	@Override
	public DocIDRowConfig getCaseNumDocIDRowConfig() {
		return caseNumDocIDRowConfig; 
	}
	
	@Override
	public DocIDRowConfig getSetNameDocIDRowConfig() {
		return setNameDocIDRowConfig; 
	}
	
	@Override
	public List<DocIDRowConfig> getVarDocIDRowConfigs() {
		return varDocIDRowConfigs; 
	}
	
	@Override
	public int getDocIDRowStart() {
		return docIDRowStart;
	}
	
	@Override
	public int getDocIDRowLabelCol() {
		return docIDRowLabelCol;
	}

	@Override
	public int getHeaderRowStart() {
		return headerRowStart;
	}

	@Override
	public int getNodeRowStart() {
		return nodeRowStart;
	}

	@Override
	public int getNodeColStart() {
		return nodeColStart;
	}

	@Override
	public int getNodeDetailColStart() {
		return nodeDetailColStart;
	}

	@Override
	public int getTestDocColStart() {
		return testDocColStart;
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = "Pages.Page";

		private final Config config;
		private final Map<String, DocTypeConfig> docTypeConfigs;
		private final ModelConfigFactory modelConfigFactory;
		private final int headerRowCount;
		private final CellStyle defaultNodeDetailColumnStyle;
		private final int docIDRowStart;
		private final int docIDRowLabelCol;
		private final int nodeColStart;
		private final int nodeDetailColStart;

		private String pageName;
		private ModelConfig modelConfig;
		private DocTypeConfig docTypeConfig;
		private List<PageNodeDetailColumnConfig> nodeDetailColumnConfigs;
		private List<DocIDRowConfig> docIDRowConfigs;
		private DocIDRowConfig caseNumDocIDRowConfig;
		private DocIDRowConfig setNameDocIDRowConfig;
		private List<DocIDRowConfig> varDocIDRowConfigs;
		private int headerRowStart;
		private int nodeRowStart;
		private int testDocColStart;
		
		public Builder(Config config, 
				Map<String, DocTypeConfig> docTypeConfigs, ModelConfigFactory modelConfigFactory,
				int nodeColumnCount, int headerRowCount, CellStyle defaultNodeDetailColumnStyle) {
			this.config = config;
			this.docTypeConfigs = docTypeConfigs;
			this.modelConfigFactory = modelConfigFactory;
			this.headerRowCount = headerRowCount;
			this.defaultNodeDetailColumnStyle = defaultNodeDetailColumnStyle;
			this.docIDRowStart = 1;
			this.docIDRowLabelCol = 1;
			this.nodeColStart = 1;
			this.nodeDetailColStart = nodeColStart + nodeColumnCount;
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
			String indexPrefix = CONFIG_PREFIX + "(" + index + ")";
			pageName = config.getString(indexPrefix + ".PageName", null, true);
			
			Addr modelAddr = new Addr(config.getString(indexPrefix + ".ModelAddress", null, true));
			try {
				modelConfig = modelConfigFactory.getModelConfig(modelAddr);
			}
			catch (Exception ex) {
				throw new IllegalStateException("Unable to locate ModelConfig '" + 
						modelAddr.toString() + "' for Page '" + pageName + "'", ex);
			}
			
			String docTypeName = config.getString(indexPrefix + ".DocTypeName", null, true);
			docTypeConfig = docTypeConfigs.get(docTypeName);
			if (docTypeConfig == null) {
				throw new IllegalStateException("Unable to locate DocType '" + docTypeName + 
						"' for Page '" + pageName + "'");
			}

			nodeDetailColumnConfigs = new PageNodeDetailColumnConfigImpl.Builder(
					config, indexPrefix, headerRowCount, 
					defaultNodeDetailColumnStyle, nodeDetailColStart).buildAll();

			docIDRowConfigs = new DocIDRowConfigImpl.Builder(config, indexPrefix, docIDRowStart).buildAll();
			processDocIDRowConfigs();

			headerRowStart = docIDRowStart + docIDRowConfigs.size();
			nodeRowStart = headerRowStart + headerRowCount;
			testDocColStart = nodeDetailColStart + nodeDetailColumnConfigs.size();
			
			return new PageConfigImpl(this);
		}

		private void processDocIDRowConfigs() {
			caseNumDocIDRowConfig = docIDRowConfigs.stream()
					.filter(docIDRowConfig -> docIDRowConfig.getType() == DocIDType.CASE_NUM)
					.findFirst().get(); // guaranteed to have CASE_NUM row
			setNameDocIDRowConfig = docIDRowConfigs.stream()
					.filter(docIDRowConfig -> docIDRowConfig.getType() == DocIDType.SET_NAME)
					.findFirst().orElse(null); // SET_NAME may not exist
			varDocIDRowConfigs = docIDRowConfigs.stream()
					.filter(docIDRowConfig -> docIDRowConfig.getType().isVariableType())
					.collect(Collectors.toList()); // collect all variable rows
		}
	}
}
