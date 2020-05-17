/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.StringConverter;

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
    private final ObservableList<String> comboItems;
    // combobox items - not as observablelist in order to have old list for change listener
    private List<String> stringItems = new ArrayList<>();
    
    private boolean internalUpdate = false;
    
    
    /**************************************************************************
     * 
     * Constructors
     * 
     * @param control
     * @param ol
     **************************************************************************/

    @SuppressWarnings("unchecked")
    public GridComboBoxSkin(final GridComboBox<T> control, final ObservableList<T> ol) {
        super(control);
        
        myComboBox = control;
        myComboBox.setFocusTraversable(false);
        myComboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        myGridPane = createGridPane();
        myScrollPane = createScrollPane();
        getChildren().add(myScrollPane);

        if (ol == null) {
            gridItems = FXCollections.observableArrayList();
        } else {
            gridItems = FXCollections.observableArrayList(ol);
            myComboBox.setItems(ol.stream().map((t) -> {
                    return t.toString();
                }).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
        comboItems = myComboBox.getItems();

        initComboBoxListeners();
        
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

        // initialize PopupControl for this combobox - otherwise first paint for multiple boxes fails
        myComboBox.show();
        myComboBox.hide();
        
        myComboBox.showingProperty().addListener((obs, hidden, showing) -> {
            if (showing) {
                // somehow we don't get the focus on showing the popup
                if (myComboBox.isEditable()) {
                    myComboBox.getEditor().requestFocus();
                } else {
                    myComboBox.requestFocus();
                }
            }
        });
    }
    
    private void setStringItems() {
        stringItems = new ArrayList<>(myComboBox.getItems());
    }
    
    private void initComboBoxListeners() {
        setStringItems();
        
        comboItems.addListener((ListChangeListener.Change<? extends String> c) -> {
            if (internalUpdate) {
                return;
            }
            
            // someone has changed the internal combobox... stupid idea but needs to be handled
            while (c.next()) {
                if (c.wasAdded()) {
                    for (String string : c.getAddedSubList()) {
                        // insert a new label at same position
                        final int index = comboItems.indexOf(string);
                        // not sure what gridpane does with its layout???
                        myGridPane.getChildren().add(index - getGridPaneOffset(), new Label(string));
                    }
                }
                if (c.wasRemoved()) {
                    for (String string : c.getRemoved()) {
                        // remove from correct position
                        final int index = stringItems.indexOf(string);
                        myGridPane.getChildren().remove(index - getGridPaneOffset());
                    }
                }
            }
            
            // store list for next change
            setStringItems();
        });

        gridItems.addListener((ListChangeListener.Change<? extends T> c) -> {
            if (internalUpdate) {
                return;
            }
            
            // someone has changed the grid items - and it wasn't us...
            while (c.next()) {
                if (c.wasAdded()) {
                    for (T node : c.getAddedSubList()) {
                        final int index = gridItems.indexOf(node);
                        // not sure what gridpane does with its layout???
                        myGridPane.getChildren().add(index - getGridPaneOffset(), node);
                    }
                }
                if (c.wasRemoved()) {
                    for (T node : c.getRemoved()) {
                        myGridPane.getChildren().remove(node);
                    }
                }
            }
        });
        
        // TODO: add other relevant properties here as well, e.g. visibleRowCount and pass on to the gridpane
    }
    
    private GridPane createGridPane() {
        final GridPane _gridPane = new GridPane();

        _gridPane.setId("grid-pane");
        _gridPane.getStyleClass().add("grid-pane");
        _gridPane.setFocusTraversable(false);
        _gridPane.setSnapToPixel(true);

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

        return _scrollPane;
    }
    
    private int getGridPaneOffset() {
        // tricky, with gridlines enables we have one more children as nodes added...
        return myGridPane.isGridLinesVisible() ? -1 : 0;
    }
    
    // some initialisation is required for new nodes
    private void initNodes(final T[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return;
        }
        
        for (T node : nodes) {
            initNode(node);
        }
    }

    private void initNode(final T node) {
        // add a click listener to the node that updates the combobox selection
        node.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                final int index = myGridPane.getChildren().indexOf(node) + getGridPaneOffset();
                if (index != -1) {
                    final ListView<String> listView = (ListView<String>) super.getPopupContent();
                    // let the listview do the work to update the combobox textfield on select
                    listView.getSelectionModel().select(index);
                    
                    if (isHideOnClick()) {
                        myComboBox.hide();
                    }
                }
            }            
        });

        internalUpdate = true;
        gridItems.add(myGridPane.getChildren().indexOf(node) + getGridPaneOffset(), node);
        comboItems.add(myGridPane.getChildren().indexOf(node) + getGridPaneOffset(), getNodeString(node));
        internalUpdate = false;
    }
    
    private String getNodeString(T node) {
        // run item through StringConverter if it isn't null
        final StringConverter<T> c = myComboBox.getGridConverter();
        final String promptText = myComboBox.getPromptText();
        String s = node == null && promptText != null ? promptText :
                   c == null ? (node == null ? null : node.toString()) : c.toString(node);
        
        return s;
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
        initNode(node);
    }

    public void add(T node, int i, int i1, int i2, int i3) {
        myGridPane.add(node, i, i1, i2, i3);
        initNode(node);
    }

    public void addRow(int i, T[] nodes) {
        myGridPane.addRow(i, nodes);
        initNodes(nodes);
    }

    public void addColumn(int i, T[] nodes) {
        myGridPane.addColumn(i, nodes);
        initNodes(nodes);
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
