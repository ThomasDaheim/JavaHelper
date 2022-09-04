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

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;
import java.util.GregorianCalendar;

/**
 * Helper functions around date, time, calendar.
 * 
 * @author thomas
 */
public class DateTimeCalendarHelper {
    public static GregorianCalendar roundCalendarToInterval(GregorianCalendar input, TemporalField roundTo, int roundIncrement) {
        return GregorianCalendar.from(roundDateTimeToInterval(input.toZonedDateTime(), roundTo, roundIncrement));
    }
    
    // https://stackoverflow.com/a/37423588
    public static ZonedDateTime roundDateTimeToInterval(ZonedDateTime input, TemporalField roundTo, int roundIncrement) {
        /* Extract the field being rounded. */
        int field = input.get(roundTo);

        /* Distance from previous floor. */
        int r = field % roundIncrement;

        /* Find floor and ceiling. Truncate values to base unit of field. */
        ZonedDateTime ceiling = 
            input.plus(roundIncrement - r, roundTo.getBaseUnit())
            .truncatedTo(roundTo.getBaseUnit());

        ZonedDateTime floor = 
            input.plus(-r, roundTo.getBaseUnit())
            .truncatedTo(roundTo.getBaseUnit());

        /*
         * Do a half-up rounding.
         * 
         * If (input - floor) < (ceiling - input) 
         * (i.e. floor is closer to input than ceiling)
         *  then return floor, otherwise return ceiling.
         */
        return Duration.between(floor, input).compareTo(Duration.between(input, ceiling)) < 0 ? floor : ceiling;
    }    
}
