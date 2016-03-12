package org.tdc.modelinst;

/**
 * A {@link AbstractNodeInst} implementation specific to notes (that can be displayed above or below nodes in a {@link ModelInst}).
 * 
 * <p>--PLACEHOLDER FOR FUTURE USE--
 */
public class NoteInst extends AbstractNodeInst {
	
	private String text;
	private NoteFormat format;

	public NoteInst(NodeInst parentNodeInst, String text, NoteFormat format) {
		super(parentNodeInst);
		this.text = text;
		this.format = format;
	}
	
	public String getText() {
		return text;
	}
	
	public NoteFormat getNoteFormat() {
		return format;
	}
	
	@Override
	public String toShortString() {
		return getText();
	}

	@Override
	public String toTestSummaryString() {
		return "[" + text + "]";
	}

	@Override
	public String toString() {
		return super.toString() + ", text: " + getText();
	}

}
