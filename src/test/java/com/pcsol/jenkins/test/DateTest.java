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

		if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal
				.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				&& (cal.get(Calendar.HOUR_OF_DAY) >= 8)
				&& (cal.get(Calendar.HOUR_OF_DAY) < 18 || (cal
						.get(Calendar.HOUR_OF_DAY) == 18 && cal
						.get(Calendar.MINUTE) == 0))) {

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

		if ((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal
				.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				&& (cal.get(Calendar.HOUR_OF_DAY) >= 8)
				&& (cal.get(Calendar.HOUR_OF_DAY) < 18 || (cal
						.get(Calendar.HOUR_OF_DAY) == 18 && cal
						.get(Calendar.MINUTE) == 0))) {

			test = 1;
		}

		assertEquals(test, 0);
	}

}
