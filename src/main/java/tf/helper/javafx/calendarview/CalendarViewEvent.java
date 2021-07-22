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
import static java.util.Objects.requireNonNull;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Events that can be fired by a CalendarView.
 * 
 * Enables users to subscribe to certain changes in a CalendarView.
 * 
 * @author thomas
 */
public class CalendarViewEvent extends Event {
    /**
     * The supertype of all event types in this event class.
     */
    public static final EventType<CalendarViewEvent> ANY = new EventType<>(
            Event.ANY, "ANY");

    /**
     * An event type used to inform the application that "something" inside the
     * calendar has changed and that the views need to update their visuals
     * accordingly (brute force update).
     */
    public static final EventType<CalendarViewEvent> CALENDAR_CHANGED = new EventType<>(
            CalendarViewEvent.ANY, "CALENDAR_CHANGED");

    /**
     * An event type used to inform the application that the selected month has changed.
     */
    public static final EventType<CalendarViewEvent> MONTH_CHANGED = new EventType<>(
            CalendarViewEvent.CALENDAR_CHANGED, "MONTH_CHANGED");

    /**
     * An event type used to inform the application that the calendar layout has changed.
     */
    public static final EventType<CalendarViewEvent> LAYOUT_CHANGED = new EventType<>(
            CalendarViewEvent.CALENDAR_CHANGED, "LAYOUT_CHANGED");

    /**
     * An event type used to inform the application that the calendar was asked to rebuild.
     */
    public static final EventType<CalendarViewEvent> FORCED_REBUILD = new EventType<>(
            CalendarViewEvent.CALENDAR_CHANGED, "FORCED_REBUILD");

    /**
     * An event type used to inform the application that the list of event providers has changed.
     */
    public static final EventType<CalendarViewEvent> PROVIDER_CHANGED = new EventType<>(
            CalendarViewEvent.CALENDAR_CHANGED, "PROVIDER_CHANGED");

    /**
     * An event type used to inform the application that the list of events has changed.
     */
    public static final EventType<CalendarViewEvent> EVENT_CHANGED = new EventType<>(
            CalendarViewEvent.CALENDAR_CHANGED, "EVENT_CHANGED");

    /**
     * An event type used to inform the application that "something" was dropped on a day
     * of the calendar.
     */
    public static final EventType<CalendarViewEvent> OBJECT_DROPPED = new EventType<>(
            CalendarViewEvent.ANY, "OBJECT_DROPPED");

    private final CalendarView calendar;
    private Object droppedObject = null;
    private LocalDate dropDate = null;
    private ListChangeListener.Change<?> change = null;

    /**
     * Constructs a new event.
     *
     * @param eventType the event type
     * @param cal       the calendar where the event occurred.
     */
    protected CalendarViewEvent(EventType<? extends CalendarViewEvent> eventType,
                            CalendarView cal) {
        super(cal, cal, eventType);

        calendar = requireNonNull(cal);
    }

    /**
     * Constructs a new event.
     *
     * @param eventType the event type
     * @param cal       the calendar where the event occurred
     * @param chng      the list change that occured
     */
    protected CalendarViewEvent(EventType<? extends CalendarViewEvent> eventType,
                            CalendarView cal, ListChangeListener.Change<?> chng) {
        super(cal, cal, eventType);

        calendar = requireNonNull(cal);
        // chng can be null
        change = chng;
    }

    /**
     * Constructs a new event.
     *
     * @param eventType the event type
     * @param cal       the calendar where the event occurred.
     * @param object    the object that was dropped
     * @param target    the date it was dropped on
     */
    protected CalendarViewEvent(EventType<? extends CalendarViewEvent> eventType,
                         CalendarView cal, Object object, LocalDate target) {
        super(cal, cal, eventType);

        calendar = requireNonNull(cal);
        droppedObject = requireNonNull(object);
        dropDate = requireNonNull(target);
    }
    
    public CalendarView getCalendarView() {
        return calendar;
    }
    
    public Object getDroppedObject() {
        return droppedObject;
    }
    
    public LocalDate getDropDate() {
        return dropDate;
    }
}
