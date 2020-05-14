/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx.GridComboBox;


import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Skin;

import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 *
 * @author t.feuster
 * @param <T>
 */
public class GridComboBoxSkin<T> extends SkinBase<GridComboBox<T>> {
    
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
    private final GridPane myGridPane = new GridPane();
    
    // data
    private final GridComboBox<T> control;
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
        
        this.control = control;
        this.items = control.getItems();
        
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
     * Methods - passthrough to internal combo bo                              *
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

    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return comboBox.itemsProperty();
    }

    public ObjectProperty<StringConverter<T>> converterProperty() {
        return comboBox.converterProperty();
    }

    public final void setConverter(StringConverter<T> sc) {
        comboBox.setConverter(sc);
    }

    public final StringConverter<T> getConverter() {
        return comboBox.getConverter();
    }

    public final void setSelectionModel(SingleSelectionModel<T> ssm) {
        comboBox.setSelectionModel(ssm);
    }

    public final SingleSelectionModel<T> getSelectionModel() {
        return comboBox.getSelectionModel();
    }

    public final ObjectProperty<SingleSelectionModel<T>> selectionModelProperty() {
        return comboBox.selectionModelProperty();
    }

    public final void setVisibleRowCount(int i) {
        comboBox.setVisibleRowCount(i);
    }

    public final int getVisibleRowCount() {
        return comboBox.getVisibleRowCount();
    }

    public final IntegerProperty visibleRowCountProperty() {
        return comboBox.visibleRowCountProperty();
    }

    public final TextField getEditor() {
        return comboBox.getEditor();
    }

    public final ReadOnlyObjectProperty<TextField> editorProperty() {
        return comboBox.editorProperty();
    }

    public final ObjectProperty<Node> placeholderProperty() {
        return comboBox.placeholderProperty();
    }

    public final void setPlaceholder(Node node) {
        comboBox.setPlaceholder(node);
    }

    public final Node getPlaceholder() {
        return comboBox.getPlaceholder();
    }

    public final void commitValue() {
        comboBox.commitValue();
    }

    public final void cancelEdit() {
        comboBox.cancelEdit();
    }
    
    public ObjectProperty<T> valueProperty() {
        return comboBox.valueProperty();
    }

    public final void setValue(T t) {
        comboBox.setValue(t);
    }

    public final T getValue() {
        return comboBox.getValue();
    }

    public BooleanProperty editableProperty() {
        return comboBox.editableProperty();
    }

    public final void setEditable(boolean bln) {
        comboBox.setEditable(bln);
    }

    public final boolean isEditable() {
        return comboBox.isEditable();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return comboBox.showingProperty();
    }

    public final boolean isShowing() {
        return comboBox.isShowing();
    }


    public final StringProperty promptTextProperty() {
        return comboBox.promptTextProperty();
    }

    public final String getPromptText() {
        return comboBox.getPromptText();
    }

    public final void setPromptText(String string) {
        comboBox.setPromptText(string);
    }

    public BooleanProperty armedProperty() {
        return comboBox.armedProperty();
    }

    public final boolean isArmed() {
        return comboBox.isArmed();
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return comboBox.onActionProperty();
    }

    public final void setOnAction(EventHandler<ActionEvent> eh) {
        comboBox.setOnAction(eh);
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return comboBox.getOnAction();
    }

    public final ObjectProperty<EventHandler<Event>> onShowingProperty() {
        return comboBox.onShowingProperty();
    }

    public final void setOnShowing(EventHandler<Event> eh) {
        comboBox.setOnShowing(eh);
    }

    public final EventHandler<Event> getOnShowing() {
        return comboBox.getOnShowing();
    }

    public final ObjectProperty<EventHandler<Event>> onShownProperty() {
        return comboBox.onShownProperty();
    }

    public final void setOnShown(EventHandler<Event> eh) {
        comboBox.setOnShown(eh);
    }

    public final EventHandler<Event> getOnShown() {
        return comboBox.getOnShown();
    }

    public final ObjectProperty<EventHandler<Event>> onHidingProperty() {
        return comboBox.onHidingProperty();
    }

    public final void setOnHiding(EventHandler<Event> eh) {
        comboBox.setOnHiding(eh);
    }

    public final EventHandler<Event> getOnHiding() {
        return comboBox.getOnHiding();
    }

    public final ObjectProperty<EventHandler<Event>> onHiddenProperty() {
        return comboBox.onHiddenProperty();
    }

    public final void setOnHidden(EventHandler<Event> eh) {
        comboBox.setOnHidden(eh);
    }

    public final EventHandler<Event> getOnHidden() {
        return comboBox.getOnHidden();
    }

    public void arm() {
        comboBox.arm();
    }

    public void disarm() {
        comboBox.disarm();
    }

    /**************************************************************************
     * 
     * Implementation
     * 
     **************************************************************************/
    
    private Skin<?> createComboBoxListViewSkin(ComboBox<T> comboBox) {
        final ComboBoxListViewSkin<T> comboBoxListViewSkin = new ComboBoxListViewSkin<>(comboBox);
        comboBoxListViewSkin.setHideOnClick(false);
        return comboBoxListViewSkin;
    }
}
