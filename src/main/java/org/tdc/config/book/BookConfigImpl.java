package org.tdc.config.book;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.util.Addr;

/**
 * A {@link BookConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class BookConfigImpl implements BookConfig {
	
	public static final String CONFIG_FILE = "TDCBookConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(BookConfigImpl.class);

	private final Path booksConfigRoot;
	private final Addr addr;
	private final Path bookConfigRoot;
	private final Map<String, DocTypeConfig> docTypeConfigs;
	private final Map<String, PageConfig> pageConfigs;
	private final CellStyle defaultStyle;
	private final CellStyle defaultHeaderStyle;
	private final CellStyle defaultNodeDetailColumnStyle;
	private final CellStyle defaultNodeStyle;
	private final CellStyle parentNodeStyle;
	private final CellStyle attribNodeStyle;
	private final CellStyle compositorNodeStyle;
	private final CellStyle choiceMarkerStyle;
	private final CellStyle occurMarkerStyle;
	private final int nodeColumnCount;
	private final int nodeColumnWidth;
	private final int headerRowCount;
	private final String[] nodeHeaderLabels;
	
	private BookConfigImpl(Builder builder) {
		this.booksConfigRoot = builder.booksConfigRoot;
		this.addr = builder.addr;
		this.bookConfigRoot = builder.bookConfigRoot;
		this.docTypeConfigs = Collections.unmodifiableMap(builder.docTypeConfigs); // unmodifiable
		this.pageConfigs = Collections.unmodifiableMap(builder.pageConfigs); // unmodifiable
		this.defaultStyle = builder.defaultStyle;
		this.defaultHeaderStyle = builder.defaultHeaderStyle;
		this.defaultNodeDetailColumnStyle = builder.defaultNodeDetailColumnStyle;
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerStyle = builder.choiceMarkerStyle;
		this.occurMarkerStyle = builder.occurMarkerStyle;
		this.nodeColumnCount = builder.nodeColumnCount;
		this.nodeColumnWidth = builder.nodeColumnWidth;
		this.headerRowCount = builder.headerRowCount;
		this.nodeHeaderLabels = builder.nodeHeaderLabels;
	}
	
	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	@Override
	public Addr getAddr() {
		return addr;
	}

	@Override
	public Path getBookConfigRoot() {
		return bookConfigRoot;
	}
	
	@Override
	public Map<String, DocTypeConfig> getDocTypeConfigs() {
		return docTypeConfigs;
	}

	@Override
	public Map<String, PageConfig> getPageConfigs() {
		return pageConfigs;
	}
	
	@Override
	public CellStyle getDefaultStyle() {
		return defaultStyle;
	}
	
	@Override
	public CellStyle getDefaultHeaderStyle() {
		return defaultHeaderStyle;
	}
	
	@Override
	public CellStyle getDefaultNodeDetailColumnStyle() {
		return defaultNodeDetailColumnStyle;
	}
	
	@Override
	public CellStyle getDefaultNodeStyle() {
		return defaultNodeStyle;
	}
	
	@Override
	public CellStyle getParentNodeStyle() {
		return parentNodeStyle;
	}
	
	@Override
	public CellStyle getAttribNodeStyle() {
		return attribNodeStyle;
	}
	
	@Override
	public CellStyle getCompositorNodeStyle() {
		return compositorNodeStyle;
	}
	
	@Override
	public CellStyle getChoiceMarkerStyle() {
		return choiceMarkerStyle;
	}

	@Override
	public CellStyle getOccurMarkerStyle() {
		return occurMarkerStyle;
	}

	@Override
	public int getNodeColumnCount() {
		return nodeColumnCount;
	}

	@Override
	public int getNodeColumnWidth() {
		return nodeColumnWidth;
	}

	@Override
	public int getHeaderRowCount() {
		return headerRowCount;
	}
	
	@Override
	public String getNodeHeaderLabel(int headerRowNum) {
		return nodeHeaderLabels[headerRowNum-1];
	}

	public static class Builder {
		private final XMLConfigWrapper config;
		private final ModelConfigFactory modelConfigFactory;
		private final Path booksConfigRoot;
		private final Addr addr;
		private final Path bookConfigRoot;
		
		private Map<String, DocTypeConfig> docTypeConfigs;
		private Map<String, PageConfig> pageConfigs;
		private CellStyle defaultStyle;
		private CellStyle defaultHeaderStyle;
		private CellStyle defaultNodeDetailColumnStyle;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerStyle;
		private CellStyle occurMarkerStyle;
		private int nodeColumnCount;
		private int nodeColumnWidth;
		private int headerRowCount;
		private String[] nodeHeaderLabels;
		
		public Builder(Path booksConfigRoot, Addr addr, ModelConfigFactory modelConfigFactory) {
			log.info("Creating BookConfig: {}", addr);
			this.booksConfigRoot = booksConfigRoot;
			this.addr = addr;
			this.bookConfigRoot = booksConfigRoot.resolve(addr.getPath());
			if (!Files.isDirectory(bookConfigRoot)) {
				throw new IllegalStateException("BookConfig root dir does not exist: " + bookConfigRoot.toString());
			}
			Path bookConfigFile = bookConfigRoot.resolve(CONFIG_FILE);
			this.config = new XMLConfigWrapper(bookConfigFile);
			this.modelConfigFactory = modelConfigFactory;
		}

		public BookConfig build() {
			docTypeConfigs = new DocTypeConfigImpl.Builder(config).buildAll();
			defaultStyle = config.getCellStyle("DefaultStyle", null, true);
			defaultHeaderStyle = config.getCellStyle("DefaultHeaderStyle", defaultStyle, false);
			defaultNodeDetailColumnStyle = config.getCellStyle("DefaultNodeDetailColumnStyle", defaultStyle, false);
			defaultNodeStyle = config.getCellStyle("DefaultNodeStyle", defaultStyle, false);
			parentNodeStyle = config.getCellStyle("ParentNodeStyle", defaultNodeStyle, false);
			attribNodeStyle = config.getCellStyle("AttribNodeStyle", defaultNodeStyle, false);
			compositorNodeStyle = config.getCellStyle("CompositorNodeStyle", defaultNodeStyle, false);
			choiceMarkerStyle = config.getCellStyle("ChoiceMarkerStyle", defaultNodeStyle, false);
			occurMarkerStyle = config.getCellStyle("OccurMarkerStyle", defaultNodeStyle, false);
			nodeColumnCount = config.getInt("NodeColumnCount", 0, true);
			nodeColumnWidth = config.getInt("NodeColumnWidth", 0, true);
			headerRowCount = config.getInt("HeaderRowCount", 1, false);
			nodeHeaderLabels = config.getHeaderLabels(
					"NodeHeaderLabels", headerRowCount);
			pageConfigs = new PageConfigImpl.Builder(config, docTypeConfigs, modelConfigFactory, 
					nodeColumnCount, headerRowCount, defaultNodeDetailColumnStyle).buildAll();
			return new BookConfigImpl(this);
		}
	}
}
