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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of basic cashing of calendar events. Only add, don't remove events.
 * @author thomas
 */
public abstract class CachingProvider implements ICalendarProvider {
    private final Map<LocalDate, List<ICalendarEvent>> eventCache = new HashMap<>();
    private final List<LocalDate> cachedDates = new ArrayList<>();
    
    protected abstract boolean isValidLocale(final Locale locale);
    protected abstract Map<LocalDate, List<ICalendarEvent>> getCalendarEventsForCache(final Locale locale, final LocalDate startDate, final LocalDate endDate);
    
    protected List<LocalDate> getCachedDates() {
        return cachedDates;
    }

    // default inmplementation for single date cache entries
    // can be overwritten for other strategies, e.g. returning results for the full year on a 
    // single getCalendarEventsForCache() call as a calendar would do
    protected boolean isCachedDate(final LocalDate date) {
        return cachedDates.contains(date);
    }
    
    @Override
    public Map<LocalDate, List<ICalendarEvent>> getCalendarEvents(final Locale locale, final LocalDate startDate, final LocalDate endDate) {
        if (!isValidLocale(locale)) {
            return new HashMap<>();
        }
        
        final Map<LocalDate, List<ICalendarEvent>> result = new HashMap<>();
        
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            // not yet in cache? lets add it!
            if (!isCachedDate(date) && !eventCache.containsKey(date)) {
                final Map<LocalDate, List<ICalendarEvent>> events = getCalendarEventsForCache(locale, date, date);
                eventCache.putAll(events);
                cachedDates.addAll(events.keySet());
            }
            
            if (eventCache.containsKey(date)) {
                result.put(date, eventCache.get(date));
            }
        }
        
        return result;
    }
}
