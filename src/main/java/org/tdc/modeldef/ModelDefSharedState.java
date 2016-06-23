package org.tdc.modeldef;

import java.util.HashMap;
import java.util.Map;

import org.tdc.model.ModelSharedState;

/**
 * State shared between "def" models and "def" nodes for that model.
 */
public class ModelDefSharedState extends ModelSharedState {
	// TODO ensure variables are properly thread safe
	// TODO set appropriate initial size for maps to minimize rehashing

	private final Map<String, Map<NodeDef, String>> allVariables = new HashMap<>();
	
	public String getVariable(String name, NodeDef node) {
		Map<NodeDef, String> variables = allVariables.get(name);
		return variables == null ? null : variables.get(node);
	}

	public synchronized void setVariable(String name, NodeDef node, String value) {
		throwExceptionIfImmutable("addVariable");
		
		Map<NodeDef, String> variables = allVariables.get(name);
		if (variables == null) {
			variables = new HashMap<>(100);
			allVariables.put(name, variables);
		}
		variables.put(node, value);
	}
}
