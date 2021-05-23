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

import java.util.Locale;

/**
 * Holder for options that can be passed to a CalendarView.
 * 
 * All options can be set individually on CalendarView s well. This only providesa a shortcut-route to do so.
 * This uses fluent interfaces to simplify setting of the options.
 * 
 * @author thomas
 */
public class CalenderViewOptions {
    private Locale locale = Locale.getDefault();
    private boolean markToday = true;
    private boolean markWeekends = true;
    private boolean showWeekNumber = true;
    private int additionalMonths = 0;

    public CalenderViewOptions() {
    }

    public Locale getLocale() {
        return locale;
    }

    public CalenderViewOptions setLocale(final Locale loc) {
        locale = loc;
        return this;
    }

    public boolean isMarkToday() {
        return markToday;
    }

    public CalenderViewOptions setMarkToday(boolean mark) {
        markToday = mark;
        return this;
    }

    public boolean isMarkWeekends() {
        return markWeekends;
    }

    public CalenderViewOptions setMarkWeekends(boolean mark) {
        markWeekends = mark;
        return this;
    }

    public boolean isShowWeekNumber() {
        return showWeekNumber;
    }

    public CalenderViewOptions setShowWeekNumber(boolean show) {
        showWeekNumber = show;
        return this;
    }

    public int getAdditionalMonths() {
        return additionalMonths;
    }

    public CalenderViewOptions setAdditionalMonths(int months) {
        additionalMonths = months;
        return this;
    }
}
