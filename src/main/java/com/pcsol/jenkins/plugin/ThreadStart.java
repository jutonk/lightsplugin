package com.pcsol.jenkins.plugin;

import hudson.model.AbstractProject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.pcsol.jenkins.business.Job;
import com.pcsol.jenkins.commons.Context;
import com.pcsol.jenkins.parser.JDOMXMLParser;

class ThreadStart implements Runnable {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ThreadStart.class.getCanonicalName());

	@SuppressWarnings("rawtypes")
	AbstractProject p = null;
	Context Context = null;
	Map<String, String> failedProjects = Context.getInstance()
			.getFailedProjects();
	Map<String, String> unstableProjects = Context.getInstance()
			.getUnstableProjects();

	boolean emptyFailingList = false;
	boolean emptyUnstableList = false;
	private boolean shouldRun = true;

	public void run() {
		int cptTime = 0;
		while (shouldRun) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cptTime++;
			if (cptTime > 900) {
				this.processVerification();
				cptTime = 0;
			}
		}
	}

	public void doStop() {
		this.shouldRun = false;
	}

	public void processVerification() {
		Calendar cal = new GregorianCalendar();

		if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal
				.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				&& (cal.get(Calendar.HOUR_OF_DAY) >= 9)
				&& (cal.get(Calendar.HOUR_OF_DAY) < 17 || (cal
						.get(Calendar.HOUR_OF_DAY) == 17 && cal
						.get(Calendar.MINUTE) == 0))) {


			JDOMXMLParser K = new JDOMXMLParser();
			K.urlToFile(Context.getInstance().getJenkinsXML());
			ArrayList<Job> projet = K.parse();
			Iterator<Job> i = projet.iterator();
			
			logger.info("\n\n********** Scanning projects using thread start **********\n\n");

			while (i.hasNext()) {

				Job j = i.next();

				String projectName = j.getName();
				String color = j.getColor();

				logger.info("\n\n********** Project handled by thread **********");
				
				logger.info("Name: " + projectName);

				logger.info("Color: " + color);

				logger.info("********** Project status **********");
				
				if (color.equalsIgnoreCase("red")) {
					if (!Context.getInstance().getFailedProjects()
							.containsKey(projectName)) {

						logger.info("Project not in failing list");

						if (Context.getInstance().getFailedProjects().size() == 0) {
							emptyFailingList = true;
						}

						Context.getInstance().addFailedProject(projectName,
								"Not fixed");
					} else {
						logger.info("Project already in failing list...");
					}
				}

				if (color.equalsIgnoreCase("yellow")) {
					if (!Context.getInstance().getUnstableProjects()
							.containsKey(projectName)) {

						logger.info("Project not in unstable projects list...");

						if (Context.getInstance().getUnstableProjects().size() == 0) {
							emptyUnstableList = true;
						}

						Context.getInstance().addUnstableProject(projectName,
								"Not fixed");
					} else {
						logger.info("Project already in unstable projects list...");
					}
				}

			}
			
			logger.info("\n\n********** Scanning projects using thread end **********\n\n");

			logger.info("********** Lights managing by thread **********");

			if (emptyFailingList) {
				//Gyro.doPost(LightsPlugin.getUrl(), LightsPlugin.getRedSocket(),
				//		"I", LightsPlugin.getUser(), LightsPlugin.getPasswd());
				//LightsPlugin.setRedLightOn(true);
			}

			if (emptyUnstableList) {
				//Gyro.doPost(LightsPlugin.getUrl(),
				//		LightsPlugin.getYellowSocket(), "I",
				//		LightsPlugin.getUser(), LightsPlugin.getPasswd());
				//LightsPlugin.setYellowLightOn(true);
			}

			emptyFailingList = false;
			emptyUnstableList = false;

			logger.info("********** End Project handling by thread **********\n\n");
			
			// Show lists
			Context.getInstance().showFailingList();
			Context.getInstance().showUnstableList();
		}
		else {
        	logger.info("\n\n********** Not a working day **********\n\n");
        }
	}
}
