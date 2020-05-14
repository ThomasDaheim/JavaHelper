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
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tf.helper.javafx.GridComboBox.GridComboAltBox;
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
        
        final FlowPane pane = new FlowPane();
        pane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        
        final VBox vBox = new VBox();
        
        final HBox hBox1 = new HBox();
        
        final GridComboAltBox<String> comboBox11 = new GridComboAltBox<>();
        comboBox11.setEditable(true);
        comboBox11.getItems().add("Test #1");
        comboBox11.getItems().add("Test #2");
        comboBox11.getItems().add("Test #3");
        hBox1.getChildren().add(comboBox11);
        
        final GridComboAltBox<String> comboBox12 = new GridComboAltBox<>();
        comboBox12.setEditable(false);
        comboBox12.getItems().add("Test #1");
        comboBox12.getItems().add("Test #2");
        comboBox12.getItems().add("Test #3");
        hBox1.getChildren().add(comboBox12);
        
        final GridComboAltBox<Integer> comboBox13 = new GridComboAltBox<>();
        comboBox13.setEditable(true);
        comboBox13.getItems().add(1);
        comboBox13.getItems().add(2);
        comboBox13.getItems().add(3);
        hBox1.getChildren().add(comboBox13);

        final HBox hBox2 = new HBox();
        
        final GridComboBox<Label> comboBox21 = new GridComboBox<>();
        comboBox21.setEditable(true);
        comboBox21.getItems().add(new Label("Test #4"));
        comboBox21.getItems().add(new Label("Test #5"));
        comboBox21.getItems().add(new Label("Test #6"));
        hBox2.getChildren().add(comboBox21);
        
        final GridComboBox<Label> comboBox22 = new GridComboBox<>();
        comboBox22.setEditable(false);
        comboBox22.getItems().add(new Label("Test #4"));
        comboBox22.getItems().add(new Label("Test #5"));
        comboBox22.getItems().add(new Label("Test #6"));
        hBox2.getChildren().add(comboBox22);
        
        final GridComboBox<Label> comboBox23 = new GridComboBox<>();
        comboBox23.setEditable(true);
        comboBox23.getItems().add(new Label("4"));
        comboBox23.getItems().add(new Label("5"));
        comboBox23.getItems().add(new Label("6"));
        hBox2.getChildren().add(comboBox23);

        final HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(
                comboBox21.getGridComboBoxSkin().getPopupContent(),
                comboBox22.getGridComboBoxSkin().getPopupContent(),
                comboBox23.getGridComboBoxSkin().getPopupContent());
        
        vBox.getChildren().addAll(hBox1, hBox2, hBox3);
        
        pane.getChildren().add(vBox);
        
        primaryStage.setScene(new Scene(pane, 800, 800));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
