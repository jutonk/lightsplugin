/* 
 * This plugin controls Jenkins builds with flash lights.
 * Copyright (C) 2012    PCSol S.A.

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcsol.jenkins.commons;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class Context {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Context.class.getCanonicalName());

	private static Context context = null;
	private Map<String, String> failedProjects;
	private Map<String, String> unstableProjects;
	private String jenkinsXML;

	public static Context getInstance() {
		if (context == null) {
			context = new Context();
		}
		return context;
	}

	public Map<String, String> getFailedProjects() {
		if (failedProjects == null) {
			failedProjects = new HashMap<String, String>();
		}
		return failedProjects;
	}

	public void addFailedProject(String project, String user) {
		getFailedProjects().put(project, user);
//		System.out.println("Project added to failing list...");
		logger.info("Project added to failing list...");
	}

	public void removeFailedProject(String project) {
		getFailedProjects().remove(project);
//		System.out.println("Project removed from failing list...");
		logger.info("Project removed from failing list...");
	}

	public Map<String, String> getUnstableProjects() {
		if (unstableProjects == null) {
			unstableProjects = new HashMap<String, String>();
		}
		return unstableProjects;
	}

	public void addUnstableProject(String project, String user) {
		getUnstableProjects().put(project, user);
//		System.out.println("Project added to unstable projects list...");
		logger.info("Project added to unstable projects list...");
	}

	public void removeUnstableProject(String project) {
		getUnstableProjects().remove(project);
//		System.out.println("Project removed from unstable projects list...");
		logger.info("Project removed from unstable projects list...");
	}
	
	public void showFailingList() {
		Iterator it = getFailedProjects().entrySet().iterator();
		
//		System.out.println("\n\n********** Failing project(s) list **********");
		logger.info("\n\n********** Failing project(s) list **********");
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
//			System.out.println((String) pairs.getKey());
			logger.info((String) pairs.getKey());
		}
	}
	
	public void showUnstableList() {
		Iterator it = getUnstableProjects().entrySet().iterator();
		
//		System.out.println("\n\n********** Unstable project(s) list **********");
		logger.info("\n\n********** Unstable project(s) list **********");
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
//			System.out.println((String) pairs.getKey());
			logger.info((String) pairs.getKey());
		}
	}

	public String getJenkinsXML() {
		// TODO Auto-generated method stub
		return jenkinsXML;
	}

	public void setJenkinsXML(String jenkinsXML) {
		// TODO Auto-generated method stub
		this.jenkinsXML = jenkinsXML;
	}

}