package org.contentmine.ami;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MakeVersion {
	public static void main(String[] args) {
		ZonedDateTime now = ZonedDateTime.now();
		//System.out.println(now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		String s = now.format(DateTimeFormatter.ISO_INSTANT).replaceAll(":", "");
		System.out.println(s.substring(0, s.lastIndexOf('.')) + "Z");
	}
}
