package com.alami.koperasi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
	
}
