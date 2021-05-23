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

import java.time.Month;
import java.time.YearMonth;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author thomas
 */
public class TestCalendarView extends Application {
    @Override
    public void start(Stage primaryStage) {
        final CalendarView calendar1 = new CalendarView();
        calendar1.setMarkWeekends(false);
        final CalendarView calendar2 = new CalendarView();
        calendar2.setMarkToday(false);
        final CalendarView calendar3 = new CalendarView(YearMonth.of(2021, Month.DECEMBER), new CalenderViewOptions().setShowWeekNumber(false));

        final CalendarView calendar4 = new CalendarView(YearMonth.now(), new CalenderViewOptions().setAdditionalMonths(2));
        
        final Text dragLbl = new Text("Drag Me");
        dragLbl.setOnDragDetected((t) -> {
            dragLbl.setStyle("-fx-stroke: red;");

            final Dragboard db = dragLbl.startDragAndDrop(TransferMode.ANY);
            final ClipboardContent content = new ClipboardContent();
            content.putString(dragLbl.getText());
            db.setContent(content);

            t.consume();
        });
        dragLbl.setOnDragDone((t) -> {
            dragLbl.setStyle("-fx-stroke: null;");
        });

        final FlowPane pane = new FlowPane();
        pane.setStyle("-fx-background-color: white;");
        pane.setPadding(new Insets(12, 12, 12, 12));
        pane.setHgap(12);
        pane.setVgap(12);
        pane.getChildren().addAll(calendar1.getCalendarView(), calendar2.getCalendarView(), calendar3.getCalendarView(), dragLbl, calendar4.getCalendarView());
        Scene scene = new Scene(pane, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // add event handlers
        final EventHandler<CalendarViewEvent> testHandler = new EventHandler<>() {
            @Override
            public void handle(CalendarViewEvent t) {
                System.out.println("Event triggered! " + t.getEventType());

                if (CalendarViewEvent.OBJECT_DROPPED.equals(t.getEventType())) {
                    Assert.assertTrue("Dropped Object should be our label", t.getDroppedObject() instanceof Text);
                }
            }
        };
                
        calendar1.addEventHandler(testHandler);
        calendar2.addEventHandler(testHandler);
        calendar3.addEventHandler(testHandler);
        calendar4.addEventHandler(testHandler);
    }
    
    @Test
    public void Test1() {
        launch();
    }    
}
