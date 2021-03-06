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

import hudson.Plugin;

public class StopPlugin extends Plugin {

	int compteur = 0;
	private ThreadStart th;

	public void start() throws Exception {

//		this.th = new ThreadStart();
//		
//		Thread myThread = new Thread(this.th);
//		
//		myThread.start();
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		// TODO Auto-generated method stub
		this.th.doStop();
		super.finalize();
	}
}