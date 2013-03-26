package com.pcsol.jenkins.test;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;







public class DateTest extends TestCase {
	
	
    
  
    
    
    
    @Override
	protected void setUp() {
    }
	
	public void testDay() {
		int test = 0;
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		
		if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) &&
				(cal.get(Calendar.HOUR_OF_DAY) >= 8) &&
				(cal.get(Calendar.HOUR_OF_DAY) < 18 || (cal.get(Calendar.HOUR_OF_DAY) == 18 && cal.get(Calendar.MINUTE) == 0))) {
			
			test = 1;
		}
		
		assertEquals(test, 0);
	}
	
	public void testHour() {
	       int test = 0;
	       Calendar cal = new GregorianCalendar();
	       
	       cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	       cal.set(Calendar.HOUR_OF_DAY, 18);
	       cal.set(Calendar.MINUTE, 1);
	       
	       if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) &&
	               (cal.get(Calendar.HOUR_OF_DAY) >= 8) &&
	               (cal.get(Calendar.HOUR_OF_DAY) < 18 || (cal.get(Calendar.HOUR_OF_DAY) == 18 && cal.get(Calendar.MINUTE) == 0))) {
	           
	           test = 1;
	       }
	       
	       assertEquals(test, 0);
	   }
	
	
//	public void testSec(){
//	    
//
//	   Timer timer1 = new Timer();             // Get timer 1
//	    Timer timer2 = new Timer();             // get timer 2
//
//	    long delay1 = 5*1000;                   // 5 seconds delay
//	    long delay2 = 3*1000;                   // 3 seconds delay
//
//	    // Schedule the two timers to run with different delays.
//	    timer1.schedule(new Task("object1"), 0, delay1);
//	    timer2.schedule(new Task("Object2"), 0, delay2);
//	    }
//	    
	 }
	       
	



