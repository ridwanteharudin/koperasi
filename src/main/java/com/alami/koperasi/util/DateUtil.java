package com.alami.koperasi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static Date convertDate(String sdate) {
		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(sdate);
			return date;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public static Date getDateNow() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    return calendar.getTime();
	}
}
