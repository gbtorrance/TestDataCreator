package org.tdc.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * A {@link TestCase} implementation.
 */
public class TestCaseImpl implements TestCase {
	
	private final int caseNum;
	private final String setName;
	private final List<TestDoc> testDocs;
	
	public TestCaseImpl(Builder builder) {
		this.caseNum = builder.caseNum;
		this.setName = builder.setName;
		this.testDocs = Collections.unmodifiableList(builder.testDocs); // unmodifiable
	}

	@Override
	public int getCaseNum() {
		return caseNum;
	}

	@Override
	public String getSetName() {
		return setName;
	}
	
	@Override
	public List<TestDoc> getTestDocs() {
		return testDocs;
	}

	public static class Builder {
		private final String setName;
		private final List<TestDoc> allTestDocsInSet;
		
		private int caseNum;
		private List<TestDoc> testDocs;
		
		public Builder(String setName, List<TestDoc> allTestDocsInSet) {
			this.setName = setName;
			this.allTestDocsInSet = allTestDocsInSet;
		}
		
		public List<TestCase> buildAll() {
			List<TestCase> testCases = new ArrayList<>();
			Map<Integer, List<TestDoc>> testCaseNumToTestDocMap = allTestDocsInSet
					.stream()
					.collect(Collectors.groupingBy(TestDoc::getCaseNum));
			for (Entry<Integer, List<TestDoc>> entry : testCaseNumToTestDocMap.entrySet()) {
				TestCase testCase = build(entry.getKey(), entry.getValue());
				testCases.add(testCase);
			}
			return testCases;
		}
		
		private TestCase build(int caseNum, List<TestDoc> allTestDocsInCase) {
			this.caseNum = caseNum;
			this.testDocs = allTestDocsInCase;
			return new TestCaseImpl(this);
		}
	}
}
