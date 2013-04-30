/* 
x * This plugin controls Jenkins builds with flash lights.
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

import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.pcsol.jenkins.LightsPlugin;
import com.pcsol.jenkins.commons.Context;

public class StopAction implements Action {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(StopAction.class.getCanonicalName());

	public @SuppressWarnings("rawtypes")
	AbstractProject project;

	public StopAction(@SuppressWarnings("rawtypes") AbstractProject _project) {
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

	public void doSubmit(StaplerRequest req, StaplerResponse resp)
			throws Exception {

		String lastBuildColor = project.getLastBuild().getResult().color
				.toString();
		User currentUser = User.current();
		
		logger.info("\n\n********** Stoping lights for project **********");

		if (lastBuildColor.equalsIgnoreCase("red")) {
			String projectName = Context.getInstance().getFailedProjects()
					.get(project.getDisplayName());

			if (currentUser != null && projectName != null) {
				Context.getInstance().removeFailedProject(project.getDisplayName());

				logger.info("User: " + currentUser.getDisplayName() + " has stopped light for project: " + project.getDisplayName());

				if (Context.getInstance().getFailedProjects().size() == 0) {

					logger.info("Failing list is empty, switching red light off...");

					LightsPlugin.setRedLightOff();
				}
			}

		} else if (lastBuildColor.equalsIgnoreCase("yellow")) {
			String projectName = Context.getInstance().getUnstableProjects()
					.get(project.getDisplayName());

			if (currentUser != null && projectName != null) {
				Context.getInstance().removeUnstableProject(project.getDisplayName());

				logger.info("User: " + currentUser.getDisplayName()
						+ " has stopped light for project: " + project.getDisplayName());

				if (Context.getInstance().getUnstableProjects().size() == 0) {

					logger.info("Unstable projects list is empty, switching yellow light off...");

					LightsPlugin.setYellowLightOff();
				}
			}
		}

		
		 resp.sendRedirect(req.getContextPath() + "/job/" +
		 project.getName());

	}

	public ArrayList<String> getFailProjects() {
		ArrayList<String> projects = new ArrayList<String>();

		Iterator<Entry<String, String>> it = Context.getInstance()
				.getFailedProjects().entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Map.Entry<String, String>) it
					.next();
			projects.add(pair.getKey() + " - "
					+ pair.getValue());
		}

		return projects;
	}

	public ArrayList<String> getUnstableProjects() {
		ArrayList<String> projects = new ArrayList<String>();

		Iterator<Entry<String, String>> it = Context.getInstance()
				.getUnstableProjects().entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Map.Entry<String, String>) it
					.next();
			projects.add(pair.getKey() + " - "
					+ pair.getValue());
		}

		return projects;
	}


	public String getProjectAndUser() {
		if (User.current() != null) {
			return project.getDisplayName() + " - "
					+ User.current().getDisplayName();
		}

		return project.getDisplayName();
	}


	public boolean showStopButton() {
		if (User.current() != null) {

			if (getFailProjects().size() > 0
					|| getUnstableProjects().size() > 0) {
				
				if(Context.getInstance().getFailedProjects().containsKey(project.getDisplayName())) {
					return true;
				}
				
				if(Context.getInstance().getUnstableProjects().containsKey(project.getDisplayName())) {
					return true;
				}
				
				return false;
			}
		}

		return false;

	}
	
	public boolean showEmptyButton() {
		if (User.current() != null) {

			if (getFailProjects().size() > 0
					|| getUnstableProjects().size() > 0) {
				
				return true;
			}
		}

		return false;

	}
	
	public boolean showCheckButton() {
		if (User.current() != null) {
			
			return true;
		}
		
		return false;
		
	}

	public void doEmpty(StaplerRequest req, StaplerResponse resp) throws IOException {
		
		logger.info("\n\n********** Making the lists empty **********");
		
		if (Context.getInstance().getFailedProjects().size() > 0) {

			LightsPlugin.setRedLightOff();
		}

		if (Context.getInstance().getUnstableProjects().size() > 0) {

			LightsPlugin.setYellowLightOff();
		}

		Context.getInstance().getUnstableProjects().clear();
		Context.getInstance().getFailedProjects().clear();
		
		resp.sendRedirect(req.getContextPath() + "/job/" + project.getName());
	}
	
	public void doCheck(StaplerRequest req, StaplerResponse resp) throws IOException {
		
		logger.info("\n\n********** Check projects **********");

		resp.sendRedirect(req.getContextPath() + "/job/" + project.getName());
		
		ThreadStart thread = new ThreadStart();
		
		thread.processVerification();
		
	}

}
