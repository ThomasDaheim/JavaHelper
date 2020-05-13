/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx.GridComboBox;


import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyCode;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.scene.control.SkinBase;
import javafx.scene.control.skin.ComboBoxListViewSkin;

/**
 *
 * @author t.feuster
 */
public class CheckComboBoxSkin<T> extends SkinBase<CheckComboBox<T>> {
    
    /**************************************************************************
     * 
     * Static fields
     * 
     **************************************************************************/

    
    
    /**************************************************************************
     * 
     * fields
     * 
     **************************************************************************/
    
    // visuals
    private final ComboBox<T> comboBox;
    private final ListCell<T> buttonCell;
    
    // data
    private final CheckComboBox<T> control;
    private final ObservableList<T> items;
    private final ReadOnlyUnbackedObservableList<Integer> selectedIndices;
    private final ReadOnlyUnbackedObservableList<T> selectedItems;
    
    
    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/

    @SuppressWarnings("unchecked")
    public CheckComboBoxSkin(final CheckComboBox<T> control) {
        super(control);
        
        this.control = control;
        this.items = control.getItems();
        
        selectedIndices = (ReadOnlyUnbackedObservableList<Integer>) control.getCheckModel().getCheckedIndices();
        selectedItems = (ReadOnlyUnbackedObservableList<T>) control.getCheckModel().getCheckedItems();
        
        comboBox = new ComboBox<T>(items) {
            @Override
            protected javafx.scene.control.Skin<?> createDefaultSkin() {
                return createComboBoxListViewSkin(this);
            }
        };
        comboBox.setFocusTraversable(false);
        comboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        Bindings.bindContent(control.getStyleClass(), comboBox.getStyleClass());

        // installs a custom CheckBoxListCell cell factory
        comboBox.setCellFactory(listView -> {
            CheckBoxListCell<T> result = new CheckBoxListCell<>(control::getItemBooleanProperty);
            result.focusedProperty().addListener((o, ov, nv) -> {
                if (nv) {
                    result.getParent().requestFocus();
                }
            });
            //clicking on the label checks/unchecks the item
            result.setOnMouseClicked(e -> {
                T item = result.getItem();
                if (control.getCheckModel().isChecked(item)) {                        
                    control.getCheckModel().clearCheck(item);
                } else {
                    control.getCheckModel().check(item);                        
                }
            });
            result.converterProperty().bind(control.converterProperty());
            return result;
        });
        
        // we render the selection into a custom button cell, so that it can 
        // be pretty printed (e.g. 'Item 1, Item 2, Item 10').
        buttonCell = new ListCell<T>() {
            @Override protected void updateItem(T item, boolean empty) {
                // we ignore whatever item is selected, instead choosing
                // to display the selected item text using commas to separate
                // each item
                setText(getTextString());
            }
        };
        comboBox.setButtonCell(buttonCell);
        comboBox.setValue((T)getTextString());
        
        // The zero is a dummy value - it just has to be legally within the bounds of the
        // item count for the CheckComboBox items list.
        selectedIndices.addListener((ListChangeListener<Integer>) c -> buttonCell.updateIndex(0));
        
        getChildren().add(comboBox);
    }
    
    
    /**************************************************************************
     * 
     * Overriding public API
     * 
     **************************************************************************/
    
    @Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.minWidth(height);
    }

    @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.minHeight(width);
    }
    
    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.prefWidth(height);
    }

    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.prefHeight(width);
    }
    
    @Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * Shows the internal ComboBox
     */
    public void show() {
        comboBox.show();
    }

    /**
     * Hides the internal ComboBox
     */
    public void hide() {
        comboBox.hide();
    }
    
    /**************************************************************************
     * 
     * Implementation
     * 
     **************************************************************************/
    
    protected String getTextString() {
        
        if (control.getTitle() != null) {
            //if a title has been set, we use it...
            String vResult = control.getTitle();
            if (control.isShowCheckedCount()) {
                //...adding also the count of how many are selected, if so configured
                vResult = String.format("%s (%d/%d)", vResult, selectedItems.size(), items.size());
            }             
            return vResult;
        } else {            
            //...otherwise we generate a string concatenating the items
            return buildString();
        }
        
    }
    
    private String buildString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, max = selectedItems.size(); i < max; i++) {
            T item = selectedItems.get(i);
            if (control.getConverter() == null) {
                sb.append(item);
            } else {
                sb.append(control.getConverter().toString(item));
            }
            if (i < max - 1) {
                sb.append(", "); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }

    private Skin<?> createComboBoxListViewSkin(ComboBox<T> comboBox) {
        final ComboBoxListViewSkin<T> comboBoxListViewSkin = new ComboBoxListViewSkin<T>(comboBox);
        comboBoxListViewSkin.setHideOnClick(false);
        // Override to prevent the default behaviour of ListView when SPACE and ENTER is pressed
        final ListView<T> listView = (ListView<T>) comboBoxListViewSkin.getPopupContent();
        listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                T item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    final IndexedCheckModel<T> checkModel = control.getCheckModel();
                    if (checkModel != null) {
                        checkModel.toggleCheckState(item);
                    }
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                hide();
            } else if (e.getCode() == KeyCode.TAB ||
                    new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_ANY).match(e)) {
                e.consume();
                hide();
                control.fireEvent(e);
            }  else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                e.consume();
            }
        });
        return comboBoxListViewSkin;
    }
}
