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
 * A minimal implementation of a calendar event.
 * 
 * @author thomas
 */
public class SimpleDayEvent implements ICalendarEvent {
    private final ObjectProperty<CalendarView.DateStyle> myStyle = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> myDate = new SimpleObjectProperty<>();
    private final StringProperty myDescription = new SimpleStringProperty();

    private SimpleDayEvent() {
    }

    public SimpleDayEvent(final LocalDate date, final String desc, final CalendarView.DateStyle style) {
        myDate.set(date);
        myDescription.set(desc);
        myStyle.set(style);
    }

    @Override
    public ObjectProperty<LocalDate> getStartDate() {
        return myDate;
    }

    @Override
    public ObjectProperty<LocalDate> getEndDate() {
        return myDate;
    }

    @Override
    public ObjectProperty<CalendarView.DateStyle> getStyle() {
        return myStyle;
    }

    @Override
    public StringProperty getDescription() {
        return myDescription;
    }
}
