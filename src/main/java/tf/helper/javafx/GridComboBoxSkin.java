/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx;


import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 *
 * @author t.feuster
 * @param <T>
 */
public class GridComboBoxSkin<T extends Node> extends ComboBoxListViewSkin<String> {
    
    // visuals
    private final GridComboBox<T> myComboBox;
    private final ScrollPane myScrollPane;
    private final GridPane myGridPane;
    
    // data
    private final ObservableList<T> gridItems;
    // combobox items - not as observablelist in order to have old list for change listener
    private List<String> stringItems = new ArrayList<>();
    
    private boolean internalUpdate = false;
    
    
    /**************************************************************************
     * 
     * Constructors
     * 
     * @param control
     **************************************************************************/

    @SuppressWarnings("unchecked")
    public GridComboBoxSkin(final GridComboBox<T> control) {
        super(control);
        
        myComboBox = control;
        myComboBox.setFocusTraversable(false);
        myComboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        myGridPane = createGridPane();
        myScrollPane = createScrollPane();
        getChildren().add(myScrollPane);
        
        myGridPane.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node node : c.getAddedSubList()) {
                        // any required inits for new nodes go here
                    }
                }
                if (c.wasRemoved()) {
                    for (Node node : c.getRemoved()) {
                        // any required exits for removed nodes go here
                    }
                }
            }
        });

        gridItems = control.getGridItems();
        gridItems.addListener((ListChangeListener.Change<? extends T> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node node : c.getAddedSubList()) {
                        // any required inits for new nodes go here
                    }
                }
                if (c.wasRemoved()) {
                    for (Node node : c.getRemoved()) {
                        // any required exits for removed nodes go here
                    }
                }
            }
        });
        
        initStringItems();
        
        // initialize PopupControl for this combobox - otherwise first paint for multiple boxes fails
        myComboBox.show();
        myComboBox.hide();
    }
    
    private void setStringItems() {
        stringItems = new ArrayList<>(myComboBox.getItems());
    }
    
    private void initStringItems() {
        setStringItems();
        
        myComboBox.getItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            // someone has changed the internal combobox... stupid idea but needs to be handled
            while (c.next()) {
                if (c.wasAdded()) {
                    for (String string : c.getAddedSubList()) {
                        // insert a new label at same position
                        final int index = myComboBox.getItems().indexOf(string);
                        myGridPane.getChildren().add(index, new Label(string));
                    }
                }
                if (c.wasRemoved()) {
                    for (String string : c.getRemoved()) {
                        // remove from correct position
                        final int index = stringItems.indexOf(string);
                        myGridPane.getChildren().remove(index);
                    }
                }
            }
            
            // store list for next change
            setStringItems();
        });
    }
    
    private GridPane createGridPane() {
        final GridPane _gridPane = new GridPane();

        _gridPane.setId("grid-pane");
        _gridPane.getStyleClass().add("grid-pane");
        _gridPane.setFocusTraversable(false);
        _gridPane.setSnapToPixel(true);

        // TODO: match gridpane onclick to myComboBox.getSelectionModel().select(index);
//        _gridPane.getSelectionModel().selectedIndexProperty().addListener(o -> {
//            if (listSelectionLock) return;
//            int index = listView.getSelectionModel().getSelectedIndex();
//            myComboBox.getSelectionModel().select(index);
//            updateDisplayNode();
//            myComboBox.notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
//        });

//        myComboBox.getSelectionModel().selectedItemProperty().addListener(o -> {
//            listViewSelectionDirty = true;
//        });

        _gridPane.setOnKeyPressed(t -> {
            // TODO move to behavior, when (or if) this class becomes a SkinBase
            if (t.getCode() == KeyCode.ENTER ||
                    t.getCode() == KeyCode.SPACE ||
                    t.getCode() == KeyCode.ESCAPE) {
                myComboBox.hide();
            }
        });

        return _gridPane;
    }
    
    private ScrollPane createScrollPane() {
        final ScrollPane _scrollPane = new ScrollPane() {
            @Override protected double computeMinWidth(double width) {
                // popup needs to have same width as combobox
                return myComboBox.getWidth();
            }

            @Override protected double computePrefWidth(double height) {
                // popup needs to have same width as combobox
                return myComboBox.getWidth();
            }

            @Override protected double computeMaxWidth(double height) {
                // popup needs to have same width as combobox
                return myComboBox.getWidth();
            }

            @Override protected double computeMinHeight(double width) {
                // not sure why this is needed? minimum height when popup not visible?
                return myComboBox.getEditor().getMinHeight();
            }

            @Override protected double computePrefHeight(double width) {
                return Math.min(myGridPane.getRowCount() * 30, 100);
            }

            @Override protected double computeMaxHeight(double width) {
                return Math.min(myGridPane.getRowCount() * 30, 100);
            }
        };
        
        _scrollPane.setFitToWidth(true);
        _scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        _scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        _scrollPane.setId("scroll-pane");
        _scrollPane.setFocusTraversable(false);
        _scrollPane.setSnapToPixel(true);
        // fix blurry scrollpane
        // https://stackoverflow.com/a/44498902
        _scrollPane.setStyle("-fx-background-insets: 0, 1;");

        _scrollPane.setContent(myGridPane);

        _scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, t -> {
            // RT-18672: Without checking if the user is clicking in the
            // scrollbar area of the ListView, the myComboBox will hide. Therefore,
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
                myComboBox.hide();
            }
        });

        return _scrollPane;
    }

    @Override public Node getPopupContent() {
        return myScrollPane;
    }
    
    /**************************************************************************
     * 
     * GridPane methods - only forward to internal GridPane
     * 
     **************************************************************************/
    
    public final DoubleProperty hgapProperty() {
        return myGridPane.hgapProperty();
    }

    public final void setHgap(double d) {
        myGridPane.setHgap(d);
    }

    public final double getHgap() {
        return myGridPane.getHgap();
    }

    public final DoubleProperty vgapProperty() {
        return myGridPane.vgapProperty();
    }

    public final void setVgap(double d) {
        myGridPane.setVgap(d);
    }

    public final double getVgap() {
        return myGridPane.getVgap();
    }

    public final ObjectProperty<Pos> alignmentProperty() {
        return myGridPane.alignmentProperty();
    }

    public final void setAlignment(Pos pos) {
        myGridPane.setAlignment(pos);
    }

    public final Pos getAlignment() {
        return myGridPane.getAlignment();
    }

    public final BooleanProperty gridLinesVisibleProperty() {
        return myGridPane.gridLinesVisibleProperty();
    }

    public final void setGridLinesVisible(boolean bln) {
        myGridPane.setGridLinesVisible(bln);
    }

    public final boolean isGridLinesVisible() {
        return myGridPane.isGridLinesVisible();
    }

    public final ObservableList<RowConstraints> getRowConstraints() {
        return myGridPane.getRowConstraints();
    }

    public final ObservableList<ColumnConstraints> getColumnConstraints() {
        return myGridPane.getColumnConstraints();
    }

    public void add(T node, int i, int i1) {
        myGridPane.add(node, i, i1);
        internalUpdate = true;
        gridItems.add(node);
        internalUpdate = false;
    }

    public void add(T node, int i, int i1, int i2, int i3) {
        myGridPane.add(node, i, i1, i2, i3);
        internalUpdate = true;
        gridItems.add(node);
        internalUpdate = false;
    }

    public void addRow(int i, T[] nodes) {
        myGridPane.addRow(i, nodes);
        internalUpdate = true;
//        gridItems.addAll(nodes);
        internalUpdate = false;
    }

    public void addColumn(int i, T[] nodes) {
        myGridPane.addColumn(i, nodes);
        internalUpdate = true;
//        gridItems.addAll(nodes);
        internalUpdate = false;
    }

    public Orientation getContentBias() {
        return myGridPane.getContentBias();
    }

    public String toString() {
        return "";
    }

    public final int getRowCount() {
        return myGridPane.getRowCount();
    }

    public final int getColumnCount() {
        return myGridPane.getColumnCount();
    }

    public final Bounds getCellBounds(int i, int i1) {
        return myGridPane.getCellBounds(i, i1);
    }
}
