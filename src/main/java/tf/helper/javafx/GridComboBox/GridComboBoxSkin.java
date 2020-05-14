/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx.GridComboBox;


import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 *
 * @author t.feuster
 * @param <T>
 */
public class GridComboBoxSkin<T extends Node> extends ComboBoxListViewSkin<T> {
    
    // visuals
    private final GridComboBox<T> comboBox;
    private final GridPane myGridPane;
    
    // data
    private final ObservableList<T> items;
    
    
    /**************************************************************************
     * 
     * Constructors
     * 
     * @param control
     **************************************************************************/

    @SuppressWarnings("unchecked")
    public GridComboBoxSkin(final GridComboBox<T> control) {
        super(control);
        
        myGridPane = createGridPane();
        myGridPane.setManaged(false);
        getChildren().add(myGridPane);

        items = control.getItems();
        items.addListener((ListChangeListener.Change<? extends T> c) -> {
            updateGridPane(c);
        });
        updateGridPane(null);
        
        comboBox = control;
        comboBox.setFocusTraversable(false);
        comboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // installs a custom CheckBoxListCell cell factory
//        comboBox.setCellFactory(listView -> {
//            CheckBoxListCell<T> result = new CheckBoxListCell<>(control::getItemBooleanProperty);
//            result.focusedProperty().addListener((o, ov, nv) -> {
//                if (nv) {
//                    result.getParent().requestFocus();
//                }
//            });
//            //clicking on the label checks/unchecks the item
//            result.setOnMouseClicked(e -> {
//                T item = result.getItem();
//                if (control.getCheckModel().isChecked(item)) {                        
//                    control.getCheckModel().clearCheck(item);
//                } else {
//                    control.getCheckModel().check(item);                        
//                }
//            });
//            result.converterProperty().bind(control.converterProperty());
//            return result;
//        });
    }
    
    private GridPane createGridPane() {
        final GridPane _gridPane = new GridPane() {
            {
                getProperties().put("selectFirstRowByDefault", false);
            }

            @Override protected double computeMinHeight(double width) {
                return 30;
            }

            @Override protected double computePrefWidth(double height) {
                double pw = Math.max(100, comboBox.getWidth());
                return Math.max(50, pw);
            }

            @Override protected double computePrefHeight(double width) {
                return getListViewPrefHeight();
            }
        };

        _gridPane.setId("grid-pane");
        _gridPane.setFocusTraversable(false);

        // TODO: match gridpane onclick to comboBox.getSelectionModel().select(index);
//        _gridPane.getSelectionModel().selectedIndexProperty().addListener(o -> {
//            if (listSelectionLock) return;
//            int index = listView.getSelectionModel().getSelectedIndex();
//            comboBox.getSelectionModel().select(index);
//            updateDisplayNode();
//            comboBox.notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
//        });

//        comboBox.getSelectionModel().selectedItemProperty().addListener(o -> {
//            listViewSelectionDirty = true;
//        });

        _gridPane.addEventFilter(MouseEvent.MOUSE_RELEASED, t -> {
            // RT-18672: Without checking if the user is clicking in the
            // scrollbar area of the ListView, the comboBox will hide. Therefore,
            // we add the check below to prevent this from happening.
            EventTarget target = t.getTarget();
            if (target instanceof Parent) {
                List<String> s = ((Parent) target).getStyleClass();
                if (s.contains("thumb")
                        || s.contains("track")
                        || s.contains("decrement-arrow")
                        || s.contains("increment-arrow")) {
                    return;
                }
            }

            if (isHideOnClick()) {
                comboBox.hide();
            }
        });

        _gridPane.setOnKeyPressed(t -> {
            // TODO move to behavior, when (or if) this class becomes a SkinBase
            if (t.getCode() == KeyCode.ENTER ||
                    t.getCode() == KeyCode.SPACE ||
                    t.getCode() == KeyCode.ESCAPE) {
                comboBox.hide();
            }
        });

        return _gridPane;
    }

    private double getListViewPrefHeight() {
        double ch = items.size() * 25;
        double ph = Math.min(ch, 200);

        return ph;
    }
    
    private void updateGridPane(ListChangeListener.Change<? extends T> c) {
        if (items == null || items.isEmpty()) {
            myGridPane.getChildren().clear();
            return;
        }
        
//        if (c != null) {
//            // go through changes individually and update grid
//        } else {
//            // repaint whole grid
        myGridPane.getChildren().setAll(items);
        
//        }
        
    }

    @Override public Node getPopupContent() {
        return myGridPane;
    }
}
