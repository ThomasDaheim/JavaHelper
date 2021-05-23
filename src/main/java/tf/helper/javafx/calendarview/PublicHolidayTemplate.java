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
    
    public static LocalDate calculateEasterSunday(final int year) {
        final LocalDate result = LocalDate.now();
        
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
