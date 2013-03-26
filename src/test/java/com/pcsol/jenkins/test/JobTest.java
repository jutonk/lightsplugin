package com.pcsol.jenkins.test;

import junit.framework.TestCase;
import com.pcsol.jenkins.business.Job;

public class JobTest extends TestCase {

    public void testJob() {
        String name1 = "name";
        String url1 = "url";
        String color1 = "color";

        Job K = new Job(name1, url1, color1);
        int test = 0;

        if (K.getName() != name1 || K.getUrl() != url1 || K.getColor() != color1) {

            test = 1;
        }

        assertEquals(test, 0);

    }

}
