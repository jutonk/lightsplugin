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

//import org.apache.log4j.Logger;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Logger;

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
	private static final Logger logger = Logger.getLogger(LightsPlugin.class
			.getCanonicalName());

	public static String DISPLAY_NAME = "Lights Plugin";
	private static boolean redLightOn = false;
	private static boolean yellowLightOn = false;
	private static String urlGyro = "";
	private static String redSocketGyro = "0";
	private static String yellowSocketGyro = "1";
	private static String redSocket = "";
	private static String yellowSocket = "";
	private static String user = "";
	private static String passwd = "";
	private static String jenkinsXML = "";

	@DataBoundConstructor
	public LightsPlugin() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) {
		Calendar cal = new GregorianCalendar();
		String color = build.getResult().color.toString();
		UserIdCause userCause = (UserIdCause) build.getCause(UserIdCause.class);

		redSocket = getDescriptor().getRedSocket();
		yellowSocket = getDescriptor().getYellowSocket();

		if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal
				.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				&& (cal.get(Calendar.HOUR_OF_DAY) >= 9)
				&& (cal.get(Calendar.HOUR_OF_DAY) < 17 || (cal
						.get(Calendar.HOUR_OF_DAY) == 17 && cal
						.get(Calendar.MINUTE) == 0))) {

			logger.info("\n\n********** Project handled **********");

			logger.info("Color: " + color);

			logger.info("Name: " + build.getProject().getDisplayName());
			if (userCause != null) {
				logger.info("User: " + userCause.getUserName());
			}

			logger.info("********** Project status **********");

			if (color.equalsIgnoreCase("red")) {

				logger.info("Project has failed...");

				if (!Context.getInstance().getFailedProjects()
						.containsKey(build.getProject().getDisplayName())) {

					logger.info("Project not in failing list");

					if (userCause != null) {
						Context.getInstance().addFailedProject(
								build.getProject().getDisplayName(),
								userCause.getUserName());
					} else {

						logger.info("No user detected, user set to 'anonymous/SCM user'");

						String lastUser = "anonymous/SCM user";
						for (Iterator iterator = build.getCulprits().iterator(); iterator
								.hasNext();) {
							User u = (User) iterator.next();
							lastUser = u.getDisplayName();
						}

						logger.info("Adding project to the failing list...");

						Context.getInstance().addFailedProject(
								build.getProject().getDisplayName(), lastUser);
					}
				} else {
					logger.info("Project already in failing list...");
				}

				if (Context.getInstance().getUnstableProjects()
						.containsKey(build.getProject().getDisplayName())) {

					logger.info("Project was unstable. Removing project from the list...");

					Context.getInstance().removeUnstableProject(
							build.getProject().getDisplayName());

					if (Context.getInstance().getUnstableProjects().size() == 0) {

						logger.info("No more unstable projects, switching the yellow light off");

						setYellowLightOff();
					}
				}

				logger.info("Project has failed, switching the red light on");

				setRedLightOn();

			} else if (color.equalsIgnoreCase("yellow")) {

				logger.info("Project is unstable...");

				if (!Context.getInstance().getUnstableProjects()
						.containsKey(build.getProject().getDisplayName())) {

					logger.info("Project not in unstable projects list...");

					if (userCause != null) {
						Context.getInstance().addUnstableProject(
								build.getProject().getDisplayName(),
								userCause.getUserName());
					} else {

						logger.info("No user detected, user set to 'anonymous/SCM user");

						String lastUser = "anonymous/SCM user";
						for (Iterator iterator = build.getCulprits().iterator(); iterator
								.hasNext();) {
							User u = (User) iterator.next();
							lastUser = u.getDisplayName();
						}

						logger.info("Adding projects to unstable projects list...");

						Context.getInstance().addUnstableProject(
								build.getProject().getDisplayName(), lastUser);
					}
				} else {
					logger.info("Project already in unstable projects list...");
				}

				if (Context.getInstance().getFailedProjects()
						.containsKey(build.getProject().getDisplayName())) {

					logger.info("Project was failing. Removing project from the list...");

					Context.getInstance().removeFailedProject(
							build.getProject().getDisplayName());

					if (Context.getInstance().getFailedProjects().size() == 0) {

						logger.info("No more fail projects, switching the red light off");

						setRedLightOff();
					}
				}

				logger.info("Project is unstable, switching the yellow light on");

				setYellowLightOn();

			} else {

				logger.info("Project not failed or unstable...");

				if (Context.getInstance().getUnstableProjects()
						.containsKey(build.getProject().getDisplayName())) {

					logger.info("Project was unstable. Removing project from the list...");

					Context.getInstance().removeUnstableProject(
							build.getProject().getDisplayName());

					if (Context.getInstance().getUnstableProjects().size() == 0) {

						logger.info("No more unstable projects, switching the yellow light off");

						setYellowLightOff();
					}
				}
				if (Context.getInstance().getFailedProjects()
						.containsKey(build.getProject().getDisplayName())) {

					logger.info("Project was failing. Removing project from the list...");

					Context.getInstance().removeFailedProject(
							build.getProject().getDisplayName());

					if (Context.getInstance().getFailedProjects().size() == 0) {

						logger.info("No more fail projects, switching the red light off");

						setRedLightOff();
					}
				}
			}

			logger.info("********** Lights status **********");

			if (isRedLightOn()) {
				logger.info("Red light is (or should be) on...");
			} else {
				logger.info("Red light is (or should be) off...");
			}

			if (isYellowLightOn()) {
				logger.info("Yellow light is (or should be) on...");
			} else {
				logger.info("Yellow light is (or should be) off...");
			}

			logger.info("********** End Project handling **********\n\n");

			// Show lists
			Context.getInstance().showFailingList();
			Context.getInstance().showUnstableList();
		} else {
			// System.out.println("********** Not a working day **********");
			logger.info("********** Not a working day **********");
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
			urlGyro = formData.getString("url");
			redSocketGyro = formData.getString("redSocket");
			yellowSocketGyro = formData.getString("yellowSocket");
			user = formData.getString("username");
			passwd = formData.getString("passwd");
			jenkinsXML = formData.getString("jenkinsXml");

			Context.getInstance().setJenkinsXML(jenkinsXML);

			save();
			return super.configure(req, formData);
		}

		public String getUrl() {
			return urlGyro;
		}

		public String getRedSocket() {
			return redSocketGyro;
		}

		public String getYellowSocket() {
			return yellowSocketGyro;
		}

		public String getUser() {
			return user;
		}

		public String getPass() {
			return passwd;
		}

		public String getJenkinsXML() {
			return jenkinsXML;
		}

	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	public static void setRedLightOn() {

		if (redLightOn == false) {

			LightsPlugin.redLightOn = true;
			redLightOn = true;

			Gyro.doPost(urlGyro, redSocket, "I", user, passwd);
		}
	}

	public static void setRedLightOff() {

		if (redLightOn == true) {

			LightsPlugin.redLightOn = false;
			redLightOn = false;

			Gyro.doPost(urlGyro, redSocket, "O", user, passwd);
		}
	}

	public static boolean isRedLightOn() {
		return redLightOn;
	}

	public static void setYellowLightOn() {

		if (yellowLightOn == false) {

			LightsPlugin.yellowLightOn = true;
			yellowLightOn = true;

			Gyro.doPost(urlGyro, yellowSocket, "I", user, passwd);
		}
	}

	public static void setYellowLightOff() {

		if (yellowLightOn == true) {

			LightsPlugin.yellowLightOn = false;
			yellowLightOn = false;

			Gyro.doPost(urlGyro, yellowSocket, "O", user, passwd);
		}
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

	public static void setUser(String user) {
		LightsPlugin.user = user;
	}

	public static String getUser() {
		return user;
	}

	public static void setPasswd(String passwd) {
		LightsPlugin.passwd = passwd;
	}

	public static String getPasswd() {
		return passwd;
	}

	public static void setJenkinsXML(String jenkinsXML) {
		LightsPlugin.jenkinsXML = jenkinsXML;
	}

	public static String getJenkinsXML() {
		return jenkinsXML;
	}

}