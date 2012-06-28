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

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import biz.source_code.base64Coder.Base64Coder;



public class Gyro {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Gyro.class);

	public static boolean doPost(String adresse, String socket, String value){
		   OutputStreamWriter writer = null;
		   BufferedReader reader = null;
		   try {
		     //encodage des paramètres de la requête
		      String donnees = "F"+URLEncoder.encode(socket, "UTF-8")+
		                        "="+URLEncoder.encode(value, "UTF-8");
		      
		      logger.info("Data: " + donnees);

		      //création de la connection
		      URL url = new URL(adresse);
		      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		      conn.setRequestMethod("POST");
		      conn.setDoOutput(true);
		      String userpassword = "username:password";
		      String encodedAuthorization = Base64Coder.encodeString( userpassword );
		      conn.setRequestProperty("Authorization", "Basic "+
		            encodedAuthorization);
		      
		      //envoi de la requête
		      
		      logger.info("POST request: " + conn.toString());
		      
		      writer = new OutputStreamWriter(conn.getOutputStream());
		      writer.write(donnees);
		      writer.flush();

		      //lecture de la réponse
		      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		      String ligne;
//		      while ((ligne = reader.readLine()) != null) {
//		         System.out.println(ligne);
//		      }
		      
		      return true;
		      
		   }catch (Exception e) {
		      e.printStackTrace();
		      return false;
		   }finally{
		      try{writer.close();}catch(Exception e){}
		      try{reader.close();}catch(Exception e){}
		   }
		}
}
