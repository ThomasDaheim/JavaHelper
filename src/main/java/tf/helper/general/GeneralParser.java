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
package tf.helper.general;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * General parser from string to various java data types.
 * 
 * Based on the idea of https://ideone.com/WtNDN2
 * @author thomas
 */
public class GeneralParser {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    // use generic types to avoid "Object" and "?"
    public static <T> T parse(String argString, Class<T> param) {
        final Function<String, ?> func = parser.get(param);
        // in theory we could check whether the second parameter should be of type T - 
        // but that would require reflection magic which I don't want to add here
        // so if it fails, parser Map has been messed up...
        try {
            if (func != null) {
                return ObjectsHelper.uncheckedCast(func.apply(argString));
            }
            if (param.isEnum()) {
                return ObjectsHelper.uncheckedCast(Enum.valueOf((Class) param, argString));
            }
        } catch (Exception ex) {
            Logger.getLogger(GeneralParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Cannot parse string to " + param.getName());
    }

    private final static HashMap<Class<?>, Function<String,?>> parser = new HashMap<>();
    static {
        parser.put(boolean.class       , Boolean::parseBoolean);
        parser.put(byte.class          , Byte::parseByte);
        parser.put(short.class         , Short::parseShort);
        parser.put(int.class           , Integer::parseInt);
        parser.put(long.class          , Long::parseLong);
        parser.put(double.class        , Double::parseDouble);
        parser.put(float.class         , Float::parseFloat);
        parser.put(Boolean.class       , Boolean::valueOf);
        parser.put(Byte.class          , Byte::valueOf);
        parser.put(Short.class         , Short::valueOf);
        parser.put(Integer.class       , Integer::valueOf);
        parser.put(Long.class          , Long::valueOf);
        parser.put(Double.class        , Double::valueOf);
        parser.put(Float.class         , Float::valueOf);
        parser.put(String.class        , String::valueOf);
        parser.put(BigDecimal.class    , BigDecimal::new);
        parser.put(BigInteger.class    , BigInteger::new);
        parser.put(LocalDate.class     , LocalDate::parse);
        parser.put(LocalDateTime.class , LocalDateTime::parse);
        parser.put(LocalTime.class     , LocalTime::parse);
        parser.put(MonthDay.class      , MonthDay::parse);
        parser.put(OffsetDateTime.class, OffsetDateTime::parse);
        parser.put(OffsetTime.class    , OffsetTime::parse);
        parser.put(Year.class          , Year::parse);
        parser.put(YearMonth.class     , YearMonth::parse);
        parser.put(ZonedDateTime.class , ZonedDateTime::parse);
        parser.put(ZoneId.class        , ZoneId::of);
        parser.put(ZoneOffset.class    , ZoneOffset::of);
    }
}
