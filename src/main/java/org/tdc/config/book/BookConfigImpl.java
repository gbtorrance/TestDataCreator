package org.tdc.config.book;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
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
	private final String bookName;
	private final String bookDescription;
	private final Path bookTemplateFile;
	private final Map<String, DocTypeConfig> docTypeConfigs;
	private final Map<String, PageConfig> pageConfigs;
	private final List<FilterConfig> filterConfigs;
	private final List<TaskConfig> taskConfigs;
	private final CellStyle defaultStyle;
	private final CellStyle nodeHeaderStyle; 			// based on defaultStyle
	private final CellStyle defaultNodeStyle; 			// based on defaultStyle
	private final CellStyle parentNodeStyle;			// based on defaultNodeStyle
	private final CellStyle attribNodeStyle;			// based on defaultNodeStyle
	private final CellStyle compositorNodeStyle;		// based on defaultNodeStyle
	private final CellStyle choiceMarkerNodeStyle;		// based on defaultNodeStyle
	private final CellStyle occurMarkerNodeStyle;		// based on defaultNodeStyle
	private final CellStyle nodeDetailHeaderStyle;		// based on defaultStyle
	private final CellStyle defaultNodeDetailStyle;		// based on defaultStyle (parent to detail column styles)
	private final CellStyle docIDRowLabelStyle;			// based on defaultStyle
	private final CellStyle conversionNewRowStyle;		// based on defaultStyle
	private final CellStyle conversionPrevNewRowStyle;	// based on defaultStyle
	private final CellStyle defaultLogStyle;			// based on defaultStyle
	private final CellStyle headerLogStyle;				// based on defaultLogStyle
	private final CellStyle errorLogStyle;				// based on defaultLogStyle
	private final int nodeColumnCount;
	private final int nodeColumnWidth;
	private final int headerRowCount;
	private final String[] nodeHeaderLabels;

	private BookConfigImpl(Builder builder) {
		this.booksConfigRoot = builder.booksConfigRoot;
		this.addr = builder.addr;
		this.bookConfigRoot = builder.bookConfigRoot;
		this.bookName = builder.bookName;
		this.bookDescription = builder.bookDescription;
		this.bookTemplateFile = builder.bookTemplateFile;
		this.docTypeConfigs = Collections.unmodifiableMap(builder.docTypeConfigs); // unmodifiable
		this.pageConfigs = Collections.unmodifiableMap(builder.pageConfigs); // unmodifiable
		this.filterConfigs = Collections.unmodifiableList(builder.filterConfigs); // unmodifiable
		this.taskConfigs = Collections.unmodifiableList(builder.taskConfigs); // unmodifiable
		this.defaultStyle = builder.defaultStyle;
		this.nodeHeaderStyle = builder.nodeHeaderStyle;
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerNodeStyle = builder.choiceMarkerNodeStyle;
		this.occurMarkerNodeStyle = builder.occurMarkerNodeStyle;
		this.nodeDetailHeaderStyle = builder.nodeDetailHeaderStyle;
		this.defaultNodeDetailStyle = builder.defaultNodeDetailStyle;
		this.docIDRowLabelStyle = builder.docIDRowLabelStyle;
		this.conversionNewRowStyle = builder.conversionNewRowStyle;
		this.conversionPrevNewRowStyle = builder.conversionPrevNewRowStyle;
		this.defaultLogStyle = builder.defaultLogStyle;
		this.headerLogStyle = builder.headerLogStyle;
		this.errorLogStyle = builder.errorLogStyle;
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
	public String getBookName() {
		return bookName;
	}

	@Override
	public String getBookDescription() {
		return bookDescription;
	}

	@Override
	public Path getBookTemplateFile() {
		return bookTemplateFile;
	}

	@Override
	public String getBookTemplateFileExtension() {
		if (bookTemplateFile == null) {
			return null;
		}
		String filename = bookTemplateFile.toString();
		return filename.substring(filename.lastIndexOf(".") + 1);
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
	public List<FilterConfig> getFilterConfigs() {
		return filterConfigs;
	}

	@Override
	public List<TaskConfig> getTaskConfigs() {
		return taskConfigs;
	}

	@Override
	public CellStyle getDefaultStyle() {
		return defaultStyle;
	}
	
	@Override
	public CellStyle getNodeHeaderStyle() {
		return nodeHeaderStyle;
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
	public CellStyle getChoiceMarkerNodeStyle() {
		return choiceMarkerNodeStyle;
	}

	@Override
	public CellStyle getOccurMarkerNodeStyle() {
		return occurMarkerNodeStyle;
	}

	@Override
	public CellStyle getNodeDetailHeaderStyle() {
		return nodeDetailHeaderStyle;
	}

	@Override
	public CellStyle getDefaultNodeDetailStyle() {
		return defaultNodeDetailStyle;
	}

	@Override
	public CellStyle getDocIDRowLabelStyle() {
		return docIDRowLabelStyle;
	}

	@Override
	public CellStyle getConversionNewRowStyle() {
		return conversionNewRowStyle;
	}

	@Override
	public CellStyle getConversionPrevNewRowStyle() {
		return conversionPrevNewRowStyle;
	}

	@Override
	public CellStyle getDefaultLogStyle() {
		return defaultLogStyle;
	}

	@Override
	public CellStyle getHeaderLogStyle() {
		return headerLogStyle;
	}

	@Override
	public CellStyle getErrorLogStyle() {
		return errorLogStyle;
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
		private final FilterConfigFactory filterConfigFactory;
		private final TaskConfigFactory taskConfigFactory;
		private final Path booksConfigRoot;
		private final Addr addr;
		private final Path bookConfigRoot;
		
		private String bookName;
		private String bookDescription;
		private Path bookTemplateFile;
		private Map<String, DocTypeConfig> docTypeConfigs;
		private Map<String, PageConfig> pageConfigs;
		private List<FilterConfig> filterConfigs;
		private List<TaskConfig> taskConfigs;
		private CellStyle defaultStyle;
		private CellStyle nodeHeaderStyle;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerNodeStyle;
		private CellStyle occurMarkerNodeStyle;
		private CellStyle nodeDetailHeaderStyle;
		private CellStyle defaultNodeDetailStyle;
		private CellStyle docIDRowLabelStyle;
		private CellStyle conversionNewRowStyle;
		private CellStyle conversionPrevNewRowStyle;
		private CellStyle defaultLogStyle;
		private CellStyle headerLogStyle;
		private CellStyle errorLogStyle;
		private int nodeColumnCount;
		private int nodeColumnWidth;
		private int headerRowCount;
		private String[] nodeHeaderLabels;
		
		public Builder(Path booksConfigRoot, Addr addr, 
				ModelConfigFactory modelConfigFactory, 
				FilterConfigFactory filterConfigFactory,
				TaskConfigFactory taskConfigFactory) {
			
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
			this.filterConfigFactory = filterConfigFactory;
			this.taskConfigFactory = taskConfigFactory;
		}

		public BookConfig build() {
			bookName = config.getString("BookName", null, true);
			bookDescription = config.getString("BookDescription", "", false);
			String templateFileStr = config.getString("BookTemplateFile", null, false);
			bookTemplateFile = templateFileStr == null ? null : bookConfigRoot.resolve(templateFileStr);
			if (bookTemplateFile != null && Files.notExists(bookTemplateFile)) {
				throw new IllegalStateException("BookTemplateFile does not exist: " + bookTemplateFile.toString());
			}
			docTypeConfigs = new DocTypeConfigImpl.Builder(config).buildAll();
			defaultStyle = config.getCellStyle("DefaultStyle", null, true);
			nodeHeaderStyle = config.getCellStyle("NodeHeaderStyle", defaultStyle, false);
			defaultNodeStyle = config.getCellStyle("DefaultNodeStyle", defaultStyle, false);
			parentNodeStyle = config.getCellStyle("ParentNodeStyle", defaultNodeStyle, false);
			attribNodeStyle = config.getCellStyle("AttribNodeStyle", defaultNodeStyle, false);
			compositorNodeStyle = config.getCellStyle("CompositorNodeStyle", defaultNodeStyle, false);
			choiceMarkerNodeStyle = config.getCellStyle("ChoiceMarkerNodeStyle", defaultNodeStyle, false);
			occurMarkerNodeStyle = config.getCellStyle("OccurMarkerNodeStyle", defaultNodeStyle, false);
			nodeDetailHeaderStyle = config.getCellStyle("NodeDetailHeaderStyle", defaultStyle, false);
			defaultNodeDetailStyle = config.getCellStyle("DefaultNodeDetailStyle", defaultStyle, false);
			docIDRowLabelStyle = config.getCellStyle("DocIDRowLabelStyle", defaultStyle, false); 
			conversionNewRowStyle = config.getCellStyle("ConversionNewRowStyle", defaultStyle, false); 
			conversionPrevNewRowStyle = config.getCellStyle("ConversionPrevNewRowStyle", defaultStyle, false); 
			defaultLogStyle = config.getCellStyle("DefaultLogStyle", defaultStyle, false); 
			headerLogStyle = config.getCellStyle("HeaderLogStyle", defaultLogStyle, false); 
			errorLogStyle = config.getCellStyle("ErrorLogStyle", defaultLogStyle, false); 
			nodeColumnCount = config.getInt("NodeColumnCount", 0, true);
			nodeColumnWidth = config.getInt("NodeColumnWidth", 0, true);
			headerRowCount = config.getInt("HeaderRowCount", 1, false);
			nodeHeaderLabels = config.getHeaderLabels(
					"NodeHeaderLabels", headerRowCount);
			pageConfigs = new PageConfigImpl.Builder(config, docTypeConfigs, modelConfigFactory, 
					nodeColumnCount, headerRowCount, defaultNodeDetailStyle).buildAll();
			filterConfigs = filterConfigFactory.createFilterConfigs(
					config, "Filters", bookConfigRoot, addr, bookName);
			taskConfigs = taskConfigFactory.createTaskConfigs(
					config, "Tasks", bookConfigRoot, addr, bookName);
			return new BookConfigImpl(this);
		}
	}
}
