package org.tdc.modelinst;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.modelinst.ModelInstConfig;
import org.tdc.model.MPathBuilder;
import org.tdc.model.MPathIndex;
import org.tdc.modeldef.AttribNodeDef;
import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.CompositorType;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.NodeDef;
import org.tdc.modeldef.NonAttribNodeDef;

public class ModelInstBuilderImpl implements ModelInstBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(ModelInstBuilderImpl.class);

	private ModelInstConfig config;
	private ModelDef modelDef;
	private int rowOffset;
	private MPathIndex<NodeInst> mpathIndex; 
	private MPathBuilder mpathBuilder;
	
	public ModelInstBuilderImpl(ModelInstConfig config, ModelDef modelDef) {
		this.config = config;
		this.modelDef = modelDef;
	}
	
	@Override
	public ModelInst build() {
		log.debug("Start building ModelInst tree");
		ElementNodeInst rootElementInst = buildRootElementInst(modelDef.getRootElement());
		log.debug("Finish building ModelInst tree: rootElementInst: {}", rootElementInst.getName()); 
		return new ModelInstImpl(config, modelDef, rootElementInst, mpathIndex);
	}
	
	private ElementNodeInst buildRootElementInst(ElementNodeDef rootElementNodeDef) {
		mpathIndex = new MPathIndex<>();
		mpathBuilder = new MPathBuilder();
		rowOffset = 0;
		List<? extends NonAttribNodeInst> list;
		list = buildTree(null, rootElementNodeDef, 0);
		if (list.size() != 1) {
			throw new IllegalStateException("There should only be one 'root' element, not " + list.size());
		}
		mpathIndex = null;
		mpathBuilder = null;
		return (ElementNodeInst)list.get(0);
	}
	
	// TODO reconsider; are generics needed here? probably should not use wildcards as return type
	private List<? extends NonAttribNodeInst> buildTree(
			NonAttribNodeInst parentNonAttribNodeInst, NonAttribNodeDef nonAttribNodeDef,int colOffset) {
		
		List<? extends NonAttribNodeInst> list;
		if (nonAttribNodeDef instanceof ElementNodeDef) {
			ElementNodeDef elementNodeDef = (ElementNodeDef)nonAttribNodeDef;
			list = buildOccurrenceElements(parentNonAttribNodeInst, elementNodeDef, colOffset);
		}
		else if (nonAttribNodeDef instanceof CompositorNodeDef) {
			CompositorNodeDef compositorNodeDef = (CompositorNodeDef)nonAttribNodeDef;
			if (canFlattenCompositor(compositorNodeDef)) {
				list = flattenCompositor(parentNonAttribNodeInst, compositorNodeDef, colOffset);
			}
			else {
				list = buildOccurrenceCompositors(parentNonAttribNodeInst, compositorNodeDef, colOffset);
			}
		}
		else {
			throw new IllegalStateException("Non Attrib Node Def is of invalid type: " + nonAttribNodeDef.getClass().getTypeName());
		}
		return list;
	}
	
	private List<ElementNodeInst> buildOccurrenceElements(
			NonAttribNodeInst parentNonAttribNodeInst, ElementNodeDef elementNodeDef, int colOffset) {
		
		List<ElementNodeInst> occurrenceList = new ArrayList<>();
		ElementNodeInst elementNodeInst;
		int occurCount = getOccurDepth(elementNodeDef);
		for (int i = 0; i < occurCount; i++) {
			elementNodeInst = buildElement(parentNonAttribNodeInst, elementNodeDef, colOffset, i+1, occurCount);
			rowOffset++;
			mpathBuilder.zoomIn();
			buildPreNotes(elementNodeInst);
			buildPostNotes(elementNodeInst);
			buildAttribs(elementNodeInst);
			buildChildren(elementNodeInst);
			mpathBuilder.zoomOut();
			occurrenceList.add(elementNodeInst);
		}
		return occurrenceList;
	}
	
	private ElementNodeInst buildElement(
			NonAttribNodeInst parentNonAttribNodeInst, ElementNodeDef elementNodeDef, 
			int colOffset, int occurNum, int occurCount) {
		
		ElementNodeInst elementNodeInst = new ElementNodeInst(parentNonAttribNodeInst, elementNodeDef);
		elementNodeInst.setRowOffset(rowOffset);
		elementNodeInst.setColOffset(colOffset);
		elementNodeInst.setOccurNum(occurNum);
		elementNodeInst.setOccurCount(occurCount);
		elementNodeInst.setMPath(buildMPath(elementNodeInst, occurNum == 1));
		return elementNodeInst;
	}
	
	private List<CompositorNodeInst> buildOccurrenceCompositors(
			NonAttribNodeInst parentNonAttribNodeInst, CompositorNodeDef compositorNodeDef, int colOffset) {
		
		List<CompositorNodeInst> occurrenceList = new ArrayList<>();
		CompositorNodeInst compositorNodeInst;
		int occurCount = getOccurDepth(compositorNodeDef);
		for (int i = 0; i < occurCount; i++) {
			compositorNodeInst = buildCompositor(parentNonAttribNodeInst, compositorNodeDef, colOffset, i+1, occurCount);
			rowOffset++;
			mpathBuilder.zoomIn();
			buildPreNotes(compositorNodeInst);
			buildPostNotes(compositorNodeInst);
			buildChildren(compositorNodeInst);
			mpathBuilder.zoomOut();
			occurrenceList.add(compositorNodeInst);
		}
		return occurrenceList;
	}
	
	private CompositorNodeInst buildCompositor(
			NonAttribNodeInst parentNonAttribNodeInst, CompositorNodeDef compositorNodeDef, 
			int colOffset, int occurNum, int occurCount) {
		
		CompositorNodeInst compositorNodeInst = new CompositorNodeInst(parentNonAttribNodeInst, compositorNodeDef);
		compositorNodeInst.setRowOffset(rowOffset);
		compositorNodeInst.setColOffset(colOffset);
		compositorNodeInst.setOccurNum(occurNum);
		compositorNodeInst.setOccurCount(occurCount);
		compositorNodeInst.setMPath(buildMPath(compositorNodeInst, occurNum == 1));
		return compositorNodeInst;
	}
	
	private int getOccurDepth(NonAttribNodeDef nonAttribNodeDef) {
		// TODO determine a more flexible/configurable way to handle occurrences
		int configOccurDepth = config.getMPathOccurrenceDepth(nonAttribNodeDef.getMPath());
		int occurDepth = 
				nonAttribNodeDef.isUnbounded() || nonAttribNodeDef.getMaxOccurs() > configOccurDepth ? 
						configOccurDepth :
							nonAttribNodeDef.getMaxOccurs();
		return occurDepth;
	}

	private void buildAttribs(ElementNodeInst parentElementNodeInst) {
		ElementNodeDef elementNodeDef = parentElementNodeInst.getNodeDef();
		if (elementNodeDef.hasAttribute()) {
			for (AttribNodeDef attribNodeDef : elementNodeDef.getAttributes()) {
				AttribNodeInst attribNodeInst = buildAttrib(parentElementNodeInst, attribNodeDef, 
						parentElementNodeInst.getColOffset()+1);
				rowOffset++;
				buildPreNotes(attribNodeInst);
				buildPostNotes(attribNodeInst);
				parentElementNodeInst.addAttribute(attribNodeInst);
			}
		}
	}
	
	private AttribNodeInst buildAttrib(
			ElementNodeInst parentElementNodeInst, AttribNodeDef attribNodeDef, int colOffset) {
		
		AttribNodeInst attribNodeInst = new AttribNodeInst(parentElementNodeInst, attribNodeDef);
		attribNodeInst.setRowOffset(rowOffset);
		attribNodeInst.setColOffset(colOffset);
		attribNodeInst.setMPath(buildMPath(attribNodeInst, true));
		return attribNodeInst;
	}
	
	private void buildChildren(NonAttribNodeInst parentNonAttribNodeInst) {
		List<? extends NonAttribNodeInst> childList;
		for (NonAttribNodeDef nonAttribNodeDefChild : parentNonAttribNodeInst.getNodeDef().getChildren()) {
			childList = buildTree(parentNonAttribNodeInst, nonAttribNodeDefChild, 
					parentNonAttribNodeInst.getColOffset()+1);
			parentNonAttribNodeInst.addChildAll(childList);
		}
	}
	
	private boolean canFlattenCompositor(CompositorNodeDef compositorNodeDef) {
		// TODO add configuration for flattening sequence and choice
		// TODO cleanup code
		
		// flatten compositor if:
		// - it is a sequence with one and only one occurrence (and)
		//   - its parent is an element (or)
		//   - its parent is a compositor which is NOT a choice
		//     (because if it's a choice, it needs to be clearly marked as such in the output, so it can't be flattened)
		boolean canFlatten = false;
		if ((compositorNodeDef.getType() == CompositorType.SEQUENCE) && //|| compositorNodeDef.getType() == CompositorType.CHOICE) &&
				compositorNodeDef.getMinOccurs() == 1 &&
				compositorNodeDef.getMaxOccurs() == 1 &&
				(compositorNodeDef.getParent() instanceof ElementNodeDef ||
						(compositorNodeDef.getParent() instanceof CompositorNodeDef &&
								((CompositorNodeDef)compositorNodeDef.getParent()).getType() != CompositorType.CHOICE
						)
				)
			) {
			canFlatten = true;
		}
		return canFlatten;
	}
	
	private List<? extends NonAttribNodeInst> flattenCompositor(
			NonAttribNodeInst parentNonAttribNodeInst, CompositorNodeDef compositorNodeDef, int colOffset) {
		
		List<NonAttribNodeInst> list = new ArrayList<>();
		List<? extends NonAttribNodeInst> childList;
		
		buildFlattenedNodeMPath(compositorNodeDef);
		
		mpathBuilder.zoomIn();
		for (NonAttribNodeDef nonAttribNodeDefChild : compositorNodeDef.getChildren()) {
			if (nonAttribNodeDefChild instanceof CompositorNodeDef && 
					canFlattenCompositor((CompositorNodeDef)nonAttribNodeDefChild)) {
				
				childList = flattenCompositor(parentNonAttribNodeInst, (CompositorNodeDef)nonAttribNodeDefChild, colOffset);
			}
			else {
				childList = buildTree(parentNonAttribNodeInst, nonAttribNodeDefChild, colOffset);
			}
			list.addAll(childList);
		}
		mpathBuilder.zoomOut();
		
		return list;
	}

	private void buildPreNotes(NodeInst nodeInst) {
		// TODO implement notes?
		//buildNotesForMultiOccurs(nodeInst);
		//buildNotesForChoices(nodeInst);
	}
	
	private void buildPostNotes(NodeInst nodeInst) {
		// TODO implement notes?
	}
	
//	private void buildNotesForMultiOccurs(NodeInst nodeInst) {
//		if (nodeInst instanceof NonAttribNodeInst) {
//			NonAttribNodeInst nonAttribNodeInst = (NonAttribNodeInst)nodeInst;
//			if (nonAttribNodeInst.getOccurCount() > 1 && nonAttribNodeInst.getOccurNum() == 1) {
//				NoteInst note = new NoteInst(nodeInst, "Multiples allowed", NoteFormat.BASIC_PRE);
//				nodeInst.addPreNote(note);
//				rowOffset++;
//			}
//		}
//	}
//	
//	private void buildNotesForChoices(NodeInst nodeInst) {
//		if (nodeInst instanceof NonAttribNodeInst) {
//			NonAttribNodeInst nonAttribNodeInst = (NonAttribNodeInst)nodeInst;
//			if (nonAttribNodeInst.isFirstChildOfChoice()) {
//				// need to get count from parent of *def* node, 
//				// as choice compositor parent may have been removed in inst model
//				int count = nonAttribNodeInst.getNodeDef().getParent().getChildren().size();
//				NoteInst note = new NoteInst(nodeInst, "Choose 1 of the next " + count + " element(s)", NoteFormat.BASIC_PRE);
//				nodeInst.addPreNote(note);
//				rowOffset++;
//			}
//		}
//	}
	
	private String buildMPath(NodeInst nodeInst, boolean isFirstOccurrence) {
		NodeDef nodeDef = nodeInst.getNodeDef();
		String mpath = mpathBuilder.buildMPath(nodeDef);
		mpathIndex.addMPath(mpath, nodeInst);
		return mpath;
	}
	
	private void buildFlattenedNodeMPath(NodeDef nodeDef) {
		// even though we don't care about the return value here, we do need
		// to do this to ensure the mpaths sync up correctly between
		// ModelDef and ModelInst models
		mpathBuilder.buildMPath(nodeDef);
	}
}
