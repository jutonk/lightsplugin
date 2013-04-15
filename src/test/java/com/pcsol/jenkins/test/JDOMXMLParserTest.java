package com.pcsol.jenkins.test;

import java.util.List;

import com.pcsol.jenkins.business.Job;
import com.pcsol.jenkins.parser.JDOMXMLParser;

import junit.framework.TestCase;

public class JDOMXMLParserTest extends TestCase {
	
	@Override
	protected void setUp() {
    }
	
	public void testDocumentParse() {
		JDOMXMLParser parser = new JDOMXMLParser();

		parser.stringToFile("src/test/resources/hudson.xml");
		List<Job> jobs = parser.parse();
		
		assertTrue(jobs.size() > 0);
		
	}
}