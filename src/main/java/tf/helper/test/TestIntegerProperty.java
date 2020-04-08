/*
 * Copyright (c) 2014ff Thomas Feuster
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tf.helper.test;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tf.helper.AppClipboard;

/**
 *
 * @author thomas
 */
public class TestIntegerProperty extends Application {
    private final IntegerProperty intProperty = new SimpleIntegerProperty(0);

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Test IntegerProperty");
        
        final StackPane stackPane = new StackPane();
        stackPane.setMinHeight(400);
        stackPane.setMaxHeight(400);
        stackPane.setPrefHeight(400);
        stackPane.setMinWidth(400);
        stackPane.setMaxWidth(400);
        stackPane.setPrefWidth(400);
        stackPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        final VBox vbox = new VBox();

        final Label valueLabel = new Label("Property: " + intProperty.getValue().toString());
        intProperty.addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                valueLabel.setText("Property: " + newValue.toString());
            } else {
                valueLabel.setText("null");
            }
        });

        final Label appLabel = new Label("Clipboard: " + AppClipboard.getInstance().putCountProperty().getValue().toString());
        AppClipboard.getInstance().putCountProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                appLabel.setText("Clipboard: " + newValue.toString());
            } else {
                appLabel.setText("null");
            }
        });
        
        vbox.getChildren().addAll(valueLabel, appLabel);

        stackPane.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        
        primaryStage.setScene(new Scene(stackPane, 200, 200));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();    
        

        final Timeline timeline1 = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            intProperty.set(intProperty.get() + 1);
        }));
        timeline1.setCycleCount(4);
        timeline1.play();
        
        final Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
             AppClipboard.getInstance().addContent(DataFormat.PLAIN_TEXT, "TEST");
        }));
        timeline2.setCycleCount(4);
        timeline1.setOnFinished(e -> timeline2.play());
    }
}
