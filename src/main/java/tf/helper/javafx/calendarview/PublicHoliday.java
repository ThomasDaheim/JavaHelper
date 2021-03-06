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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A concrete public holiday.
 * 
 * @author thomas
 */
public class PublicHoliday implements ICalendarEvent {
    public final static ObjectProperty<CalendarView.DateStyle> style = new SimpleObjectProperty<>(CalendarView.DateStyle.DATE_HOLIDAY);
    private final ObjectProperty<LocalDate> holidayDate = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();

    private PublicHoliday() {
    }

    public PublicHoliday(final LocalDate date, final String desc) {
        holidayDate.set(date);
        description.set(desc);
    }

    @Override
    public ObjectProperty<LocalDate> getStartDate() {
        return holidayDate;
    }

    @Override
    public ObjectProperty<LocalDate> getEndDate() {
        return holidayDate;
    }

    @Override
    public ObjectProperty<CalendarView.DateStyle> getStyle() {
        return style;
    }

    @Override
    public StringProperty getEventDescription() {
        return description;
    }
}
