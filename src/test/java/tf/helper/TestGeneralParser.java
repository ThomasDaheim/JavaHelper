/*
 *  Copyright (c) 2014ff Thomas Feuster
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tf.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.Test;
import tf.helper.general.GeneralParser;

/**
 *
 * @author thomas
 */
public class TestGeneralParser {
    @Test
    public void testGeneralParser() {
            test("true", boolean.class);
            test("123" , byte.class);
            test("123" , short.class);
            test("123" , int.class);
            test("123" , long.class);
            test("123" , double.class);
            test("123" , float.class);
            test("true", Boolean.class);
            test("123" , Byte.class);
            test("123" , Short.class);
            test("123" , Integer.class);
            test("123" , Long.class);
            test("123" , Double.class);
            test("123" , Float.class);
            test("123" , BigDecimal.class);
            test("123" , BigInteger.class);
            test("Hello World"                                  , String.class);       // String
            test("HALF_EVEN"                                    , RoundingMode.class); // enum
            test("2016"                                         , Year.class);
            test("2016-04"                                      , YearMonth.class);
            test("--04-01"                                      , MonthDay.class);
            test("2016-04-01"                                   , LocalDate.class);
            test("23:18:47"                                     , LocalTime.class);
            test("23:18:47-04:00"                               , OffsetTime.class);
            test("2016-04-01T23:18:47"                          , LocalDateTime.class);
            test("2016-04-01T23:18:47-04:00"                    , OffsetDateTime.class);
            test("2016-04-01T23:18:47-04:00[America/New_York]"  , ZonedDateTime.class);
            test("America/New_York"                             , ZoneId.class);
            test("-04:00"                                       , ZoneOffset.class);
            test("FRIDAY"                                       , DayOfWeek.class);     // enum
            test("APRIL"                                        , Month.class);         // enum
    }

    private static <T> void test(String argString, Class<T> param) {
            final T ret = GeneralParser.parse(argString, param);
            System.out.printf("%-45s -> %-45s   [%-25s -> %s]%n",
                              '"' + argString + '"', ret, param.getName(), ret.getClass().getName());
    }
}
