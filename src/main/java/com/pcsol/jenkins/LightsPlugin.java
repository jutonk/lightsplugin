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
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a
 * new {@link LightsPlugin} is created. The created instance is
 * persisted to the project configuration XML by using XStream, so
 * this allows you to use instance fields (like {@link #name}) to
 * remember the configuration.
 * 
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method
 * will be invoked.
 * 
 * @author Kohsuke Kawaguchi
 */
public class LightsPlugin extends Recorder {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(LightsPlugin.class.getCanonicalName());

    public static String DISPLAY_NAME = "Lights Plugin";
    private static boolean redLightOn = false;
    private static boolean yellowLightOn = false;
    private static boolean orangeLightOn = false;
    private static String urlGyro = "";
    private static String redSocketGyro = "0";
    private static String yellowSocketGyro = "1";
    private static String user = "";
    private static String passwd = "";
    private static String jenkinsXML = "";

    @DataBoundConstructor
    public LightsPlugin() {

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        String socket = null;
        Calendar cal = new GregorianCalendar();
        String color = build.getResult().color.toString();
        UserIdCause userCause = (UserIdCause) build.getCause(UserIdCause.class);

        if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) && (cal.get(Calendar.HOUR_OF_DAY) >= 9)
                && (cal.get(Calendar.HOUR_OF_DAY) < 17 || (cal.get(Calendar.HOUR_OF_DAY) == 17 && cal.get(Calendar.MINUTE) == 0))) {

        	//System.out.println("\n\n********** Project handled **********");
        	logger.info("\n\n********** Project handled **********");

//        	System.out.println("Color: " + color);
        	logger.info("Color: " + color);

//        	System.out.println("Name: " + build.getProject().getDisplayName());
        	logger.info("Name: " + build.getProject().getDisplayName());
            if (userCause != null) {
//            	System.out.println("User: " + userCause.getUserName());
            	logger.info("User: " + userCause.getUserName());
            }

//            System.out.println("********** Project status **********");
            logger.info("********** Project status **********");
            
            if (color.equalsIgnoreCase("red")) {

//            	System.out.println("Project fails...");
            	logger.info("Project fails...");

                if (!Context.getInstance().getFailedProjects().containsKey(build.getProject().getDisplayName())) {

//                	System.out.println("Project not in failing list");
                	logger.info("Project not in failing list");

                    if (userCause != null) {
                        Context.getInstance().addFailedProject(build.getProject().getDisplayName(), userCause.getUserName());
                    } else {

//                    	System.out.println("No user detected, user set to 'anonymous/SCM user'");
                    	logger.info("No user detected, user set to 'anonymous/SCM user'");

                        String lastUser = "anonymous/SCM user";
                        for (Iterator iterator = build.getCulprits().iterator(); iterator.hasNext();) {
                            User u = (User) iterator.next();
                            lastUser = u.getDisplayName();
                        }

//                        System.out.println("Adding project to the failing list...");
                        logger.info("Adding project to the failing list...");

                        Context.getInstance().addFailedProject(build.getProject().getDisplayName(), lastUser);
                    }
                } else {
//                	System.out.println("Project already in failing list...");
                	logger.info("Project already in failing list...");
                }

                if (Context.getInstance().getUnstableProjects().containsKey(build.getProject().getDisplayName())) {

//                	System.out.println("Project was unstable. Removing project from the list...");
                	logger.info("Project was unstable. Removing project from the list...");

                    Context.getInstance().removeUnstableProject(build.getProject().getDisplayName());

                    if (Context.getInstance().getUnstableProjects().size() == 0) {

//                    	System.out.println("No more unstable projects, switching the yellow light off");
                    	logger.info("No more unstable projects, switching the yellow light off");

                        socket = getDescriptor().getYellowSocket();
                        Gyro.doPost(urlGyro, socket, "O", user, passwd);
                    }
                }
                
                socket = getDescriptor().getRedSocket();

            } else if (color.equalsIgnoreCase("yellow")) {

//            	System.out.println("Project is unstable...");
            	logger.info("Project is unstable...");

                if (!Context.getInstance().getUnstableProjects().containsKey(build.getProject().getDisplayName())) {

//                	System.out.println("Project not in unstable projects list...");
                	logger.info("Project not in unstable projects list...");

                    if (userCause != null) {
                        Context.getInstance().addUnstableProject(build.getProject().getDisplayName(), userCause.getUserName());
                    } else {

//                    	System.out.println("No user detected, user set to 'anonymous/SCM user");
                    	logger.info("No user detected, user set to 'anonymous/SCM user");

                        String lastUser = "anonymous/SCM user";
                        for (Iterator iterator = build.getCulprits().iterator(); iterator.hasNext();) {
                            User u = (User) iterator.next();
                            lastUser = u.getDisplayName();
                        }

//                        System.out.println("Adding projects to unstable projects list...");
                        logger.info("Adding projects to unstable projects list...");

                        Context.getInstance().addUnstableProject(build.getProject().getDisplayName(), lastUser);
                    }
                } else {
//                	System.out.println("Project already in unstable projects list...");
                	logger.info("Project already in unstable projects list...");
                }

                if (Context.getInstance().getFailedProjects().containsKey(build.getProject().getDisplayName())) {

//                	System.out.println("Project was failing. Removing project from the list...");
                	logger.info("Project was failing. Removing project from the list...");

                    Context.getInstance().removeFailedProject(build.getProject().getDisplayName());

                    if (Context.getInstance().getFailedProjects().size() == 0) {

//                    	System.out.println("No more fail projects, switching the red light off");
                    	logger.info("No more fail projects, switching the red light off");

                        socket = getDescriptor().getRedSocket();
                        Gyro.doPost(urlGyro, socket, "O", user, passwd);
                    }
                }
                
                socket = getDescriptor().getYellowSocket();

            } else {

//            	System.out.println("Project not fail or unstable...");
            	logger.info("Project not fail or unstable...");

                if (Context.getInstance().getUnstableProjects().containsKey(build.getProject().getDisplayName())) {

//                	System.out.println("Project was unstable. Removing project from the list...");
                	logger.info("Project was unstable. Removing project from the list...");

                    Context.getInstance().removeUnstableProject(build.getProject().getDisplayName());

                    if (Context.getInstance().getUnstableProjects().size() == 0) {

//                    	System.out.println("No more unstable projects, switching the yellow light off");
                    	logger.info("No more unstable projects, switching the yellow light off");

                        socket = getDescriptor().getYellowSocket();
                        Gyro.doPost(urlGyro, socket, "O", user, passwd);
                    }
                }
                if (Context.getInstance().getFailedProjects().containsKey(build.getProject().getDisplayName())) {

//                	System.out.println("Project was failing. Removing project from the list...");
                	logger.info("Project was failing. Removing project from the list...");

                    Context.getInstance().removeFailedProject(build.getProject().getDisplayName());

                    if (Context.getInstance().getFailedProjects().size() == 0) {

//                    	System.out.println("No more fail projects, switching the red light off");
                    	logger.info("No more fail projects, switching the red light off");

                        socket = getDescriptor().getRedSocket();
                        Gyro.doPost(urlGyro, socket, "O", user, passwd);
                    }
                }

                socket = null;
            }
            
//            System.out.println("********** Lights status **********");
            logger.info("********** Lights status **********");

            if (socket != null) {

                if (!(color.equalsIgnoreCase("red") && isRedLightOn()) && !(color.equalsIgnoreCase("yellow") && isYellowLightOn())) {

//                	System.out.println("Light is not (or should not be) on...");
                	logger.info("Light is not (or should not be) on...");

                    Gyro.doPost(urlGyro, socket, "I", user, passwd);

//                    System.out.println("Light is (or shoud be) on...");
                    logger.info("Light is (or shoud be) on...");
                }
            }

            if (color.equalsIgnoreCase("red")) {

//            	System.out.println("Red light is on...");
            	logger.info("Red light is on...");

                setRedLightOn(true);
            } else if (color.equalsIgnoreCase("yellow")) {

//            	System.out.println("Yellow light is on...");
            	logger.info("Yellow light is on...");

                setYellowLightOn(true);
            }
            
//            System.out.println("********** End Project handling **********\n\n");
            logger.info("********** End Project handling **********\n\n");
            
            // Show lists
            Context.getInstance().showFailingList();
         	Context.getInstance().showUnstableList();
        }
        else {
//        	System.out.println("********** Not a working day **********");
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
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {


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
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
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
    public static void setOrangeLightOn(boolean orangeLightOn) {
        LightsPlugin.orangeLightOn = orangeLightOn;
    }

    public static boolean isOrangeLightOn() {
        return orangeLightOn;
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