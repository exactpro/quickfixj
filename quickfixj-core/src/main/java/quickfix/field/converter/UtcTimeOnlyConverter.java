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

import quickfix.FieldConvertError;

/**
 * Convert between a time and a String.
 */
public class UtcTimeOnlyConverter extends AbstractDateTimeConverter {
    // SimpleDateFormats are not thread safe. A thread local is being
    // used to maintain high concurrency among multiple session threads
    private static final ThreadLocal<DateFormat> UTC_TIMESTAMP_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return createDateFormat("HH:mm:ss");
        }
    };

    /**
     * Convert a time (represented as a Timestamp) to a String (HH:MM:SS or HH:MM:SS.SSS)
     *
     * @param timestamp the date with the time to convert
     * @param includeMilliseconds controls whether milliseconds are included in the result
     * @param includeMicroseconds controls whether microseconds are included in the result
     * @param includeNanoseconds controls whether nanoseconds are included in the result
     * @return a String representing the time.
     */
    public static String convert(Timestamp timestamp, boolean includeMilliseconds, boolean includeMicroseconds, boolean includeNanoseconds) {
        TimePrecision timePrecision = TimePrecision.chooseSuitable(includeMilliseconds, includeMicroseconds, includeNanoseconds);
        return UTC_TIMESTAMP_FORMAT.get().format(timestamp)
                + timePrecision.print(timestamp.getNanos());
    }

    /**
     * Convert a timestamp (represented as a Timestamp) to a String.
     * @param timestamp the date to convert
     * @param includeMilliseconds controls whether milliseconds are included in the result
     * @param includeMicroseconds controls whether microseconds are included in the result
     * @return the formatted timestamp
     */
    public static String convert(Timestamp timestamp, boolean includeMilliseconds, boolean includeMicroseconds) {
        return convert(timestamp, includeMilliseconds, includeMicroseconds, false);
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
            verifyFormat(value);
            d = new Timestamp(UTC_TIMESTAMP_FORMAT.get()
                    .parse(value.substring(0, 8)).getTime());
            int nanosecond = 0;
            if (value.length() > 9) {
                nanosecond = parseFractionOfSeconds(value.substring(9));
            }
            d.setNanos(nanosecond);
        } catch (ParseException e) {
            throwFieldConvertError(value, "time");
        }
        return d;
    }

    private static void verifyFormat(String value) throws FieldConvertError {
        if (value.length() != 8
                && value.length() != 12
                && value.length() != 15
                && value.length() != 18) {
            throwFieldConvertError(value, "time");
        }
    }

}
