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

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static java.util.Objects.requireNonNull;
import javafx.beans.Observable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import tf.helper.general.ObjectsHelper;
import tf.helper.javafx.TooltipHelper;

/**
 * Implementation of a limited calendar view that can only show months an an excel-like grid.
 * 
 * Besides that it support highlighting of today and weekends, changing of shown calendar month
 * and the option to show addiotonal months before / after the selected one.
 * 
 * Its based on the work of https://gist.github.com/james-d/ee8a5c216fb3c6e027ea
 * 
 * @author thomas
 */
public class CalendarView implements EventTarget {
    private final ObjectProperty<YearMonth> monthProperty = new SimpleObjectProperty<>();
    
    private final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.getDefault());

    private final static PseudoClass BEFORE_DISPLAY_MONTH = PseudoClass.getPseudoClass("before-display-month");
    private final static PseudoClass AFTER_DISPLAY_MONTH = PseudoClass.getPseudoClass("after-display-month");

    private final static PseudoClass CENTER_LEFT_ALIGN = PseudoClass.getPseudoClass("center-left-align");
    private final static PseudoClass CENTER_RIGHT_ALIGN = PseudoClass.getPseudoClass("center-right-align");
    
    private final ScrollPane view;
    private final GridPane calendar;
    
    private final BooleanProperty markToday = new SimpleBooleanProperty(true);
    private final BooleanProperty markWeekends = new SimpleBooleanProperty(true);
    private final BooleanProperty showWeekNumber = new SimpleBooleanProperty(true);
    
    private final IntegerProperty additionalMonths = new SimpleIntegerProperty(0);
    
    // list of all event calendarProviders that we ask for calendarEvents
    private final ObservableList<ICalendarProvider> calendarProviders = FXCollections.<ICalendarProvider>observableArrayList();
    
    // list of all calendarEvents, stored via property extractor in order to react to any changes with a rebuild
    private final ObservableList<ICalendarEvent> calendarEvents = 
            FXCollections.<ICalendarEvent>observableArrayList(p -> new Observable[]{p.getStartDate(), p.getEndDate(), p.getDescription(), p.getStyle()});    
    
    public enum DateStyle {
        DATE_SATURDAY(PseudoClass.getPseudoClass("date-saturday")),
        DATE_SUNDAY(PseudoClass.getPseudoClass("date-sunday")),
        DATE_TODAY(PseudoClass.getPseudoClass("date-today")),
        DATE_HOLIDAY(PseudoClass.getPseudoClass("date-holiday")),
        STYLE_1(PseudoClass.getPseudoClass("style-1")),
        STYLE_2(PseudoClass.getPseudoClass("style-2")),
        STYLE_3(PseudoClass.getPseudoClass("style-3")),
        STYLE_4(PseudoClass.getPseudoClass("style-4")),
        STYLE_5(PseudoClass.getPseudoClass("style-5")),
        STYLE_6(PseudoClass.getPseudoClass("style-6")),
        STYLE_7(PseudoClass.getPseudoClass("style-7")),
        STYLE_8(PseudoClass.getPseudoClass("style-8")),
        STYLE_9(PseudoClass.getPseudoClass("style-9")),
        STYLE_10(PseudoClass.getPseudoClass("style-10"));
        
        private final PseudoClass pseudoClass;
        
        DateStyle(final PseudoClass pseudo) {
            pseudoClass = pseudo;
        }

        public PseudoClass getPseudoClass() {
            return pseudoClass;
        }
    }
    
    public CalendarView(YearMonth month, final CalenderViewOptions options) {
        view = new ScrollPane();
        view.getStylesheets().add(getClass().getResource("/tf/helper/javafx/calendarview/calendar-view.css").toExternalForm());
        view.getStyleClass().add("calendar");
        
        calendar = new GridPane();
        calendar.getStyleClass().add("calendar-grid");
        GridPane.setHgrow(calendar, Priority.ALWAYS);
        GridPane.setVgrow(calendar, Priority.ALWAYS);   
        
        // set values from options before attaching listeners :-)
        
        setLocale(options.getLocale());
        setMarkToday(options.isMarkToday());
        setMarkWeekends(options.isMarkWeekends());
        setShowWeekNumber(options.isShowWeekNumber());
        setAdditionalMonths(options.getAdditionalMonths());

        monthProperty.addListener((obs, oldMonth, newMonth) -> 
            rebuildCalendar(CalendarViewEvent.MONTH_CHANGED, null));
        
        localeProperty.addListener((obs, oldLocale, newLocale) -> 
            rebuildCalendar(CalendarViewEvent.LAYOUT_CHANGED, null));
        
        markToday.addListener((obs, oldLocale, newLocale) -> 
            rebuildCalendar(CalendarViewEvent.LAYOUT_CHANGED, null));
        
        markWeekends.addListener((obs, oldLocale, newLocale) -> 
            rebuildCalendar(CalendarViewEvent.LAYOUT_CHANGED, null));
        
        showWeekNumber.addListener((obs, oldLocale, newLocale) -> 
            rebuildCalendar(CalendarViewEvent.LAYOUT_CHANGED, null));
        
        additionalMonths.addListener((obs, oldLocale, newLocale) -> 
            rebuildCalendar(CalendarViewEvent.LAYOUT_CHANGED, null));

        calendarProviders.addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends ICalendarProvider> change) {
                rebuildCalendar(CalendarViewEvent.PROVIDER_CHANGED, change);
            }
        });

        calendarEvents.addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends ICalendarEvent> change) {
                rebuildCalendar(CalendarViewEvent.EVENT_CHANGED, change);
            }
        });

        view.setContent(calendar);
        
        // triggers building calendar
        setMonth(month);
    }
    
    public CalendarView() {
        this(YearMonth.now(), new CalenderViewOptions());
    }
    
    public void rebuildCalendar() {
        rebuildCalendar(CalendarViewEvent.FORCED_REBUILD, null);
    }
    
    public void nextMonth() {
        monthProperty.set(monthProperty.get().plusMonths(1));
    }
    
    public void previousMonth() {
        monthProperty.set(monthProperty.get().minusMonths(1));
    }
    
    private void rebuildCalendar(final EventType<CalendarViewEvent> changeReason, final ListChangeListener.Change<?> change) {
        calendar.getChildren().clear();
        
        // loop through the additional months and add all calendars to grid - with proper offset
        // we need 7 days + 1 space + 1 weeknum cols per month
        final int offsetIncr = (showWeekNumber.get() ? 1 : 0) + 7 + 1;
        int calendarOffset = 0;
        for (int i = -additionalMonths.get(); i <= additionalMonths.get(); i++) {
            addCalendar(monthProperty.get().plusMonths(i), calendarOffset);
            
            calendarOffset += offsetIncr;

            if (i < additionalMonths.get()) {
                // add col with empty label before the next calendar
                final Label label = new Label("");
                label.setMaxWidth(Double.MAX_VALUE);
                label.getStyleClass().add("calendar-separator");
                GridPane.setHalignment(label, HPos.CENTER);
                calendar.add(label, calendarOffset - 1, 0);
            }
        }

        // go, tell it to the mountains
        fireEvent(new CalendarViewEvent(changeReason, this, change));
    }

    private void addCalendar(final YearMonth month, final int calendarOffset) {
        // setting up the required variables
        final WeekFields weekFields = WeekFields.of(localeProperty.get());

        final LocalDate first = month.atDay(1);
        final int dayOfWeekOfFirst = first.get(weekFields.dayOfWeek());
        final LocalDate firstDisplayedDate = first.minusDays(dayOfWeekOfFirst - 1);

        final LocalDate last = month.atEndOfMonth();
        // make sure we always show 6 weeks to have multiple months in equal height...
        final LocalDate lastDisplayedDate = firstDisplayedDate.plusWeeks(5).plusDays(6);

        final TemporalField woy = WeekFields.of(localeProperty.get()).weekOfWeekBasedYear(); 

        // only check for current date if really necessary
        final boolean doMarkToday = 
                (markToday.get() && 
                firstDisplayedDate.minusDays(1).isBefore(LocalDate.now()) && 
                lastDisplayedDate.plusDays(1).isAfter(LocalDate.now()));
        
        // in case we show the week numbers things are shifted one to the right - on top of the real offset for this calendar
        final int colOffset = (showWeekNumber.get() ? 1 : 0) + calendarOffset;

        int rowOffset = 0;
        // add header with month name and controls (if required, e.g. for "current" month)
        addMonthNameRow(month, rowOffset, colOffset, month.equals(monthProperty.get()));
        
        rowOffset++;
        // column headers:
        for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {
            LocalDate date = first.minusDays(dayOfWeekOfFirst - dayOfWeek);
            DayOfWeek day = date.getDayOfWeek();
            final Label label = new Label(day.getDisplayName(TextStyle.SHORT_STANDALONE, localeProperty.get()));
            label.setMaxWidth(Double.MAX_VALUE);
            label.getStyleClass().add("calendar-day-header");
            GridPane.setHalignment(label, HPos.CENTER);
            calendar.add(label, dayOfWeek - 1 + colOffset, rowOffset);
        }
        
        rowOffset++;
        // and now for the calendar itself
        int weekNum = -1;
        for (LocalDate date = firstDisplayedDate; ! date.isAfter(lastDisplayedDate); date = date.plusDays(1)) {
            final int dayOfWeek = date.get(weekFields.dayOfWeek());
            final int daysSinceFirstDisplayed = (int) firstDisplayedDate.until(date, ChronoUnit.DAYS);
            final int weeksSinceFirstDisplayed = daysSinceFirstDisplayed / 7;

            final Label label = getDateLabel(date, first, last, doMarkToday);
            GridPane.setHalignment(label, HPos.CENTER);
            calendar.add(label, dayOfWeek - 1 + colOffset, weeksSinceFirstDisplayed + rowOffset);
            
            // check calendarProviders if any styles to apply
            applyProviderStyles(date, label);
            
            // check if any calendarEvents on that date
            applyEventStyles(date, label);

            // in case of weeknum change add it in the first column
            if (showWeekNumber.get()) {
                final int curWeekNum = date.get(woy);
                if (weekNum != curWeekNum) {
                    final Label weekLabel = getWeekNumLabel(curWeekNum);
                    GridPane.setHalignment(weekLabel, HPos.CENTER);
                    calendar.add(weekLabel, calendarOffset, weeksSinceFirstDisplayed + rowOffset);
                    
                    weekNum = curWeekNum;
                }
            }
        }
    }
    
    private Label getDateLabel(final LocalDate date, final LocalDate first, final LocalDate last, final boolean doMarkToday) {
        final Label label = new Label(String.valueOf(date.getDayOfMonth()));
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add("calendar-cell");
        label.pseudoClassStateChanged(BEFORE_DISPLAY_MONTH, date.isBefore(first));
        label.pseudoClassStateChanged(AFTER_DISPLAY_MONTH, date.isAfter(last));
        if (markWeekends.get()) {
            label.pseudoClassStateChanged(DateStyle.DATE_SATURDAY.getPseudoClass(), date.getDayOfWeek().getValue() == 6);
            label.pseudoClassStateChanged(DateStyle.DATE_SUNDAY.getPseudoClass(), date.getDayOfWeek().getValue() == 7);
        }
        if (doMarkToday) {
            label.pseudoClassStateChanged(DateStyle.DATE_TODAY.getPseudoClass(), date.equals(LocalDate.now()));
        }
        label.setUserData(date);
        label.setOnDragOver((t) -> {
            t.acceptTransferModes(TransferMode.ANY);
            t.consume();
        });
        label.setOnDragDropped((t) -> {
            t.setDropCompleted(true);
            t.consume();

            // go, tell it to the mountains
            fireEvent(new CalendarViewEvent(CalendarViewEvent.OBJECT_DROPPED, this, t.getGestureSource(), (LocalDate) label.getUserData()));
        });

        return label;
    }
    
    private Label getWeekNumLabel(final int curWeekNum) {
        // https://stackoverflow.com/a/8023718 - fastest solution out there to show leading "0"
        final Label weekLabel = new Label(((curWeekNum<10)?"0":"") + curWeekNum);
        weekLabel.setMaxWidth(Double.MAX_VALUE);
        weekLabel.getStyleClass().add("calendar-weeknum");
        
        return weekLabel;
    }
    
    private void addMonthNameRow(final YearMonth month, final int rowOffset, final int colOffset, final boolean withControls) {
        if (withControls) {
            // someone ordered the full menu!
            // month/year comboboxes with < > buttons as first row over all cols
            final Button prevMonth = new Button("<");
            prevMonth.setOnAction((t) -> {
                setMonth(month.minusMonths(1));
                t.consume();
            });
            prevMonth.setMaxWidth(Double.MAX_VALUE);
            prevMonth.getStyleClass().add("calendar-header-button");
            GridPane.setHalignment(prevMonth, HPos.CENTER);
            calendar.add(prevMonth, colOffset, rowOffset);

            final ComboBox<String> monthBox = new ComboBox<>();
            monthBox.setMaxWidth(Double.MAX_VALUE);
            monthBox.getStyleClass().add("calendar-combo-box");
            monthBox.pseudoClassStateChanged(CENTER_RIGHT_ALIGN, true);
            monthBox.setEditable(false);
            monthBox.setPromptText(month.format(DateTimeFormatter.ofPattern("MMMM", localeProperty.get())));
            // and now add all month names in that locale
            // https://memorynotfound.com/java-get-list-month-names-locale/
            final ObservableList<String> monthNames = FXCollections.<String>observableArrayList();
            final DateFormatSymbols dfs = new DateFormatSymbols(localeProperty.get());
            for (String monthName : dfs.getMonths()) {
                // we don't want undicember in the list!!!
                if (!monthName.isEmpty()) {
                    monthNames.add(monthName);
                }
            }
            // hack: scroll to selection when list is shown
            monthBox.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                if (isNowShowing) {
                    // set focus on selected item
                    if (monthBox.getSelectionModel().getSelectedIndex() > -1) {
                        // https://stackoverflow.com/a/47933342
                        final ListView<String> lv = ObjectsHelper.uncheckedCast(((ComboBoxListViewSkin) monthBox.getSkin()).getPopupContent());
                        lv.scrollTo(monthBox.getSelectionModel().getSelectedItem());
                    }
                }
            });
            monthBox.setItems(monthNames);
            monthBox.setValue(monthBox.getPromptText());
            // update calendar when new value is selected - only after setValue has been done :-)
            monthBox.valueProperty().addListener((obs, oldMonth, newMonth) -> {
                if (newMonth != null && !newMonth.equals(oldMonth)) {
                    setMonth(YearMonth.of(getMonth().getYear(), monthBox.getSelectionModel().getSelectedIndex()+1));
                }
            });
            GridPane.setHalignment(monthBox, HPos.CENTER);
            calendar.add(monthBox, colOffset+1, rowOffset, 3, 1);

            final ComboBox<String> yearBox = new ComboBox<>();
            yearBox.setMaxWidth(Double.MAX_VALUE);
            yearBox.getStyleClass().add("calendar-combo-box");
            yearBox.pseudoClassStateChanged(CENTER_LEFT_ALIGN, true);
            yearBox.setEditable(false);
            yearBox.setPromptText(month.format(DateTimeFormatter.ofPattern("yyyy", localeProperty.get())));
            // and now add all years to the list (all = requested year +/- 10 years)
            final ObservableList<String> yearNames = FXCollections.<String>observableArrayList();
            for (int i = month.getYear()+10; i >= month.getYear()-10; i--) {
                yearNames.add(String.valueOf(i));
            }
            // hack: scroll to selection when list is shown
            yearBox.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                if (isNowShowing) {
                    // set focus on selected item
                    if (yearBox.getSelectionModel().getSelectedIndex() > -1) {
                        // https://stackoverflow.com/a/47933342
                        final ListView<String> lv = ObjectsHelper.uncheckedCast(((ComboBoxListViewSkin) yearBox.getSkin()).getPopupContent());
                        lv.scrollTo(yearBox.getSelectionModel().getSelectedItem());
                    }
                }
            });
            yearBox.setItems(yearNames);
            yearBox.setValue(yearBox.getPromptText());
            // update calendar when new value is selected - only after setValue has been done :-)
            yearBox.valueProperty().addListener((obs, oldYear, newYear) -> {
                if (newYear != null && !newYear.equals(oldYear)) {
                    setMonth(YearMonth.of(Integer.parseInt(newYear), getMonth().getMonthValue()));
                }
            });
            GridPane.setHalignment(yearBox, HPos.CENTER);
            calendar.add(yearBox, colOffset+4, rowOffset, 2, 1);

            final Button nextMonth = new Button(">");
            nextMonth.setOnAction((t) -> {
                setMonth(month.plusMonths(1));
                t.consume();
            });
            nextMonth.setMaxWidth(Double.MAX_VALUE);
            nextMonth.getStyleClass().add("calendar-header-button");
            GridPane.setHalignment(nextMonth, HPos.CENTER);
            calendar.add(nextMonth, colOffset+6, rowOffset);
        } else {
            // show only the name of the month as a label
            final Label monthName = new Label(month.format(DateTimeFormatter.ofPattern("MMMM yyyy", localeProperty.get())));
            monthName.setMaxWidth(Double.MAX_VALUE);
            monthName.getStyleClass().add("calendar-header");
            GridPane.setHalignment(monthName, HPos.CENTER);
            calendar.add(monthName, colOffset, rowOffset, 7, 1);
        }
    }
    
    private void applyProviderStyles(final LocalDate date, final Label label) {
        String toolText = "";
        for (ICalendarProvider provider : calendarProviders) {
            final List<ICalendarEvent> providerEvents = provider.getCalendarEvents(localeProperty.get(), date, date).get(date);
            if (providerEvents == null || providerEvents.isEmpty()) {
                continue;
            }
            for (ICalendarEvent event : providerEvents) {
                label.pseudoClassStateChanged(event.getStyle().get().getPseudoClass(), true);
                if (!toolText.isEmpty()) {
                    toolText += ", ";
                }
                toolText += event.getDescription().get();
            }
        }
        if (!toolText.isEmpty()) {
            Tooltip tooltip = label.getTooltip();
            if (tooltip == null) {
                tooltip = new Tooltip(toolText);
                tooltip.getStyleClass().add("calendar-tooltip");
                TooltipHelper.updateTooltipBehavior(tooltip, 0, 10000, 0, true);
            } else {
                tooltip.setText(tooltip.getText() + "; " + toolText);
            }
            label.setTooltip(tooltip);
        }
    }
    
    private void applyEventStyles(final LocalDate date, final Label label) {
        String toolText = "";
        for (ICalendarEvent event : calendarEvents) {
            // date is either between start end or @ start or end
            if ((date.isAfter(event.getStartDate().get()) && date.isBefore(event.getEndDate().get()))
                    || date.equals(event.getStartDate().get()) || date.equals(event.getEndDate().get())) {
                label.pseudoClassStateChanged(event.getStyle().get().getPseudoClass(), true);
                if (!toolText.isEmpty()) {
                    toolText += ", ";
                }
                toolText += event.getDescription().get();
            }
        }
        if (!toolText.isEmpty()) {
            Tooltip tooltip = label.getTooltip();
            if (tooltip == null) {
                tooltip = new Tooltip(toolText);
                tooltip.getStyleClass().add("calendar-tooltip");
                TooltipHelper.updateTooltipBehavior(tooltip, 0, 10000, 0, true);
            } else {
                tooltip.setText(tooltip.getText() + "; " + toolText);
            }
            label.setTooltip(tooltip);
        }
    }
    
    public final ScrollPane getCalendarView() {
        return view;
    }

    public final GridPane getCalendarGrid() {
        return calendar;
    }

    public final ObjectProperty<YearMonth> monthProperty() {
        return monthProperty;
    }

    public final YearMonth getMonth() {
        return monthProperty().get();
    }

    public final void setMonth(final YearMonth month) {
        monthProperty().set(month);
    }

    public final ObjectProperty<Locale> localeProperty() {
        return localeProperty;
    }

    public final Locale getLocale() {
        return localeProperty().get();
    }

    public final void setLocale(final Locale locale) {
        localeProperty().set(locale);
    }

    public boolean isMarkToday() {
        return markToday.get();
    }

    public final void setMarkToday(final boolean mark) {
        markToday.set(mark);
    }

    public boolean isMarkWeekends() {
        return markWeekends.get();
    }

    public final void setMarkWeekends(final boolean mark) {
        markWeekends.set(mark);
    }

    public boolean isShowWeekNumber() {
        return showWeekNumber.get();
    }

    public final void setShowWeekNumber(final boolean show) {
        showWeekNumber.set(show);
    }
    
    public int getAdditionalMonths() {
        return additionalMonths.get();
    }
    
    public final void setAdditionalMonths(final int months) {
        additionalMonths.set(months);
    }
    
    public void addCalendarProviders(final List<ICalendarProvider> providers) {
        calendarProviders.addAll(providers);
    }
    
    public void removeCalendarProviders(final List<ICalendarProvider> providers) {
        calendarProviders.removeAll(providers);
    }
    
    public void addCalendarEvents(final List<ICalendarEvent> evts) {
        calendarEvents.addAll(evts);
    }
    
    public void removeCalendarEvents(final List<ICalendarEvent> evts) {
        calendarEvents.removeAll(evts);
    }
    
    // support for calendar calendarEvents
    // based on CalendarFX

    private final ObservableList<EventHandler<CalendarViewEvent>> eventHandlers = FXCollections.observableArrayList();

    /**
     * Adds an event handler for calendar events. Handlers will be called when
     * an entry gets added, removed, changes, etc.
     *
     * @param l the event handler to add
     */
    public final void addEventHandler(EventHandler<CalendarViewEvent> l) {
        if (l != null) {
            eventHandlers.add(l);
        }
    }

    /**
     * Removes an event handler from the calendar.
     *
     * @param l the event handler to remove
     */
    public final void removeEventHandler(EventHandler<CalendarViewEvent> l) {
        if (l != null) {
            eventHandlers.remove(l);
        }
    }

    /**
     * Fires the given calendar event to all event handlers currently registered
     * with this calendar.
     *
     * @param evt the event to fire
     */
    public final void fireEvent(CalendarViewEvent evt) {
        requireNonNull(evt);
        Event.fireEvent(this, evt);
    }

    @Override
    public final EventDispatchChain buildEventDispatchChain(EventDispatchChain givenTail) {
        return givenTail.append((event, tail) -> {
            if (event instanceof CalendarViewEvent) {
                for (EventHandler<CalendarViewEvent> handler : eventHandlers) {
                    handler.handle((CalendarViewEvent) event);
                }
            }

            return event;
        });
    }
}
