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

package com.pcsol.jenkins.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.pcsol.jenkins.business.Job;

public class JDOMXMLParser {
    private ArrayList<Job> jobs;
    private File f;

    public void stringToFile(String str) {
        f = new File(str);
    }

    public void urlToFile(String str) {
        URL url;
        try {
            url = new URL(str);
            ReadableByteChannel rbc;
            rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream("src/test/resources/test.xml");
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        f = new File("src/test/resources/test.xml");
    }

    public ArrayList<Job> parse() {
        Document document = new Document();
        SAXBuilder builder = new SAXBuilder();
        Job tmpJob;

        try {
            document = builder.build(f);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element racine = document.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> elements = racine.getChildren("job");
        Iterator<Element> i = elements.iterator();

        jobs = new ArrayList<Job>();

        while (i.hasNext()) {
            Element courant = (Element) i.next();
            tmpJob = new Job(courant.getChildText("name"), courant.getChildText("url"), courant.getChildText("color"));
            // System.out.println(courant.getChild("nom").getText());
            jobs.add(tmpJob);
        }

        return jobs;
    }
}
