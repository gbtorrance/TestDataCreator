package org.tdc.message;

/**
 * A Message is used for storing information about a particular item of information, 
 * an error, or a warning that needs to, ultimately, be communicated back to the user. 
 * 
 * <p>They are created, typically, when validating {@link TestDoc}s and {@link TestCase}s.
 */
public class Message {
	private final MessageType type;
	private final String message;
	private final String pageName;
	private final Integer rowNum;
	private final Integer colNum;
	private final String cellRef;
	private final String xpath;
	private final String value;
	
	private Message(Builder builder) {
		this.type = builder.type;
		this.message = builder.message;
		this.pageName = builder.pageName;
		this.rowNum = builder.rowNum;
		this.colNum = builder.colNum;
		this.cellRef = builder.cellRef;
		this.xpath = builder.xpath;
		this.value = builder.value;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getPageName() {
		return pageName;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public Integer getColNum() {
		return colNum;
	}

	public String getCellRef() {
		return cellRef;
	}

	public String getXpath() {
		return xpath;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(type.getLabel());
		if (rowNum != null) {
			sb.append(", row: ").append(rowNum);
		}
		if (colNum != null) {
			sb.append(", col: ").append(colNum);
		}
		if (cellRef != null) {
			sb.append(", cell: ").append(cellRef);
		}
		sb.append("] ");
		sb.append(message);
		sb.append(" ");
		if (value != null) {
			sb.append("[value: ").append(value).append("] ");
		}
		if (xpath != null) {
			sb.append("[xpath: ").append(xpath).append("] ");
		}
		sb.setLength(sb.length()-1);
		return sb.toString();
	}
	
	public static class Builder {
		private final MessageType type;
		private final String message;

		private String pageName;
		private Integer rowNum;
		private Integer colNum;
		private String cellRef;
		private String xpath;
		private String value;

		public Builder(MessageType type, String message) {
			this.type = type;
			this.message = message;
		}
		
		public Builder setPageName(String pageName) {
			this.pageName = pageName;
			return this;
		}
		
		public Builder setRowNum(Integer rowNum) {
			this.rowNum = rowNum;
			return this;
		}

		public Builder setColNum(Integer colNum) {
			this.colNum = colNum;
			return this;
		}
		
		public Builder setRowNumColNum(Integer rowNum, Integer colNum) {
			this.rowNum = rowNum;
			this.colNum = colNum;
			return this;
		}

		public Builder setCellRef(String cellRef) {
			this.cellRef = cellRef;
			return this;
		}

		public Builder setXpath(String xpath) {
			this.xpath = xpath;
			return this;
		}

		public Builder setValue(String value) {
			this.value = value;
			return this;
		}

		public Message build() {
			return new Message(this); 
		}
	}
}
