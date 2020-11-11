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

import static java.lang.String.format;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import quickfix.FieldConvertError;

abstract class AbstractDateTimeConverter {
    protected static void assertLength(String value, int i, String type) throws FieldConvertError {
        if (value.length() != i) {
            throwFieldConvertError(value, type);
        }
    }

    protected static void assertDigitSequence(String value, int i, int j, String type)
            throws FieldConvertError {
        for (int offset = i; offset < j; offset++) {
            if (!Character.isDigit(value.charAt(offset))) {
                throwFieldConvertError(value, type);
            }
        }
    }

    protected static void assertSeparator(String value, int offset, char ch, String type)
            throws FieldConvertError {
        if (value.charAt(offset) != ch) {
            throwFieldConvertError(value, type);
        }
    }

    protected static void throwFieldConvertError(String value, String type)
            throws FieldConvertError {
        throw new FieldConvertError("invalid UTC " + type + " value: " + value);
    }

    protected static long parseLong(String s) {
        long n = 0;
        for (int i = 0; i < s.length(); i++) {
            n = (n * 10) + (s.charAt(i) - '0');
        }
        return n;
    }

    /**
     * Parses fractions of seconds to nanoseconds
     * @param value in format 3, 6 or 9 digits
     * @return nanoseconds
     */
    protected static int parseFractionOfSeconds(String value) {
        int nanosecond = 0;
        switch (value.length()) {
        case 9:
            nanosecond += parseLong(value.substring(6, 9));
        case 6:
            nanosecond += parseLong(value.substring(3, 6)) * 1_000;
        case 3:
            nanosecond += parseLong(value.substring(0, 3)) * 1_000_000;
        default:
            break;
        }
        return nanosecond;
    }

    protected enum TimePrecision {
        SECOND {
            @Override
            protected String print(int nanoseconds) {
                return "";
            }
        },
        MILLISECOND {
            @Override
            protected String print(int nanoseconds) {
                return format(".%03d", nanoseconds / 1_000_000);
            }
        },
        MICROSECOND {
            @Override
            protected String print(int nanoseconds) {
                return format(".%06d", nanoseconds / 1_000);
            }
        },
        NANOSECOND {
            @Override
            protected String print(int nanoseconds) {
                return format(".%09d", nanoseconds);
            }
        };

        /**
         * Prints fraction of seconds with dot prefix
         * @param nanoseconds
         * @return
         */
        protected abstract String print(int nanoseconds);

        protected static TimePrecision chooseSuitable(boolean includeMilliseconds, boolean includeMicroseconds, boolean includeNanoseconds) {
            if (includeNanoseconds) {
                return NANOSECOND;
            } else if (includeMicroseconds) {
                return MICROSECOND;
            } else if (includeMilliseconds) {
                return MILLISECOND;
            } else {
                return SECOND;
            }
        }
    }

    protected static DateFormat createDateFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setDateFormatSymbols(new DateFormatSymbols(Locale.US));
        return sdf;
    }

}
