package com.pcsol.jenkins.test;

import com.pcsol.jenkins.commons.Context;


import hudson.model.AbstractProject;

import java.util.Map;


import junit.framework.TestCase;

public class ContextTest extends TestCase {
    @SuppressWarnings("rawtypes")
    AbstractProject project;
    @SuppressWarnings("rawtypes")
    Map<String, String> failedProjectstest = null;
    @SuppressWarnings("rawtypes")
    Map<String, String> UnstableProjectstest = null;
//    @SuppressWarnings("rawtypes")
//    Map<AbstractProject, String> stopProjectstest = null;

    int test = 0;
    Context context1 = null;
    String lastUser = "lastUser1";

    @SuppressWarnings("static-access")
    public void testGetFailedProjects() {
        failedProjectstest = context1.getInstance().getFailedProjects();
        if (failedProjectstest == null) {
            test = 1;
        }
        assertEquals(test, 0);
    }

//    @SuppressWarnings("static-access")
//    public void testAddFailedProjects() {
//        context1.getInstance().addFailedProject(project.getDisplayName(), lastUser);
//        failedProjectstest = context1.getInstance().getFailedProjects();
//        if (failedProjectstest == null) {
//            test = 1;
//        }
//        assertEquals(test, 0);
//    }
//
//    @SuppressWarnings("static-access")
//    public void testRemoveFailedProjects() {
//        context1.getInstance().addFailedProject(project.getDisplayName(), lastUser);
//        context1.getInstance().removeFailedProject(project);
//        failedProjectstest = context1.getInstance().getFailedProjects();
//        if (failedProjectstest != null) {
//            test = 1;
//        }
//        assertEquals(test, 1);
//    }

    @SuppressWarnings("static-access")
    public void testGetUnstableProjects() {
        UnstableProjectstest = context1.getInstance().getUnstableProjects();
        if (UnstableProjectstest == null) {
            test = 1;
        }
        assertEquals(test, 0);
    }

//    @SuppressWarnings("static-access")
//    public void testAddUnstableProjects() {
//        context1.getInstance().addUnstableProject(project.getDisplayName(), lastUser);
//        UnstableProjectstest = context1.getInstance().getUnstableProjects();
//        if (UnstableProjectstest == null) {
//            test = 1;
//        }
//        assertEquals(test, 0);
//    }
//
//    @SuppressWarnings("static-access")
//    public void testRemoveUnstableProjects() {
//        context1.getInstance().addUnstableProject(project.getDisplayName(), lastUser);
//        context1.getInstance().removeUnstableProject(project);
//        failedProjectstest = context1.getInstance().getUnstableProjects();
//        if (failedProjectstest != null) {
//            test = 1;
//        }
//        assertEquals(test, 1);
//    }

//    @SuppressWarnings("static-access")
//    public void testGetStopProjects() {
//        stopProjectstest = context1.getInstance().getStopProjects();
//        if (stopProjectstest == null) {
//            test = 1;
//        }
//        assertEquals(test, 0);
//    }
//
//    @SuppressWarnings("static-access")
//    public void testAddStopProjects() {
//        context1.getInstance().addStopProject(project, lastUser);
//        stopProjectstest = context1.getInstance().getStopProjects();
//        if (stopProjectstest == null) {
//            test = 1;
//        }
//        assertEquals(test, 0);
//    }
//
//    @SuppressWarnings("static-access")
//    public void testRemoveStopProjects() {
//        context1.getInstance().addStopProject(project, lastUser);
//        context1.getInstance().removeStopProject(project);
//        stopProjectstest = context1.getInstance().getStopProjects();
//        if (stopProjectstest != null) {
//            test = 1;
//        }
//        assertEquals(test, 1);
//    }
}