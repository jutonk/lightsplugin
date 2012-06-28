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

package com.pcsol.jenkins.plugin;

import org.apache.log4j.Logger;

import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.pcsol.jenkins.LightsPlugin;
import com.pcsol.jenkins.business.Gyro;
import com.pcsol.jenkins.commons.Context;


public class StopAction implements Action {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(StopAction.class);

	public @SuppressWarnings("rawtypes") AbstractProject project;
	
	public StopAction(@SuppressWarnings("rawtypes") AbstractProject _project/*, boolean red, boolean yellow*/) {
		project = _project;
	}

	public String getIconFileName() {
		return "installer.gif";
	}

	public String getDisplayName() {
		return "Lights";
	}

	public String getUrlName() {
		return "stop";
	}

	public void doSubmit(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
		
		String lastBuildColor = project.getLastBuild().getResult().color.toString();
		User currentUser = User.current();
		if (lastBuildColor.equalsIgnoreCase("red")) {
			String projectName = Context.getInstance().getFailedProjects().get(project);
			if (currentUser != null && projectName != null) {
				Context.getInstance().removeFailedProject(project);
				if (Context.getInstance().getFailedProjects().size() == 0) {
					
					logger.info("Failing list is empty, switching red light off...");
					
					Gyro.doPost(LightsPlugin.getUrl(),LightsPlugin.getRedSocket(),"O");
					LightsPlugin.setRedLightOn(false);
				}
			}
		} else if (lastBuildColor.equalsIgnoreCase("yellow")) {
			String projectName = Context.getInstance().getUnstableProjects().get(project);
			if (currentUser != null && projectName != null) {
				Context.getInstance().removeUnstableProject(project);
				if (Context.getInstance().getUnstableProjects().size() == 0) {
					
					logger.info("Unstable projects list is empty, switching yellow light off...");
					
					Gyro.doPost(LightsPlugin.getUrl(),LightsPlugin.getYellowSocket(),"O");
					LightsPlugin.setYellowLightOn(false);
				}
			}
		}
		
		resp.sendRedirect(req.getContextPath() + "/job/" + project.getName());
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList<String> getFailProjects() {
		ArrayList<String> projects = new ArrayList<String>();
		
		Iterator<Entry<AbstractProject, String>> it = Context.getInstance().getFailedProjects().entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<AbstractProject,String> pair = (Map.Entry<AbstractProject,String>) it.next();
			projects.add(pair.getKey().getDisplayName() + " - " + pair.getValue());
		}
		
		return projects;
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList<String> getUnstableProjects() {
		ArrayList<String> projects = new ArrayList<String>();
		
		Iterator<Entry<AbstractProject, String>> it = Context.getInstance().getUnstableProjects().entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<AbstractProject,String> pair = (Map.Entry<AbstractProject,String>) it.next();
			projects.add(pair.getKey().getDisplayName() + " - " + pair.getValue());
		}
		
		return projects;
	}
	
	public String getProjectAndUser() {
		if (User.current() != null) {
			return project.getDisplayName() + " - " + User.current().getDisplayName();
		}
		
		return project.getDisplayName();
	}
	
	public boolean showButton() {
		if (User.current() != null) {
			for (Iterator<String> iterator = getFailProjects().iterator(); iterator.hasNext();) {
				String str = (String) iterator.next();
				if (str.contains(getProjectAndUser())) {
					return true;
				}
			}
			for (Iterator<String> iterator = getUnstableProjects().iterator(); iterator.hasNext();) {
				String str = (String) iterator.next();
				if (str.contains(getProjectAndUser())) {
					return true;
				}
			}
			if (getUnstableProjects().contains(project.getDisplayName() + " - anonymous")) {
				return true;
			}
			if (getFailProjects().contains(project.getDisplayName() + " - anonymous")) {
				return true;
			}
		}
		
		return false;
	}
}
