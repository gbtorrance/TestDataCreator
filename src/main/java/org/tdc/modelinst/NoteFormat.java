package org.tdc.modelinst;

public class NoteFormat {
	
	public static NoteFormat BASIC_PRE = new NoteFormat(0);
	
	private int relativePos;

	public NoteFormat(int relativePos) {
		this.relativePos = relativePos;
	}
	
	public int getRelativePos() {
		return relativePos;
	}
}
