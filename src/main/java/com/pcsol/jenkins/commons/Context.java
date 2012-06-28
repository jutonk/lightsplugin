/* 
 * This plugin controls Jenkins builds with flash lights.
 * Copyright (C) 2012	PCSol S.A.

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

import org.apache.log4j.Logger;

import hudson.model.AbstractProject;

import java.util.HashMap;
import java.util.Map;

public class Context {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Context.class);

	private static Context context = null;
	@SuppressWarnings("rawtypes")
	private Map<AbstractProject, String> failedProjects;
	@SuppressWarnings("rawtypes")
	private Map<AbstractProject, String> unstableProjects;
	
	public static Context getInstance(){
		if(context==null){
			context = new Context();
		}
		return context;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<AbstractProject, String> getFailedProjects() {
		if (failedProjects==null) {
			failedProjects = new HashMap<AbstractProject, String>();
		}
		return failedProjects;
	}
	
	@SuppressWarnings("rawtypes")
	public void addFailedProject(AbstractProject project, String user) {
		failedProjects.put(project,user);
		logger.info("Project added to failing list...");
	}
	
	@SuppressWarnings("rawtypes")
	public void removeFailedProject(AbstractProject project) {
		failedProjects.remove(project);
		logger.info("Project removed from failing list...");
	}
	
	@SuppressWarnings("rawtypes")
	public Map<AbstractProject, String> getUnstableProjects() {
		if (unstableProjects==null) {
			unstableProjects = new HashMap<AbstractProject, String>();
		}
		return unstableProjects;
	}
	
	@SuppressWarnings("rawtypes")
	public void addUnstableProject(AbstractProject project, String user) {
		unstableProjects.put(project,user);
		logger.info("Project added to unstable projects list...");
	}
	
	@SuppressWarnings("rawtypes")
	public void removeUnstableProject(AbstractProject project) {
		unstableProjects.remove(project);
		logger.info("Project removed from unstable projects list...");
	}
}
