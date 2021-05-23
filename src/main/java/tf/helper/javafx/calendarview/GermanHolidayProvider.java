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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Provider for public holidays in Germany (independent of region).
 * 
 * @author thomas
 */
public class GermanHolidayProvider extends CachingProvider {
    private final static GermanHolidayProvider INSTANCE = new GermanHolidayProvider();
    
    private final List<PublicHolidayTemplate> holidays = new ArrayList<>();
    
    private GermanHolidayProvider() {
        // fill list of german holidays
        // see e.g. https://de.wikipedia.org/wiki/Gesetzliche_Feiertage_in_Deutschland
        holidays.add(new PublicHolidayTemplate("Neujahr", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.JANUARY, 1), 0));
        holidays.add(new PublicHolidayTemplate("Heilige Drei K\u00d6nige", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.JANUARY, 6), 0));
        holidays.add(new PublicHolidayTemplate("Frauentag", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.MARCH, 8), 0));
        holidays.add(new PublicHolidayTemplate("Karfreitag", PublicHolidayTemplate.HolidayType.EASTER_BASED, null, -2));
        holidays.add(new PublicHolidayTemplate("Ostersonntag", PublicHolidayTemplate.HolidayType.EASTER_BASED, null, 0));
        holidays.add(new PublicHolidayTemplate("Ostermontag", PublicHolidayTemplate.HolidayType.EASTER_BASED, null, 1));
        holidays.add(new PublicHolidayTemplate("1. Mai", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.MAY, 1), 0));
        holidays.add(new PublicHolidayTemplate("Himmelfahrt", PublicHolidayTemplate.HolidayType.EASTER_BASED, null, 39));
        holidays.add(new PublicHolidayTemplate("Pfingstsonntag", PublicHolidayTemplate.HolidayType.EASTER_BASED, null, 49));
        holidays.add(new PublicHolidayTemplate("Pfingstmontag", PublicHolidayTemplate.HolidayType.EASTER_BASED, null, 50));
        holidays.add(new PublicHolidayTemplate("Fronleichnam", PublicHolidayTemplate.HolidayType.EASTER_BASED, null, 60));
        holidays.add(new PublicHolidayTemplate("Augsburger Friedensfest", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.AUGUST, 8), 0));
        holidays.add(new PublicHolidayTemplate("Mari\u00e4 Himmelfahrt", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.AUGUST, 15), 0));
        holidays.add(new PublicHolidayTemplate("Weltkindertag", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.SEPTEMBER, 20), 0));
        holidays.add(new PublicHolidayTemplate("Tag der Deutschen Einheit", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.OCTOBER, 3), 0));
        holidays.add(new PublicHolidayTemplate("Reformationstag", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.OCTOBER, 31), 0));
        holidays.add(new PublicHolidayTemplate("Allerheiligen", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.NOVEMBER, 1), 0));
        holidays.add(new PublicHolidayTemplate("Bu\u00df- und Bettag", PublicHolidayTemplate.HolidayType.PREV_WEDNESDAY, LocalDate.of(0, Month.NOVEMBER, 23), 0));
        holidays.add(new PublicHolidayTemplate("1. Weihnachtstag", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.DECEMBER, 25), 0));
        holidays.add(new PublicHolidayTemplate("2. Weihnachtstag", PublicHolidayTemplate.HolidayType.FIXED_DATE, LocalDate.of(0, Month.DECEMBER, 26), 0));
    }
    
    public GermanHolidayProvider getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean isValidLocale(final Locale locale) {
        return Locale.GERMANY.getISO3Country().equals(locale.getISO3Country());
    }

    @Override
    public ObservableList<ICalenderEvent> getCalendarEventsForCache(Locale locale, LocalDate startDate, LocalDate endDate) {
        // calculation of german holidays - always calculate full year...
        final ObservableList<ICalenderEvent> result = FXCollections.observableArrayList();
        
        // could be a span longer than one year...
        for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
            result.addAll(getHolidaysForYear(year));
        }
        
        return result;
    }
    
    private ObservableList<ICalenderEvent> getHolidaysForYear(final int year) {
        final ObservableList<ICalenderEvent> result = FXCollections.observableArrayList();
        
        // calculate easter sunday for year
        final LocalDate easterSunday = PublicHolidayTemplate.calculateEasterSunday(year);
        
        for (PublicHolidayTemplate holiday: holidays) {
            result.add(PublicHolidayTemplate.concretePublicHoliday(holiday, year, easterSunday));
        }

        return result;
    }
}
