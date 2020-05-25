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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tf.helper.javafx.GridComboBox;

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

        // for comparison: standard combobox
        final HBox hBox1 = new HBox(12);
        
        final ComboBox<String> comboBox11 = new ComboBox<>();
        comboBox11.setEditable(true);
        comboBox11.getItems().add("Test #1");
        comboBox11.getItems().add("Test #2");
        comboBox11.getItems().add("Test #3");
        comboBox11.setValue("Test #1");
        
        final ComboBox<Integer> comboBox12 = new ComboBox<>();
        comboBox12.setEditable(false);
        comboBox12.getItems().add(4);
        comboBox12.getItems().add(5);
        comboBox12.getItems().add(6);
        comboBox12.setValue(4);

        final ComboBox<String> comboBox13 = new ComboBox<>();
        comboBox13.setEditable(true);
        comboBox13.getItems().add("Test #7");
        comboBox13.getItems().add("Test #8");
        comboBox13.getItems().add("Test #9");
        comboBox13.getItems().add("Test #10");
        comboBox13.getItems().add("Test #11");
        comboBox13.getItems().add("Test #12");
        comboBox13.getItems().add("Test #13");
        comboBox13.getItems().add("Test #14");
        comboBox13.getItems().add("Test #15");
        comboBox13.setValue("Test #10");
        
        hBox1.getChildren().addAll(comboBox11, comboBox12, comboBox13);

        // our new, enhanced, glorious combobox!
        final HBox hBox2 = new HBox(12);
        
        final GridComboBox<Label> comboBox21 = new GridComboBox<>();
        comboBox21.setEditable(true);
        comboBox21.setGridConverter(GridComboBox.labelStringConverter());
        comboBox21.add(new Label("Test #1"), 0, 0);
        comboBox21.add(new Label("Test #2"), 0, 1);
        comboBox21.add(new Label("Test #3"), 0, 2);
        comboBox21.setValue("Test #1");
        ColumnConstraints column21 = new ColumnConstraints();
        column21.setHgrow(Priority.ALWAYS);
        comboBox21.getColumnConstraints().addAll(column21);
        comboBox21.getGridItems().forEach((node) -> {
            GridPane.setHgrow(node, Priority.ALWAYS);
            GridPane.setHalignment(node, HPos.CENTER);
        });
        
        final GridComboBox<Label> comboBox22 = new GridComboBox<>();
        comboBox22.setGridConverter(GridComboBox.labelStringConverter());
        comboBox22.setEditable(false);
        comboBox22.add(new Label("4"), 0, 0);
        comboBox22.getItems().add("a");
        comboBox22.add(new Label("5"), 0, 2);
        comboBox22.getItems().add("b");
        comboBox22.add(new Label("6"), 0, 4);
        comboBox22.getItems().add("c");
        comboBox22.setValue("4");
        ColumnConstraints column22 = new ColumnConstraints();
        column22.setHgrow(Priority.ALWAYS);
        comboBox22.getColumnConstraints().addAll(column22);
        comboBox22.getGridItems().forEach((node) -> {
            GridPane.setHgrow(node, Priority.ALWAYS);
        });
        
        final GridComboBox<Label> comboBox23 = new GridComboBox<>();
        comboBox23.setGridConverter(GridComboBox.labelStringConverter());
        comboBox23.setEditable(true);
//        comboBox23.setGridLinesVisible(true);
        comboBox23.setHgap(0.0);
        comboBox23.setVgap(0.0);
        comboBox23.setVisibleRowCount(100);
        comboBox23.setResizeContentColumnSpan(true);
        comboBox23.add(new Label("Test #7"), 0, 0);
        comboBox23.add(new Label("Test #8"), 1, 0);
        comboBox23.add(new Label("Test #9"), 0, 1, 3, 1);
        final Label label = new Label("Header");
        label.setDisable(true);
        comboBox23.add(label, 0, 2, 2, 1);
        comboBox23.add(new Label("Test #10"), 0, 3);
        comboBox23.add(new Label("Test #11"), 0, 4);
        comboBox23.add(new Label("Test #12"), 0, 5);
        comboBox23.add(new Label("Test #13"), 0, 6);
        comboBox23.add(new Label("Test #14"), 1, 6);
        comboBox23.add(new Label("Test #15"), 2, 6);
        comboBox23.setValue("Test #9");
        ColumnConstraints column231 = new ColumnConstraints();
        column231.setHgrow(Priority.ALWAYS);
        ColumnConstraints column232 = new ColumnConstraints();
        column232.setHgrow(Priority.ALWAYS);
        ColumnConstraints column233 = new ColumnConstraints();
        column233.setHgrow(Priority.ALWAYS);
        comboBox23.getColumnConstraints().addAll(column231, column232, column233);
        comboBox23.getGridItems().forEach((node) -> {
            GridPane.setHgrow(node, Priority.ALWAYS);
        });
        
        hBox2.getChildren().addAll(comboBox21, comboBox22, comboBox23);

        vBox.getChildren().addAll(hBox1, hBox2);
        
        pane.getChildren().add(vBox);
        
        primaryStage.setScene(new Scene(pane, 800, 800));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
