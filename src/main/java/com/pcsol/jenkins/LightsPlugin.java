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

package com.pcsol.jenkins;

import org.apache.log4j.Logger;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.scm.PollingResult.Change;
import hudson.scm.ChangeLogSet;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.pcsol.jenkins.business.Gyro;
import com.pcsol.jenkins.commons.Context;

/**
 * Sample {@link Builder}.
 * 
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link LightsPlugin} is created. The created instance is persisted to the
 * project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #name}) to remember the configuration.
 * 
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 * 
 * @author Kohsuke Kawaguchi
 */
public class LightsPlugin extends Recorder {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LightsPlugin.class);

	public static String DISPLAY_NAME = "Lights Plugin";
	private static boolean redLightOn = false;
	private static boolean yellowLightOn = false;
	private static String urlGyro = "";
	private static String redSocketGyro = "0";
	private static String yellowSocketGyro = "1";

	@DataBoundConstructor
	public LightsPlugin() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) {
		String socket = null;
		Calendar cal = new GregorianCalendar();
		String color = build.getResult().color.toString();
		UserIdCause userCause = (UserIdCause) build.getCause(UserIdCause.class);

		if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal
				.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				&& (cal.get(Calendar.HOUR_OF_DAY) >= 8)
				&& (cal.get(Calendar.HOUR_OF_DAY) < 18 || (cal
						.get(Calendar.HOUR_OF_DAY) == 18 && cal
						.get(Calendar.MINUTE) == 0))) {

			logger.info("Color: " + color);
			logger.info("Project: " + build.getProject());
			if (userCause != null) {
				logger.info("User: " + userCause.getUserName());
			}

			if (color.equalsIgnoreCase("red")) {

				logger.info("Project fails...");

				if (!Context.getInstance().getFailedProjects()
						.containsKey(build.getProject())) {

					logger.info("Project not in failing list");

					if (userCause != null) {
						Context.getInstance().addFailedProject(
								build.getProject(), userCause.getUserName());
					} else {

						logger.info("No user detected, user set to 'anonymous'");

						String lastUser = "anonymous";
						for (Iterator iterator = build.getCulprits().iterator(); iterator
								.hasNext();) {
							User u = (User) iterator.next();
							lastUser = u.getDisplayName();
						}

						logger.info("Adding project to the failing list...");

						Context.getInstance().addFailedProject(
								build.getProject(), lastUser);
					}
				} else {
					logger.info("Project already in failing list...");
				}

				if (Context.getInstance().getUnstableProjects()
						.containsKey(build.getProject())) {

					logger.info("Project was unstable. Removing project from the list...");

					Context.getInstance().removeUnstableProject(
							build.getProject());

					if (Context.getInstance().getUnstableProjects().size() == 0) {

						logger.info("No more unstable projects, switching the yellow light off");

						socket = getDescriptor().getYellowSocket();
						Gyro.doPost(getDescriptor().getUrl(), socket, "O");
					}
				}
				socket = getDescriptor().getRedSocket();

			} else if (color.equalsIgnoreCase("yellow")) {

				logger.info("Project is unstable...");

				if (!Context.getInstance().getUnstableProjects()
						.containsKey(build.getProject())) {

					logger.info("Project not in unstable projects list...");

					if (userCause != null) {
						Context.getInstance().addUnstableProject(
								build.getProject(), userCause.getUserName());
					} else {

						logger.info("No user detected, user set to 'anonymous");

						String lastUser = "anonymous";
						for (Iterator iterator = build.getCulprits().iterator(); iterator
								.hasNext();) {
							User u = (User) iterator.next();
							lastUser = u.getDisplayName();
						}

						logger.info("Adding projects to unstable projects list...");

						Context.getInstance().addUnstableProject(
								build.getProject(), lastUser);
					}
				} else {
					logger.info("Project already in unstable projects list...");
				}

				if (Context.getInstance().getFailedProjects()
						.containsKey(build.getProject())) {

					logger.info("Project was failing. Removing project from the list...");

					Context.getInstance().removeFailedProject(
							build.getProject());

					if (Context.getInstance().getFailedProjects().size() == 0) {

						logger.info("No more fail projects, switching the red light off");

						socket = getDescriptor().getRedSocket();
						Gyro.doPost(getDescriptor().getUrl(), socket, "O");
					}
				}
				socket = getDescriptor().getYellowSocket();

			} else {

				logger.info("Project not fail or unstable...");

				if (Context.getInstance().getUnstableProjects()
						.containsKey(build.getProject())) {

					logger.info("Project was unstable. Removing project from the list...");

					Context.getInstance().removeUnstableProject(
							build.getProject());

					if (Context.getInstance().getUnstableProjects().size() == 0) {

						logger.info("No more unstable projects, switching the yellow light off");

						socket = getDescriptor().getYellowSocket();
						Gyro.doPost(getDescriptor().getUrl(), socket, "O");
					}
				}
				if (Context.getInstance().getFailedProjects()
						.containsKey(build.getProject())) {
					
					logger.info("Project was failing. Removing project from the list...");
					
					Context.getInstance().removeFailedProject(
							build.getProject());

					if (Context.getInstance().getFailedProjects().size() == 0) {
						
						logger.info("No more fail projects, switching the red light off");
						
						socket = getDescriptor().getRedSocket();
						Gyro.doPost(getDescriptor().getUrl(), socket, "O");
					}
				}

				socket = null;
			}

			if (socket != null) {

				if (!(color.equalsIgnoreCase("red") && isRedLightOn())
						&& !(color.equalsIgnoreCase("yellow") && isYellowLightOn())) {
					
					logger.info("Light is not on...");
					
					Gyro.doPost(getDescriptor().getUrl(), socket, "I");
					
					logger.info("Light is on...");
				}
			}

			if (color.equalsIgnoreCase("red")) {
				
				logger.info("Red light is on...");
				
				setRedLightOn(true);
			} else if (color.equalsIgnoreCase("yellow")) {
				
				logger.info("Yellow light is on...");
				
				setYellowLightOn(true);
			}
		}

		return true;
	}

	@Override
	public boolean needsToRunAfterFinalized() {
		return true;
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

		public String url = "";
		public String redSocket = "0";
		public String yellowSocket = "1";

		@Override
		public String getDisplayName() {
			return "Enable " + LightsPlugin.DISPLAY_NAME;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(Class arg0) {
			return true;
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData)
				throws FormException {
			url = formData.getString("url");
			redSocket = formData.getString("redSocket");
			yellowSocket = formData.getString("yellowSocket");
			urlGyro = url;
			redSocketGyro = redSocket;
			yellowSocketGyro = yellowSocket;
			save();
			return super.configure(req, formData);
		}

		public String getUrl() {
			return url;
		}

		public String getRedSocket() {
			return redSocket;
		}

		public String getYellowSocket() {
			return yellowSocket;
		}

	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	public static void setRedLightOn(boolean redLightOn) {
		LightsPlugin.redLightOn = redLightOn;
	}

	public static boolean isRedLightOn() {
		return redLightOn;
	}

	public static void setYellowLightOn(boolean yellowLightOn) {
		LightsPlugin.yellowLightOn = yellowLightOn;
	}

	public static boolean isYellowLightOn() {
		return yellowLightOn;
	}

	public static void setUrl(String url) {
		LightsPlugin.urlGyro = url;
	}

	public static String getUrl() {
		return urlGyro;
	}

	public static void setRedSocket(String redSocket) {
		LightsPlugin.redSocketGyro = redSocket;
	}

	public static String getRedSocket() {
		return redSocketGyro;
	}

	public static void setYellowSocket(String yellowSocket) {
		LightsPlugin.yellowSocketGyro = yellowSocket;
	}

	public static String getYellowSocket() {
		return yellowSocketGyro;
	}

}