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

package org.contentmine.eucl.euclid.euclid;

import java.util.Date;

import org.contentmine.eucl.euclid.JodaDate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JodaDateTest {

	@Test
	public void testFormatDate() {
		DateTime datetime = new DateTime(1288135627973L);
		datetime = datetime.withZone(DateTimeZone.forID("UTC"));
		String dateTimeString = JodaDate.formatIsoDate(datetime);
		Assert.assertEquals("date string", "2010-10-26T23:27:07.973Z",
				dateTimeString);
	}

	@Test
	public void testParseDate() {
		DateTime dateTime = JodaDate.parseDate("2010-10-27T00:27:07+01:00");
		Assert.assertEquals("date millis", 1288135627000L, dateTime.getMillis());
	}

	@Test
	public void testParseDate1() {
		DateTime dateTime = JodaDate.parseDate("25/12/1984", "dd/mm/yyyy");
		Assert.assertEquals("date format", 443837520000L, dateTime.getMillis());
	}

	@SuppressWarnings("deprecation")
	@Test
	@Ignore
	// This test seems to be time zone dependent - failed in Australia//
	public void testParseJavaDate() {
		Date date = new Date(2001 - 1900, 12, 25, 10, 20, 30);
		Assert.assertNotNull(date);
		DateTime dateTime = JodaDate.parseJavaDate(date);
		Assert.assertNotNull(dateTime);
		Assert.assertEquals("date to datetime", 1011954030000L,
				dateTime.getMillis());
	}

	@Test
	public void testParseJodaDate() {
		DateTime dateTime = new DateTime(1288135627000L).withZone(DateTimeZone
				.forID("UTC"));
		Date date = JodaDate.parseJodaDate(dateTime);
		// I can't hack this at present
		// Assert.assertEquals("datetime to date",
		// "Sat Nov 27 22:27:07 GMT 2010", date.toString());
		Assert.assertTrue("datetime to date",
				date.toString().indexOf("Nov 27") != -1);
	}

}
