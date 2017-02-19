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

package quickfix.field.converter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import quickfix.FieldConvertError;

/**
 * Convert between a time and a String.
 */
public class UtcTimeOnlyConverter extends AbstractDateTimeConverter {
    // SimpleDateFormats are not thread safe. A thread local is being
    // used to maintain high concurrency among multiple session threads
    private static final ThreadLocal<UtcTimeOnlyConverter> utcTimeConverter = new ThreadLocal<UtcTimeOnlyConverter>();
    private final DateFormat utcTimeFormat = createDateFormat("HH:mm:ss");
    private final DateFormat utcTimeFormatMillis = createDateFormat("HH:mm:ss.SSS");

    /**
     * Convert a time (represented as a Timestamp) to a String (HH:MM:SS or HH:MM:SS.SSS)
     *
     * @param d the date with the time to convert
     * @param includeMilliseconds controls whether milliseconds are included in the result
     * @param includeMicroseconds controls whether microseconds are included in the result
     * @return a String representing the time.
     */
    public static String convert(Timestamp d, boolean includeMilliseconds, boolean includeMicroseconds) {
    	includeMilliseconds = includeMicroseconds || includeMilliseconds;
    	String formattedDate = getFormatter(includeMilliseconds).format(d);

        if (includeMicroseconds) {
            int micro = d.getNanos() / 1000 % 1000;
            return formattedDate + String.format("%03d", micro);
        }

        return formattedDate;
    }

    private static DateFormat getFormatter(boolean includeMillis) {
        UtcTimeOnlyConverter converter = utcTimeConverter.get();
        if (converter == null) {
            converter = new UtcTimeOnlyConverter();
            utcTimeConverter.set(converter);
        }
        return includeMillis ? converter.utcTimeFormatMillis : converter.utcTimeFormat;
    }

    /**
     * Convert between a String and a time
     *
     * @param value the string to parse
     * @return a date object representing the time
     * @throws FieldConvertError raised for invalid time string
     */
    public static Timestamp convert(String value) throws FieldConvertError {
    	Timestamp d = null;
        try {
        	int nanosecond = 0;
        	Date date = null;
        	switch (value.length()) {
        	case 15:
        		nanosecond += parseLong(value.substring(13, 15)) * 1_000;
        	case 12:
        		date = getFormatter(true).parse(value.substring(0, 12));
        		break;
        	case 8:
        		date = getFormatter(false).parse(value);
				break;
			default:
				throwFieldConvertError(value, "time");
			}
        	d = new Timestamp(date.getTime());
        	nanosecond += d.getNanos();
        	d.setNanos(nanosecond);
        } catch (ParseException e) {
            throwFieldConvertError(value, "time");
        }
        return d;
    }

}
