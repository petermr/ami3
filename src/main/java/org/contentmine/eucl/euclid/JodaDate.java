/**
 *    Copyright 2011 Peter Murray-Rust
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.euclid;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
/**
 * really just to remember at this stage
 * @author pm286
 * 
The pattern syntax is mostly compatible with java.text.SimpleDateFormat - time zone names cannot be parsed and a few more symbols are supported. All ASCII letters are reserved as pattern letters, which are defined as follows:

     Symbol  Meaning                      Presentation  Examples
     ------  -------                      ------------  -------
     G       era                          text          AD
     C       century of era (>=0)         number        20
     Y       year of era (>=0)            year          1996

     x       weekyear                     year          1996
     w       week of weekyear             number        27
     e       day of week                  number        2
     E       day of week                  text          Tuesday; Tue

     y       year                         year          1996
     D       day of year                  number        189
     M       month of year                month         July; Jul; 07
     d       day of month                 number        10

     a       halfday of day               text          PM
     K       hour of halfday (0~11)       number        0
     h       clockhour of halfday (1~12)  number        12

     H       hour of day (0~23)           number        0
     k       clockhour of day (1~24)      number        24
     m       minute of hour               number        30
     s       second of minute             number        55
     S       fraction of second           number        978

     z       time zone                    text          Pacific Standard Time; PST
     Z       time zone offset/id          zone          -0800; -08:00; America/Los_Angeles

     '       escape for text              delimiter
     ''      single quote                 literal       '
     

The count of pattern letters determine the format.

Text: If the number of pattern letters is 4 or more, the full form is used; otherwise a short or 
abbreviated form is used if available.

Number: The minimum number of digits. Shorter numbers are zero-padded to this amount.

Year: Numeric presentation for year and weekyear fields are handled specially. 
For example, if the count of 'y' is 2, the year will be displayed as the zero-based year of the century, 
which is two digits.

Month: 3 or over, use text, otherwise use number.

Zone: 'Z' outputs offset without a colon, 'ZZ' outputs the offset with a colon, 'ZZZ' or more outputs the zone id.

Zone names: Time zone names ('z') cannot be parsed.

Any characters in the pattern that are not in the ranges of ['a'..'z'] and ['A'..'Z'] will be treated as 
quoted text. For instance, characters like ':', '.', ' ', '#' and '?' will appear in the resulting time 
text even they are not embraced within single quotes.

DateTimeFormat is thread-safe and immutable, and the formatters it returns are as well.  *
 */
public class JodaDate {

// from W3C                                         2000-01-12T12:13:14Z
//    Wed Jul  8 10:53:24 PDT 1998
	private static final String ALPHA_ZONE_PATTERN = "EEE MMM  d HH:mm:ss ZZZ yyyy";
    private static final DateTimeFormatter ALPHA_ZONE_FORMATTER = DateTimeFormat.forPattern(ALPHA_ZONE_PATTERN);
//    Wed Jul  8 10:53:24 1998
	private static final String ALPHA_PATTERN1 = "EEE MMM  d HH:mm:ss yyyy";
	private static final String ALPHA_PATTERN2 = "EEE MMM dd HH:mm:ss yyyy";
    private static final DateTimeFormatter ALPHA_FORMATTER1 = DateTimeFormat.forPattern(ALPHA_PATTERN1);
    private static final DateTimeFormatter ALPHA_FORMATTER2 = DateTimeFormat.forPattern(ALPHA_PATTERN2);
    
    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZZ";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern(DATETIME_PATTERN);

    private static final String ZULU_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z";
    private static final DateTimeFormatter ZULU_FORMATTER = DateTimeFormat.forPattern(ZULU_DATETIME_PATTERN);


    public static String formatDate(DateTime datetime) {
        if (DateTimeZone.UTC.equals(datetime.getZone())) {
            return ZULU_FORMATTER.print(datetime);
        } else {
            return DATETIME_FORMATTER.print(datetime);
        }
    }
    
	public static String formatIsoDate(DateTime datetime) {
	    return ISODateTimeFormat.dateTime().print(datetime);
	}

    public static DateTime parseDate(String s) {
        if (s.endsWith("Z")) {
            MutableDateTime dateTime = ZULU_FORMATTER.parseMutableDateTime(s);
            dateTime.setZone(DateTimeZone.UTC);
            return dateTime.toDateTime();
        } else if (Character.isLetter(s.charAt(0))){
        	DateTime dateTime = JodaDate.parseQuietly(ALPHA_FORMATTER2, s);
            if (dateTime == null) {
                dateTime = JodaDate.parseQuietly(ALPHA_FORMATTER1, s);
            }
            return dateTime;
        } else {
            return DATETIME_FORMATTER.parseDateTime(s);
        }
    }
    

    /** return null if fails
     * 
     * @param formatter
     * @param s
     * @return
     */
    public static DateTime parseQuietly(DateTimeFormatter formatter, String s) {
    	DateTime dateTime = null;
    	if (formatter != null) {
    		try {
    			dateTime = formatter.parseDateTime(s);
    		} catch (Exception e) {
    			//
    		}
    	}
    	return dateTime;
	}

	public static DateTime parseDate(String date, String format) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
    	if (format.endsWith("Z")) {
    	} else {
    		dateTimeFormatter = dateTimeFormatter.withZone(DateTimeZone.forID("UTC"));
    	}
        DateTime dateTime = dateTimeFormatter.parseDateTime(date);
        return dateTime.withZone(DateTimeZone.forID("UTC"));
    }

    public static DateTime parseJavaDate(Date javaDate) {
    	long seconds = javaDate.getTime();
    	return new DateTime(seconds);
    }

    @SuppressWarnings("deprecation")
	public static Date parseJodaDate(DateTime jodaDate) {
    	int year = jodaDate.getYear();
    	int month = jodaDate.getMonthOfYear();
    	int day = jodaDate.getDayOfMonth();
    	int hour = jodaDate.getHourOfDay();
    	int min = jodaDate.getMinuteOfDay();
    	int sec = jodaDate.getSecondOfMinute();
    	// arghh
    	Date date = new Date(year-1900, month, day, hour, min, sec);
    	return date;
    }


}
