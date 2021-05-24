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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author thomas
 */
public class TestGermanHolidayProvider {
    private static GermanHolidayProvider provider;
    private static List<PublicHolidayTemplate> holidays;
    private static LocalDate yearStart;
    private static LocalDate yearEnd;
    
    @BeforeClass
    public static void getHolidays() {
        provider =  GermanHolidayProvider.getInstance();
        holidays = provider.getHolidays();
        
        final int year = LocalDate.now().getYear();
        yearStart = LocalDate.of(year, Month.JANUARY, 1);
        yearEnd = LocalDate.of(year, Month.DECEMBER, 31);
    }
    
    @Test
    public void TestProviderFullYearFixedDates() {
        final Map<LocalDate, List<ICalenderEvent>> events = provider.getCalendarEvents(Locale.GERMANY, yearStart, yearEnd);
        
        // events should be size of holiday templates
        Assert.assertEquals(events.size(), holidays.size());
        
        // 1st of January is a holiday
        Assert.assertTrue(events.containsKey(LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1)));
        // 15th of August is a holiday
        Assert.assertTrue(events.containsKey(LocalDate.of(LocalDate.now().getYear(), Month.AUGUST, 15)));
        // 26th of December is a holiday
        Assert.assertTrue(events.containsKey(LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 26)));
    }
    
    @Test
    public void TestProviderSingleDay() {
        final int holidayCount = holidays.size();
        final int prevCount = provider.getCachedDates().size();

        final LocalDate otherYear = LocalDate.of(1900, Month.FEBRUARY, 13);
        
        final Map<LocalDate, List<ICalenderEvent>> events = provider.getCalendarEvents(Locale.GERMANY, otherYear, otherYear);

        // nothing ever happened that day
        Assert.assertTrue(events.isEmpty());
        
        // but cache has increased
        Assert.assertEquals(provider.getCachedDates().size(), prevCount + holidayCount);
    }
    
    @Test
    public void TestProviderEasterDates() {
        // easter date for year of writing this code :-)
        LocalDate easterDate = LocalDate.of(2021, Month.APRIL, 4);
        Map<LocalDate, List<ICalenderEvent>> events = provider.getCalendarEvents(Locale.GERMANY, easterDate, easterDate);
        Assert.assertTrue(!events.isEmpty());
        Assert.assertTrue(events.containsKey(easterDate));
        Assert.assertEquals(1, events.get(easterDate).size());
        Assert.assertEquals("Ostersonntag", events.get(easterDate).get(0).getDescription().get());

        // corpus christi date for year of writing this code :-)
        easterDate = LocalDate.of(2021, Month.JUNE, 3);
        events = provider.getCalendarEvents(Locale.GERMANY, easterDate, easterDate);
        Assert.assertTrue(!events.isEmpty());
        Assert.assertTrue(events.containsKey(easterDate));
        Assert.assertEquals(1, events.get(easterDate).size());
        Assert.assertEquals("Fronleichnam", events.get(easterDate).get(0).getDescription().get());
    }
}
