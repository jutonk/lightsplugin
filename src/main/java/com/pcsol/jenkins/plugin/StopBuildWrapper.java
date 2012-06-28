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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.pcsol.jenkins.LightsPlugin.DescriptorImpl;

public class StopBuildWrapper extends BuildWrapper {
//private transient Logger log = LoggerFactory.getLogger(M2ReleaseBuildWrapper.class);
	
	/** For backwards compatibility with older configurations. @deprecated */
	@DataBoundConstructor
	public StopBuildWrapper() {
		super();
	}

	@Override
	public Action getProjectAction(@SuppressWarnings("rawtypes") AbstractProject project) {
		return new StopAction(project);
	}
	
	@Override
	public Environment setUp(@SuppressWarnings("rawtypes") AbstractBuild build, Launcher launcher, final BuildListener listener)
	                                                                                              throws IOException,
	                                                                                              InterruptedException {
		return new Environment() {

			@Override
			public void buildEnvVars(Map<String, String> env) {
			}

			@Override
			public boolean tearDown(@SuppressWarnings("rawtypes") AbstractBuild bld, BuildListener lstnr)
					throws IOException, InterruptedException {

				return true;
			}
		};
	}

	/**
	 * Hudson defines a method {@link Builder#getDescriptor()}, which returns the corresponding
	 * {@link Descriptor} object. Since we know that it's actually {@link DescriptorImpl}, override the method
	 * and give a better return type, so that we can access {@link DescriptorImpl} methods more easily. This is
	 * not necessary, but just a coding style preference.
	 */
	@Override
	public StopDescriptorImpl getDescriptor() {
		return (StopDescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static class StopDescriptorImpl extends BuildWrapperDescriptor {
		public StopDescriptorImpl() {
			super(StopBuildWrapper.class);
			load();
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			return true;
		}

		@Override
		public boolean configure(StaplerRequest staplerRequest, JSONObject json) throws FormException {
			return true; // indicate that everything is good so far
		}

		@Override
		public String getDisplayName() {
			return "Enable Stop Button";
		}
	}
}
