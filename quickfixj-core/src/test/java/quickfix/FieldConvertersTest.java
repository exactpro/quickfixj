/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved.
 *
 * This file is part of the QuickFIX FIX Engine
 *
 * This file may be distributed under the terms of the quickfixengine.org
 * license as defined by quickfixengine.org and appearing in the file
 * LICENSE included in the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 *
 * Contact ask@quickfixengine.org if any conditions of this licensing
 * are not clear to you.
 ******************************************************************************/

package quickfix;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import quickfix.field.converter.BooleanConverter;
import quickfix.field.converter.CharConverter;
import quickfix.field.converter.DoubleConverter;
import quickfix.field.converter.IntConverter;
import quickfix.field.converter.UtcDateOnlyConverter;
import quickfix.field.converter.UtcTimeOnlyConverter;
import quickfix.field.converter.UtcTimestampConverter;

import junit.framework.TestCase;

public class FieldConvertersTest extends TestCase {

    public void testIntegerConversion() throws Exception {
        assertEquals("123", IntConverter.convert(123));
        assertEquals(123, IntConverter.convert("123"));
        assertEquals(-1, IntConverter.convert("-1"));
        assertEquals(23, IntConverter.convert("00023"));
        try {
            IntConverter.convert("abc");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            IntConverter.convert("123.4");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            IntConverter.convert("+200");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
    }

    public void testDoubleConversion() throws Exception {
        assertEquals("45.32", DoubleConverter.convert(45.32));
        assertEquals("45", DoubleConverter.convert(45));
        assertEquals("0", DoubleConverter.convert(0));
        assertEquals(45.32, DoubleConverter.convert("45.32"), 0);
        assertEquals(45.32, DoubleConverter.convert("45.3200"), 0);
        assertEquals(0.00340244, DoubleConverter.convert("0.00340244000"), 0);
        assertEquals(45.32, DoubleConverter.convert("45.32"), 0);
        assertEquals(12.000000000001, DoubleConverter
                .convert("12.000000000001"), 0);
        assertEquals(0, DoubleConverter.convert("0.0"), 0);
        assertEquals(45.32, DoubleConverter.convert("0045.32"), 0);
        assertEquals(0, DoubleConverter.convert("0."), 0);
        assertEquals(0, DoubleConverter.convert(".0"), 0);
        assertEquals(0.06, DoubleConverter.convert("000.06"), 0);
        assertEquals(0.06, DoubleConverter.convert("0.0600"), 0);
        assertEquals(23.0, DoubleConverter.convert("00023."), 0);

        try {
            DoubleConverter.convert("abc");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            DoubleConverter.convert("+200");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            DoubleConverter.convert("123.A");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            DoubleConverter.convert(".");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            DoubleConverter.convert("1E6");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            DoubleConverter.convert("1e6");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        assertEquals("1.500", DoubleConverter.convert(1.5, 3));
        assertEquals("45.00000", DoubleConverter.convert(45, 5));
        assertEquals("5.00", DoubleConverter.convert(5, 2));
        assertEquals("-5.00", DoubleConverter.convert(-5, 2));
        assertEquals("-12.2345", DoubleConverter.convert(-12.2345, 3));
        assertEquals("0.0", DoubleConverter.convert(0, 1));
    }

    public void testCharConversion() throws Exception {
        assertEquals("a", CharConverter.convert('a'));
        assertEquals("1", CharConverter.convert('1'));
        assertEquals("F", CharConverter.convert('F'));
        assertEquals('a', CharConverter.convert("a"));
        assertEquals('1', CharConverter.convert("1"));
        assertEquals('F', CharConverter.convert("F"));
        try {
            CharConverter.convert("");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            CharConverter.convert("a1");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
    }

    public void testBooleanConversion() throws Exception {
        assertEquals("Y", BooleanConverter.convert(true));
        assertEquals("N", BooleanConverter.convert(false));
        assertEquals(true, BooleanConverter.convert("Y"));
        assertEquals(false, BooleanConverter.convert("N"));
        try {
            BooleanConverter.convert("D");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            BooleanConverter.convert("true");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
    }

    public void testUtcTimeStampConversion() throws Exception {
        Calendar c = new GregorianCalendar(2000, 3, 26, 12, 5, 6);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(c.getTimeInMillis());
        timestamp.setNanos(123456789);
        assertEquals("20000426-12:05:06", UtcTimestampConverter.convert(timestamp,
        		false, false));
        assertEquals("20000426-12:05:06.123", UtcTimestampConverter.convert(timestamp,
        		true, false));
        assertEquals("20000426-12:05:06.123456", UtcTimestampConverter.convert(timestamp,
        		true, true));
        assertEquals("20000426-12:05:06.123456789", UtcTimestampConverter.convert(timestamp,
                true, true, true));

        timestamp.setNanos(0);
        assertEquals("20000426-12:05:06", UtcTimestampConverter.convert(timestamp,
                false, false));
        assertEquals("20000426-12:05:06.000", UtcTimestampConverter.convert(timestamp,
                true, false));
        assertEquals("20000426-12:05:06.000000", UtcTimestampConverter.convert(timestamp,
                true, true));
        assertEquals("20000426-12:05:06.000000000", UtcTimestampConverter.convert(timestamp,
                true, true, true));

        timestamp = UtcTimestampConverter.convert("20000426-12:05:06");
        checkCalendar(timestamp, 12, 5, 6, 2000, 3, 26, 0);

        timestamp = UtcTimestampConverter.convert("20000426-12:05:06.123");
        checkCalendar(timestamp, 12, 5, 6, 2000, 3, 26, 123_000_000);

        timestamp = UtcTimestampConverter.convert("20000426-12:05:06.123456");
        checkCalendar(timestamp, 12, 5, 6, 2000, 3, 26, 123_456_000);

        timestamp = UtcTimestampConverter.convert("20000426-12:05:06.123456789");
        checkCalendar(timestamp, 12, 5, 6, 2000, 3, 26, 123_456_789);

        try {
            UtcTimestampConverter.convert("2000042x-12:05:06.123");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcTimestampConverter.convert("200004261-2:05:06.123");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcTimestampConverter.convert("20000426-1205:06.123");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcTimestampConverter.convert("20000426-12:0506.123");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcTimestampConverter.convert("20000426-12:05:06123");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
    }

    public void testUtcTimeOnlyConversion() throws Exception {
        Calendar c = new GregorianCalendar(0, 0, 0, 12, 5, 6);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(c.getTimeInMillis());
        timestamp.setNanos(123456789);
        assertEquals("12:05:06", UtcTimeOnlyConverter.convert(timestamp,
                false, false));
        assertEquals("12:05:06.123", UtcTimeOnlyConverter.convert(timestamp,
                true, false));
        assertEquals("12:05:06.123456", UtcTimeOnlyConverter.convert(timestamp,
                true, true));
        assertEquals("12:05:06.123456789", UtcTimeOnlyConverter.convert(timestamp,
                true, true, true));

        timestamp.setNanos(0);
        assertEquals("12:05:06", UtcTimeOnlyConverter.convert(timestamp,
                false, false));
        assertEquals("12:05:06.000", UtcTimeOnlyConverter.convert(timestamp,
                true, false));
        assertEquals("12:05:06.000000", UtcTimeOnlyConverter.convert(timestamp,
                true, true));
        assertEquals("12:05:06.000000000", UtcTimeOnlyConverter.convert(timestamp,
                true, true, true));

        timestamp = UtcTimeOnlyConverter.convert("12:05:06");
        checkCalendar(timestamp, 12, 5, 6, 1970, 0, 1, 0);

        timestamp = UtcTimeOnlyConverter.convert("12:05:06.123");
        checkCalendar(timestamp, 12, 5, 6, 1970, 0, 1, 123_000_000);

        timestamp = UtcTimeOnlyConverter.convert("12:05:06.123456");
        checkCalendar(timestamp, 12, 5, 6, 1970, 0, 1, 123_456_000);

        timestamp = UtcTimeOnlyConverter.convert("12:05:06.123456789");
        checkCalendar(timestamp, 12, 5, 6, 1970, 0, 1, 123_456_789);

        try {
            UtcTimeOnlyConverter.convert("I2:05:06.555");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
    }

    private void checkCalendar(Timestamp timestamp, int hourOfDay, int minute, int second, int year, int month, int dayOfMonth, int nano) {
        Calendar c = new GregorianCalendar();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.setTime(timestamp);
        assertEquals(hourOfDay, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, c.get(Calendar.MINUTE));
        assertEquals(second, c.get(Calendar.SECOND));
        assertEquals(year, c.get(Calendar.YEAR));
        assertEquals(month, c.get(Calendar.MONTH));
        assertEquals(dayOfMonth, c.get(Calendar.DAY_OF_MONTH));
        assertEquals(nano, timestamp.getNanos());
    }

    public void testUtcDateOnlyConversion() throws Exception {
        Calendar c = new GregorianCalendar(2000, 3, 26, 0, 0, 0);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.MILLISECOND, 555);
        assertEquals("20000426", UtcDateOnlyConverter.convert(c.getTime()));

        Date date = UtcDateOnlyConverter.convert("20000426");
        c.setTime(date);
        assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
        assertEquals(2000, c.get(Calendar.YEAR));
        assertEquals(3, c.get(Calendar.MONTH));
        assertEquals(26, c.get(Calendar.DAY_OF_MONTH));
        try {
            UtcDateOnlyConverter.convert("b000042b");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcDateOnlyConverter.convert("2000042");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcDateOnlyConverter.convert("200004268");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcDateOnlyConverter.convert("2000042b");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
        try {
            UtcDateOnlyConverter.convert("200k0425");
            fail();
        } catch (FieldConvertError e) {
            // expected
        }
    }

    //    void FieldConvertorsTestCase::checkSumConvertTo::onRun( void*& )
    //    {
    //      assert( CheckSumConvertor::convert( 0 ) == "000" );
    //      assert( CheckSumConvertor::convert( 5 ) == "005" );
    //      assert( CheckSumConvertor::convert( 12 ) == "012" );
    //      assert( CheckSumConvertor::convert( 234 ) == "234" );
    //
    //      try{ CheckSumConvertor::convert( -1 ); assert( false ); }
    //      catch ( FieldConvertError& ) {}
    //      try{ CheckSumConvertor::convert( 256 ); assert( false ); }
    //      catch ( FieldConvertError& ) {}}
    //    }
}
