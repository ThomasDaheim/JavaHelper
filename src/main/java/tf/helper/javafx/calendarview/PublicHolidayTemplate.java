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
package tf.helper.javafx.calendarview;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

/**
 * Template for a public holiday, consisting of type, date or offset.
 * No consistency checks are made between the type and the values for date and offset.
 * 
 * @author thomas
 */
public class PublicHolidayTemplate {
    public enum HolidayType {
        // holiday as always on same day
        FIXED_DATE,
        // holiday is dependent on easter date
        EASTER_BASED,
        // holiday is the prev. wednesday before a given date (buss- und bettag)
        PREV_WEDNESDAY,
        // holiday is the next monday in case of weekend (e.g. american holidays)
        NEXT_MONDAY;
    }
    
    private HolidayType holidayType;
    private LocalDate fixedDate;
    private int easterOffset;
    private String description;
    
    private PublicHolidayTemplate() {
    }
    
    public PublicHolidayTemplate(final String desc, final HolidayType type, final LocalDate date, final int offset) {
        description = desc;
        holidayType = type;
        fixedDate = date;
        easterOffset = offset;
    }
    
    public String getDescription() {
        return description;
    }
    
    public HolidayType getHolidayType() {
        return holidayType;
    }
    
    public LocalDate getFixedDate() {
        return fixedDate;
    }
    
    public int getEasterOffset() {
        return easterOffset;
    }
    
    // https://www.geeksforgeeks.org/how-to-calculate-the-easter-date-for-a-given-year-using-gauss-algorithm/
    public static LocalDate calculateEasterSunday(final int year) {
        LocalDate result;

        float A, B, C, P, Q, M, N, D, E;

        // All calculations done
        // on the basis of
        // Gauss Easter Algorithm
        
        // calculate the location of the year Y in the Metonic cycle
        A = year % 19;
        // find the number of leap days according to Julian’s calendar
        B = year % 4;
        // let’s take into account that the non-leap year is one day longer than 52 weeks
        C = year % 7;
        
        // M depends on the century of year Y. For 19th century, M = 23. For the 21st century, M = 24 and so on
        P = (float)Math.floor(year / 100);
        Q = (float)Math.floor((13 + 8 * P) / 25);
        M = (15 - Q + P - P / 4) % 30;
        
        // The difference between the number of leap days between the Julian and the Gregorian calendar
        N = (4 + P - P / 4) % 7;
        
        // The number of days to be added to March 21 to find the date of the Paschal Full Moon
        D = (19 * A + M) % 30;
        
        // the number of days from the Paschal full moon to the next Sunday
        E = (2 * B + 4 * C + 6 * D + N) % 7;
        
        // using D and E, the date of Easter Sunday is going to be March (22 + D + E)
        int days = (int)(22 + D + E);

        // corner cases: the lunar month is not exactly 30 days but a little less than 30 days
        
        // corner case, when D is 29
        if ((D == 29) && (E == 6)) {
            result = LocalDate.of(year, Month.APRIL, 19);
        }
        // corner case, when D is 28
        else if ((D == 28) && (E == 6)) {
            result = LocalDate.of(year, Month.APRIL, 18);
        } else {
            // If days > 31, move to April
            if (days > 31) {
                result = LocalDate.of(year, Month.APRIL, days - 31);
            }
            // Otherwise, stay on March
            else {
                result = LocalDate.of(year, Month.MARCH, days);
            }
        }
        
        return result;
    }
    
    public static PublicHoliday concretePublicHoliday(final PublicHolidayTemplate template, final int year, LocalDate easterSunday) {
        if (easterSunday == null) {
            easterSunday = calculateEasterSunday(year);
        }
        
        PublicHoliday date = null;
        switch (template.getHolidayType()) {
            case FIXED_DATE:
                date = new PublicHoliday(
                        LocalDate.of(year, template.getFixedDate().getMonthValue(), template.getFixedDate().getDayOfMonth()), 
                        template.getDescription());
                break;
            case EASTER_BASED:
                date = new PublicHoliday(
                        easterSunday.plusDays(template.getEasterOffset()), 
                        template.getDescription());
                break;
            case PREV_WEDNESDAY:
                date = new PublicHoliday(
                        LocalDate.of(year, template.getFixedDate().getMonthValue(), template.getFixedDate().getDayOfMonth()).
                                with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY)),
                        template.getDescription());
                break;
            case NEXT_MONDAY:
                date = new PublicHoliday(
                        LocalDate.of(year, template.getFixedDate().getMonthValue(), template.getFixedDate().getDayOfMonth()).
                                with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)),
                        template.getDescription());
                break;
            default:
        }
        
        return date;
    }
}
