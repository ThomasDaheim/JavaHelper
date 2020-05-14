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
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tf.helper.javafx.GridComboBox.GridComboBox;

/**
 *
 * @author thomas
 */
public class TestGridComboBox extends Application {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(TestGridComboBox.class, args);
    }
    
    /**
     * @param primaryStage 
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Test GridComboBox");
        
        final StackPane pane = new StackPane();
        pane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        
        final HBox hBox = new HBox();
        
        final GridComboBox<String> comboBox1 = new GridComboBox<>();
        comboBox1.setEditable(true);
        comboBox1.getItems().add("Test #1");
        comboBox1.getItems().add("Test #2");
        comboBox1.getItems().add("Test #3");
        hBox.getChildren().add(comboBox1);
        
        final GridComboBox<String> comboBox2 = new GridComboBox<>();
        comboBox2.setEditable(false);
        comboBox2.getItems().add("Test #1");
        comboBox2.getItems().add("Test #2");
        comboBox2.getItems().add("Test #3");
        hBox.getChildren().add(comboBox2);
        
        final GridComboBox<Integer> comboBox3 = new GridComboBox<>();
        comboBox3.setEditable(true);
        comboBox3.getItems().add(1);
        comboBox3.getItems().add(2);
        comboBox3.getItems().add(3);
        hBox.getChildren().add(comboBox3);

        pane.getChildren().add(hBox);
        
        primaryStage.setScene(new Scene(pane, 800, 800));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
