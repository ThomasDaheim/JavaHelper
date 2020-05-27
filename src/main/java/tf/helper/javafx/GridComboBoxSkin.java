/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.util.StringConverter;

/**
 *
 * @author t.feuster
 * @param <T>
 */
public class GridComboBoxSkin<T extends Region> extends ComboBoxListViewSkin<String> {
    // These three pseudo class states are duplicated from Cell
    private static final PseudoClass PSEUDO_CLASS_SELECTED =
            PseudoClass.getPseudoClass("selected");
    private static final PseudoClass PSEUDO_CLASS_EMPTY =
            PseudoClass.getPseudoClass("empty");
    private static final PseudoClass PSEUDO_CLASS_FILLED =
            PseudoClass.getPseudoClass("filled");
    
    // visuals
    private final GridComboBox<T> myComboBox;
    private final ScrollPane myScrollPane;
    private final GridComboBoxPane<T> myGridPane;
    // GridPane.getGrid() isn't publicly available
    // https://stackoverflow.com/a/52648828 proposes to listen to width / height changes of nodes to track row/column sizes
    private final ListView<String> myListView;
    
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
        myComboBox.setFocusTraversable(true);
        myComboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        myListView = (ListView<String>) super.getPopupContent();
        
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
            // changes done here to gridItems are propagated to myGridPane in the next listener
            while (c.next()) {
                if (c.wasAdded()) {
                    for (String string : c.getAddedSubList()) {
                        Logger.getLogger(GridComboBox.class.getName()).log(Level.WARNING, "Item \"{0}\" directly added to GridComboBox!", string);
                        // insert a new label at same position
                        final int index = comboItems.indexOf(string) - getGridPaneOffset();
                        final T newNode = getNodeFromString(string);
                        initNode(newNode);
                        gridItems.add(index, newNode);
                    }
                }
                if (c.wasRemoved()) {
                    for (String string : c.getRemoved()) {
                        Logger.getLogger(GridComboBox.class.getName()).log(Level.WARNING, "Item \"{0}\" directly deleted from GridComboBox!", string);
                        // remove from correct position
                        final int index = stringItems.indexOf(string) - getGridPaneOffset();
                        gridItems.remove(index);
                    }
                }
            }
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
                        // need to insert new row to stay in sync with listview "theme" for new items
                        int row;
                        if (index > 0) {
                            // insert after column of previous item
                            row = GridPane.getRowIndex(gridItems.get(index-1)) + 1;
                        } else {
                            // insert before any other items
                            row = 0;
                        }
                        myGridPane.addRow(row, new Node[] {node});
                    }
                }
                if (c.wasRemoved()) {
                    for (T node : c.getRemoved()) {
                        myGridPane.getChildren().remove(node);
                    }
                }
            }
        });
        
        myGridPane.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            if (internalUpdate) {
                return;
            }
            
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
            
            // store list for next change
            setStringItems();
        });

        // selection listener to set pseudostyle of node
        myListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            if (internalUpdate) {
                return;
            }
            
            // There seems to be no "unselect" in case of changing the text in an editable combobox...
            // So we don't follow the wasAdded() / wasRemoved() trail
//            while (c.next()) {
//                if (c.wasAdded()) {
//                    for (String string : c.getAddedSubList()) {
//                        // set pseudostyleclass selected
//                        final T node = gridItems.get(stringItems.indexOf(string));
//                        node.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
//                        
//                    }
//                }
//                if (c.wasRemoved()) {
//                    for (String string : c.getRemoved()) {
//                        // set pseudostyleclass unselected
//                        final T node = gridItems.get(stringItems.indexOf(string));
//                        node.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
//                    }
//                }
//            }
            // instead we simply allways iterate over the whole selection and set/unset selected pseudoclass
            final List<String> selectedItems = myListView.getSelectionModel().getSelectedItems();
            
            for (String itemName : stringItems) {
                final T node = gridItems.get(stringItems.indexOf(itemName));
                node.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, selectedItems.indexOf(itemName) > -1);
            }
        });
    }

    private GridComboBoxPane<T> createGridPane() {
        final GridComboBoxPane<T> _gridPane = new GridComboBoxPane<>(this);

        _gridPane.setId("grid-pane");
        _gridPane.getStyleClass().add("grid-pane");
        _gridPane.setFocusTraversable(false);
        _gridPane.setSnapToPixel(true);
        _gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _gridPane.setPadding(Insets.EMPTY);
        
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
                // use row height & visibleRowCount to calculate pref height
                final double[] rowSizes = myGridPane.getGrid()[1];
                int prefHeight = 0;
                final int maxRows = Math.min(myGridPane.getRowCount(), myComboBox.getVisibleRowCount());
                for (int i = 0; i < maxRows; i++) {
                    prefHeight += rowSizes[i];
                }
                // add now add the vgaps to the height - if any
                prefHeight += myGridPane.getVgap() * Math.min(maxRows-1, 0);
                
                return Math.min(prefHeight, 600);
                
//                return Math.min(myGridPane.getRowCount() * 30, 160);
            }

            @Override protected double computeMaxHeight(double width) {
                return computePrefHeight(width);
            }
        };
        
        _scrollPane.setFitToWidth(true);
        _scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        _scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        _scrollPane.setPadding(Insets.EMPTY);

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
    private void initAndAddNodes(final T[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return;
        }
        
        for (T node : nodes) {
            initAndAddNode(node);
        }
    }

    private void initAndAddNode(final T node) {
        initNode(node);
                
        internalUpdate = true;
        final int index = myGridPane.getChildren().indexOf(node) + getGridPaneOffset();
        gridItems.add(index, node);
        comboItems.add(index, getStringFromNode(node));
        internalUpdate = false;

        // store list for next change
        setStringItems();
    }

    private void initNode(final T node) {
        // add a click listener to the node that updates the combobox selection
        node.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                final int index = myGridPane.getChildren().indexOf(node) + getGridPaneOffset();
                if (index != -1) {
                    // let the listview do the work to update the combobox textfield on select

                    // unselect the old node - if any
                    final int oldIndex = myListView.getSelectionModel().getSelectedIndex();
                    if (oldIndex != -1) {
                        final Node oldNode = myGridPane.getChildren().get(oldIndex - getGridPaneOffset());
                        oldNode.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
                    }

                    // select the new node
                    internalUpdate = true;
                    myListView.getSelectionModel().select(index);
                    internalUpdate = false;
                    node.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
                    
                    if (isHideOnClick()) {
                        myComboBox.hide();
                    }
                }
            }            
        });
        
        // set pseudocall to filled for hover highlighting
        node.pseudoClassStateChanged(PSEUDO_CLASS_EMPTY,    false);
        node.pseudoClassStateChanged(PSEUDO_CLASS_FILLED,   true);
        GridPane.setHgrow(node, Priority.ALWAYS);
    }
    
    private String getStringFromNode(T node) {
        // run item through StringConverter if it isn't null
        final StringConverter<T> c = myComboBox.getGridConverter();
        final String promptText = myComboBox.getPromptText();
        String s = node == null && promptText != null ? promptText :
                   c == null ? (node == null ? null : node.toString()) : c.toString(node);
        
        return s;
    }
    
    private T getNodeFromString(String string) {
        // run string through StringConverter if it isn't null
        final StringConverter<T> c = myComboBox.getGridConverter();
        T s = c == null ? (string == null ? null : null) : c.fromString(string);
        
        return s;
    }

    @Override public Node getPopupContent() {
        return myScrollPane;
    }
    
    /**************************************************************************
     * 
     * Helper methods
     * 
     **************************************************************************/
    
    public void scrollTo(T node) {
        // TODO: calculate Vvaleu of the scroll pane for a given node
    }

    /**************************************************************************
     * 
     * GridPane methods - only forward to internal GridPane
     * 
     **************************************************************************/
    
    public final ObservableList<T> getGridItems() {
        return gridItems;
    }

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

    public void add(T node) {
        // add in a new row at the end
        int rows = myGridPane.getRowCount();
        add(node, 0, rows+1);
        initAndAddNode(node);
    }

    public void add(T node, int i, int i1) {
        myGridPane.add(node, i, i1);
        initAndAddNode(node);
    }

    public void add(T node, int i, int i1, int i2, int i3) {
        myGridPane.add(node, i, i1, i2, i3);
        initAndAddNode(node);
    }

    public void addRow(int i, T[] nodes) {
        myGridPane.addRow(i, nodes);
        initAndAddNodes(nodes);
    }

    public void addColumn(int i, T[] nodes) {
        myGridPane.addColumn(i, nodes);
        initAndAddNodes(nodes);
    }

    public Orientation getContentBias() {
        return myGridPane.getContentBias();
    }

    @Override
    public String toString() {
        return super.toString();
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

    /**************************************************************************
     * 
     * GridComboBoxPane methods - only forward to internal GridComboBoxPane
     * 
     **************************************************************************/
    
    public final BooleanProperty resizeContentRow() { return myGridPane.resizeContentRow(); }
    public final void setResizeContentRow(boolean bln) { myGridPane.setResizeContentRow(bln); }
    public final boolean getResizeContentRow() { return myGridPane.getResizeContentRow(); }

    public final BooleanProperty resizeContentRowSpan() { return myGridPane.resizeContentRowSpan(); }
    public final void setResizeContentRowSpan(boolean bln) { myGridPane.setResizeContentRowSpan(bln); }
    public final boolean getResizeContentRowSpan() { return myGridPane.getResizeContentRowSpan(); }

    public final BooleanProperty resizeContentColumn() { return myGridPane.resizeContentColumn(); }
    public final void setResizeContentColumn(boolean bln) { myGridPane.setResizeContentColumn(bln); }
    public final boolean getResizeContentColumn() { return myGridPane.getResizeContentColumn(); }

    public final BooleanProperty resizeContentColumnSpan() { return myGridPane.resizeContentColumnSpan(); }
    public final void setResizeContentColumnSpan(boolean bln) { myGridPane.setResizeContentColumnSpan(bln); }
    public final boolean getResizeContentColumnSpan() { return myGridPane.getResizeContentColumnSpan(); }
}
