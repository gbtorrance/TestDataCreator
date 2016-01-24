package org.tdc.modeldef;

import java.util.ListIterator;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.model.MPathBuilder;
import org.tdc.model.MPathIndex;
import org.w3c.dom.Node;

public class ModelDefSchemaParser {

	private static final Logger log = LoggerFactory.getLogger(ModelDefSchemaParser.class);

	private XSModel xsModel;
	private MPathIndex<NodeDef> mpathIndex; 
	private MPathBuilder mpathBuilder = new MPathBuilder();

	public ModelDefSchemaParser(XSModel xsModel, MPathIndex<NodeDef> mpathIndex) {
		this.xsModel = xsModel;
		this.mpathIndex = mpathIndex;
	}
	
	public ElementNodeDef buildModelDefTreeFromSchema(String rootElementName, String rootElementNamespace) {
        XSElementDeclaration ed = xsModel.getElementDeclaration(rootElementName, rootElementNamespace);
        
        // create root element with no parent
        ElementNodeDef rootElementNodeDef = new ElementNodeDef(null);
        rootElementNodeDef.setMinOccurs(1); // root element must always occur once and only once
        rootElementNodeDef.setMaxOccurs(1); // root element must always occur once and only once
        
        // build a model tree, beginning with the root
        processElementDeclaration(ed, rootElementNodeDef);
        
        return rootElementNodeDef;
	}
	
	private void processElementDeclaration(XSElementDeclaration xsElementDecl, ElementNodeDef elementNodeDef) {
		
		// set the name
    	elementNodeDef.setName(xsElementDecl.getName());
    	elementNodeDef.setMPath(buildMPath(elementNodeDef));
    	mpathBuilder.zoomIn();
    	
    	// elements can be of a simple type or complex type
        XSTypeDefinition xsTypeDef = xsElementDecl.getTypeDefinition();
        if (xsTypeDef.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {

        	// process simple type definition
        	processSimpleTypeDefinition( (XSSimpleTypeDefinition)xsTypeDef, elementNodeDef);
        }
        else if (xsTypeDef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
        	
        	// process complex type definition
    		processComplexTypeDefinition( (XSComplexTypeDefinition)xsTypeDef, elementNodeDef);
        }
        else {
        	// should *never* occur; throw unchecked exception to indicate failure to properly understand XSD model
    		throw new UnsupportedOperationException("Unknown element type definition: " + xsTypeDef.getName());
        }
        processAnnotation(xsElementDecl, elementNodeDef);
        
        mpathBuilder.zoomOut();
	}
	
	private void processSimpleTypeDefinition(XSSimpleTypeDefinition xsSimpleTypeDef, ElementNodeDef elementNodeDef) {
		elementNodeDef.setDataType(getDataType(xsSimpleTypeDef));
		// TODO Anything to do for restriction/list/union ... or restriction/extension? 
	}
	
	private void processComplexTypeDefinition(XSComplexTypeDefinition xsComplexTypeDef, ElementNodeDef elementNodeDef) {
		
		// process attributes
		processAttributes(xsComplexTypeDef, elementNodeDef);
		
		// process based on type of content
    	if (xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_SIMPLE) {

    		// text only content (no other elements)
    		processSimpleTypeDefinition(xsComplexTypeDef.getSimpleType(), elementNodeDef);
    	}
    	else if (xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_ELEMENT ||
    			 xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED ||
    			 xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_EMPTY) {
    		
    		// TODO Anything to do for restriction/extension?
    		
    		// CONTENTTYPE_ELEMENT:
    		// element content (this is where model groups, wildcards, and other elements come into play)
    		// [getParticle() only applies to ELEMENT and MIXED content types]
    		
    		// CONTENTTYPE_MIXED:
    		// set with the "mixed" attribute!
    		// refers to a "letter" type of content (with mixed text and embedded elements (order defined by sequence, choice, etc.) 
    		// [confirmed that getParticle() only applies to ELEMENT and MIXED content types]
    		// Note: we're allowing MIXED types, but there is no is no practical way in MDC to represent the 
    		//       "letter" part, so that will be ignored
    		
    		// CONTENTTYPE_EMPTY
    		// e.g. <element/> or <element abc="123"/>

    		// set type
    		// applicable to ELEMENT, MIXED, and EMPTY
    		elementNodeDef.setDataType(getDataType(xsComplexTypeDef));
    		
    		// process particle 
    		// applicable to ELEMENT and MIXED only
    		if (xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_ELEMENT ||
    			xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED) {
    			
        		processParticle(xsComplexTypeDef.getParticle(), elementNodeDef);
    		}
    	}
    	else {
        	// should *never* occur; throw unchecked exception to indicate failure to properly understand XSD model 
    		throw new UnsupportedOperationException("Unknown content type: " + xsComplexTypeDef.getContentType());
    	}
	}
	
	private void processParticle(XSParticle xsParticle, NonAttribNodeDef nonAttribNodeDef) {
		
		// get term
    	XSTerm xsTerm = (XSTerm)xsParticle.getTerm();
    	
    	// process based on type of term
    	if (xsTerm instanceof XSElementDeclaration) {
    		
    		// element declaration
    		// I believe this can only occur if the particle is part of a model group (nested particle), 
    		// but not when it's part of a complex type model (typedef particle)
    		
    		// create new element (with current element as parent)
    		ElementNodeDef newElementNodeDef = new ElementNodeDef(nonAttribNodeDef);
    		newElementNodeDef.setMinOccurs(xsParticle.getMinOccurs());
    		newElementNodeDef.setMaxOccurs(xsParticle.getMaxOccursUnbounded() ? NonAttribNodeDef.MAX_UNBOUNDED : xsParticle.getMaxOccurs());
    		
    		// add new element as child of current element
    		nonAttribNodeDef.addChild(newElementNodeDef);
    		
    		// process element declaration (for new element node)
        	processElementDeclaration((XSElementDeclaration)xsTerm, newElementNodeDef);
    	}
    	else if (xsTerm instanceof XSModelGroup) {
    		
    		// model groups types are "sequence", "choice", "all"
    		XSModelGroup xsModelGroup = (XSModelGroup)xsTerm;

    		// create compositor node (as child of current element
    		CompositorNodeDef compositorNodeDef = createCompositorNode(nonAttribNodeDef, xsModelGroup);
    		compositorNodeDef.setMPath(buildMPath(compositorNodeDef));
    		compositorNodeDef.setMinOccurs(xsParticle.getMinOccurs());
    		compositorNodeDef.setMaxOccurs(xsParticle.getMaxOccursUnbounded() ? NonAttribNodeDef.MAX_UNBOUNDED : xsParticle.getMaxOccurs());
    		
    		// add new compositor node as child of current element
    		nonAttribNodeDef.addChild(compositorNodeDef);
    		
    		// process model group (for new compositor node)
        	processModelGroup(xsModelGroup, compositorNodeDef);
    	} 
    	else if (xsTerm instanceof XSWildcard) {
    		
    		// "any"
    		
    		// TODO How to handle Wildcards??
    		throw new UnsupportedOperationException("Does not support 'wildcards'");
    	}
    	else {
    		// should *never* occur; throw unchecked exception to indicate failure to properly understand XSD model 
    		throw new UnsupportedOperationException("Unknown term: " + xsTerm.getName());
    	}
	}
	
	private void processModelGroup(XSModelGroup xsModelGroup, CompositorNodeDef compositorNodeDef) {
		
		// note: model groups do not have names; leave as null
		
		// iterate through particle children of model group
    	XSObjectList xsObjectList = (XSObjectList)xsModelGroup.getParticles();
    	ListIterator<?> listIterator = xsObjectList.listIterator();
    	mpathBuilder.zoomIn();
    	while (listIterator.hasNext()) {
    		
    		// process child particle
    		XSParticle xsParticle = (XSParticle)listIterator.next();
    		processParticle(xsParticle, compositorNodeDef);
    	}
    	mpathBuilder.zoomOut();
	}
	
	private void processAttributes(XSComplexTypeDefinition xsComplexTypeDef, ElementNodeDef elementNodeDef) {
		XSObjectList xsObjectList = xsComplexTypeDef.getAttributeUses();
		if (xsObjectList != null) {
			for (int i = 0; i < xsObjectList.getLength(); i++) {
				// loop through attributes associated with this complex type
				XSAttributeUse attribUse = (XSAttributeUse)xsObjectList.item(i);
				XSAttributeDeclaration attribDecl = attribUse.getAttrDeclaration();
				XSSimpleTypeDefinition attribType = attribDecl.getTypeDefinition();
				// create an attrib node def for each and add to model def collection
				AttribNodeDef attribNodeDef = new AttribNodeDef(elementNodeDef);
				attribNodeDef.setName(attribDecl.getName());
				attribNodeDef.setMPath(buildMPath(attribNodeDef));
				attribNodeDef.setDataType(getDataType(attribType));
				attribNodeDef.setIsRequired(attribUse.getRequired());
				elementNodeDef.addAttribute(attribNodeDef);
			}
		}
	}
	
	private void processAnnotation(XSElementDeclaration xsElementDecl, ElementNodeDef elementNodeDef) {
		
		// TODO can attributes have annotations? what about compositors?
		
		// TODO this is a rough solution, but it doesn't:
		// - properly deal with namespaces; implement something using a NamespaceConext
		//      http://stackoverflow.com/questions/6390339/how-to-query-xml-using-namespaces-in-java-with-xpath
		// - isn't generic enough (as far as where it's storing the data)
		XSAnnotation xsAnnotation = xsElementDecl.getAnnotation();
		if (xsAnnotation != null) {
			Node dom = new DocumentImpl();
			xsAnnotation.writeAnnotation(dom,  XSAnnotation.W3C_DOM_DOCUMENT);
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			String xpathExpression = null;
			try {
				// hack solution due to namespace issues (see above)
				xpathExpression = "//*[local-name()='Description'][1]";   //|//*[local-name()='documentation'][1]"; -- consider add for Fed documentation 
				String description = xpath.evaluate(xpathExpression, dom);
				elementNodeDef.setDescription(description);
				
				xpathExpression = "//*[local-name()='LineNumber'][1]";
				String lineNum = xpath.evaluate(xpathExpression, dom);
				elementNodeDef.setLineNum(lineNum);

				xpathExpression = "//*[local-name()='FormNumber'][1]";
				String formNum = xpath.evaluate(xpathExpression, dom);
				elementNodeDef.setFormNum(formNum);
			} 
			catch (XPathExpressionException ex) {
				throw new RuntimeException("Invalid xpath expression : " + xpathExpression, ex);
			}
		}
	}

	private CompositorNodeDef createCompositorNode(NonAttribNodeDef parent, XSModelGroup mg) {
    	switch(mg.getCompositor()) {
			case XSModelGroup.COMPOSITOR_SEQUENCE: return new CompositorNodeDef(parent, CompositorType.SEQUENCE);
			case XSModelGroup.COMPOSITOR_CHOICE: return new CompositorNodeDef(parent, CompositorType.CHOICE);
			case XSModelGroup.COMPOSITOR_ALL: return new CompositorNodeDef(parent, CompositorType.ALL);
			// should *never* occur; throw unchecked exception to indicate failure to properly understand XSD model 
			default: throw new UnsupportedOperationException("Invalid compositor: " + mg.getCompositor());
    	}
	}
	
	private String buildMPath(NodeDef nodeDef) {
		String mpath = mpathBuilder.buildMPath(nodeDef);
		mpathIndex.addMPath(mpath, nodeDef);
		return mpath;
	}
	
	private String getDataType(XSTypeDefinition xsTypeDef) {
		// TODO more robust way to handle this?
		String type = xsTypeDef.getName();
		if (type == null) {
			type = getDataType(xsTypeDef.getBaseType());
		}
		if (xsTypeDef instanceof XSSimpleTypeDefinition) {
			XSSimpleTypeDefinition xsSimpleTypeDef = (XSSimpleTypeDefinition)xsTypeDef;
			XSFacet xsMaxLength = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_MAXLENGTH);
			if (xsMaxLength != null) {
				type = type + " {" + xsMaxLength.getIntFacetValue() + "}";
			}
		}
		return type.equals("anyType") ? "" : type;
	}
}
