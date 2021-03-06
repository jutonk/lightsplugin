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

package com.pcsol.jenkins.business;

public class Job {
	private String name;
	private String url;
	private String color;
	
	public Job () {
		name = new String();
		url = new String();
		color = new String();
	}
	
	public Job(String _name, String _url, String _color) {
		name = _name;
		url = _url;
		color = _color;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getColor() {
		return color;
	}
	
}
