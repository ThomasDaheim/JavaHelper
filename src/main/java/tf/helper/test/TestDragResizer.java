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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tf.helper.javafx.DragResizer;

/**
 *
 * @author thomas
 */
public class TestDragResizer extends Application {
    
    private final static String WIDTH = "Width: ";
    private final static String HEIGHT = "Height: ";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(TestDragResizer.class, args);
    }
    
    /**
     * @param primaryStage 
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Test DragResizer by dragging the stackpane in all directions");
        
        final StackPane pane = new StackPane();
        pane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        
        final StackPane stackPane = new StackPane();
        stackPane.setMinHeight(400);
        stackPane.setMaxHeight(400);
        stackPane.setPrefHeight(400);
        stackPane.setMinWidth(400);
        stackPane.setMaxWidth(400);
        stackPane.setPrefWidth(400);
        stackPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        final VBox vbox = new VBox();
        final Label widthLbl = new Label(WIDTH + "400");
        stackPane.widthProperty().addListener((ov, t, t1) -> {
            if (t != null && t.intValue() > 0) {
               widthLbl.setText(WIDTH + t.toString());
            }
        });
        final Label heightLbl = new Label(HEIGHT + "400");
        stackPane.heightProperty().addListener((ov, t, t1) -> {
            if (t != null && t.intValue() > 0) {
               heightLbl.setText(HEIGHT + t.toString());
            }
        });
        vbox.getChildren().addAll(widthLbl, heightLbl);
        stackPane.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        
        DragResizer.makeResizable(stackPane, DragResizer.ResizeArea.ALL);
        
        pane.getChildren().add(stackPane);
        
        primaryStage.setScene(new Scene(pane, 800, 800));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
