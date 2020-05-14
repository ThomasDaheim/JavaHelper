/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx.GridComboBox;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 *
 * @author t.feuster
 * @param <T>
 */
public class GridComboBox<T> extends Control {
    
    /**************************************************************************
     * 
     * Private fields
     * 
     **************************************************************************/
    
    private String stylesheet;
    /**
     * A helper method that ensures that the resource based lookup of the user
     * agent stylesheet only happens once. Caches the external form of the
     * resource.
     *
     * @param clazz
     *            the class used for the resource lookup
     * @param fileName
     *            the name of the user agent stylesheet
     * @return the external form of the user agent stylesheet (the path)
     */
    protected final String getUserAgentStylesheet(Class<?> clazz,
            String fileName) {

        /*
         * For more information please see RT-40658
         */
        if (stylesheet == null) {
            stylesheet = clazz.getResource(fileName).toExternalForm();
        }

        return stylesheet;
    }
    
    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final GridComboBoxSkin<T> gridComboBoxSkin = new GridComboBoxSkin<>(this);


    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/
    
    /**
     * Creates a new CheckComboBox instance with an empty list of choices.
     */
    public GridComboBox() {
        this(null);
    }
    
    /**
     * Creates a new CheckComboBox instance with the given items available as
     * choices.
     * 
     * @param items The items to display within the CheckComboBox.
     */
    public GridComboBox(final ObservableList<T> items) {
        if (items != null) {
            this.items.setAll(items);
        }
    }

    
    /**************************************************************************
     * 
     * Public API
     * 
     **************************************************************************/
    
    /**
     * @param items
     */
    public final void setItems(ObservableList<T> items) {
        if (items != null) {
            this.items.setAll(items);
        } else {
            this.items.clear();
        }
    }

    /**
     * @return 
     */
    public ObservableList<T> getItems() {
        return items;
    }
    
    
    /**************************************************************************
     * 
     * Properties
     * 
     **************************************************************************/

//    // --- converter
//    private ObjectProperty<StringConverter<T>> converter = 
//            new SimpleObjectProperty<>(this, "converter");
//    
//    /**
//     * A {@link StringConverter} that, given an object of type T, will 
//     * return a String that can be used to represent the object visually.
//     * @return 
//     */
//    public final ObjectProperty<StringConverter<T>> converterProperty() { 
//        return converter; 
//    }
//    
//    /** 
//     * Sets the {@link StringConverter} to be used in the control.
//     * @param value A {@link StringConverter} that, given an object of type T, will 
//     * return a String that can be used to represent the object visually.
//     */
//    public final void setConverter(StringConverter<T> value) { 
//        converterProperty().set(value); 
//    }
//    
//    /**
//     * A {@link StringConverter} that, given an object of type T, will 
//     * return a String that can be used to represent the object visually.
//     * @return 
//     */
//    public final StringConverter<T> getConverter() { 
//        return converterProperty().get(); 
//    }
    
    /***************************************************************************
     *                                                                         *
     * Methods - passthrough to skin                                           *
     *                                                                         *
     **************************************************************************/

    /**
     * Requests that the ComboBox display the popup aspect of the user interface.
     */
    public void show() {
        gridComboBoxSkin.show();
    }

    /**
     * Closes the popup / dialog that was shown when {@link #show()} was called.
     */
    public void hide() {
        gridComboBoxSkin.hide();
    }

    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return gridComboBoxSkin.itemsProperty();
    }

    public ObjectProperty<StringConverter<T>> converterProperty() {
        return gridComboBoxSkin.converterProperty();
    }

    public final void setConverter(StringConverter<T> sc) {
        gridComboBoxSkin.setConverter(sc);
    }

    public final StringConverter<T> getConverter() {
        return gridComboBoxSkin.getConverter();
    }

    public final void setSelectionModel(SingleSelectionModel<T> ssm) {
        gridComboBoxSkin.setSelectionModel(ssm);
    }

    public final SingleSelectionModel<T> getSelectionModel() {
        return gridComboBoxSkin.getSelectionModel();
    }

    public final ObjectProperty<SingleSelectionModel<T>> selectionModelProperty() {
        return gridComboBoxSkin.selectionModelProperty();
    }

    public final void setVisibleRowCount(int i) {
        gridComboBoxSkin.setVisibleRowCount(i);
    }

    public final int getVisibleRowCount() {
        return gridComboBoxSkin.getVisibleRowCount();
    }

    public final IntegerProperty visibleRowCountProperty() {
        return gridComboBoxSkin.visibleRowCountProperty();
    }

    public final TextField getEditor() {
        return gridComboBoxSkin.getEditor();
    }

    public final ReadOnlyObjectProperty<TextField> editorProperty() {
        return gridComboBoxSkin.editorProperty();
    }

    public final ObjectProperty<Node> placeholderProperty() {
        return gridComboBoxSkin.placeholderProperty();
    }

    public final void setPlaceholder(Node node) {
        gridComboBoxSkin.setPlaceholder(node);
    }

    public final Node getPlaceholder() {
        return gridComboBoxSkin.getPlaceholder();
    }

    public final void commitValue() {
        gridComboBoxSkin.commitValue();
    }

    public final void cancelEdit() {
        gridComboBoxSkin.cancelEdit();
    }

    public ObjectProperty<T> valueProperty() {
        return gridComboBoxSkin.valueProperty();
    }

    public final void setValue(T t) {
        gridComboBoxSkin.setValue(t);
    }

    public final T getValue() {
        return gridComboBoxSkin.getValue();
    }

    public BooleanProperty editableProperty() {
        return gridComboBoxSkin.editableProperty();
    }

    public final void setEditable(boolean bln) {
        gridComboBoxSkin.setEditable(bln);
    }

    public final boolean isEditable() {
        return gridComboBoxSkin.isEditable();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return gridComboBoxSkin.showingProperty();
    }

    public final boolean isShowing() {
        return gridComboBoxSkin.isShowing();
    }


    public final StringProperty promptTextProperty() {
        return gridComboBoxSkin.promptTextProperty();
    }

    public final String getPromptText() {
        return gridComboBoxSkin.getPromptText();
    }

    public final void setPromptText(String string) {
        gridComboBoxSkin.setPromptText(string);
    }

    public BooleanProperty armedProperty() {
        return gridComboBoxSkin.armedProperty();
    }

    public final boolean isArmed() {
        return gridComboBoxSkin.isArmed();
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return gridComboBoxSkin.onActionProperty();
    }

    public final void setOnAction(EventHandler<ActionEvent> eh) {
        gridComboBoxSkin.setOnAction(eh);
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return gridComboBoxSkin.getOnAction();
    }

    public final ObjectProperty<EventHandler<Event>> onShowingProperty() {
        return gridComboBoxSkin.onShowingProperty();
    }

    public final void setOnShowing(EventHandler<Event> eh) {
        gridComboBoxSkin.setOnShowing(eh);
    }

    public final EventHandler<Event> getOnShowing() {
        return gridComboBoxSkin.getOnShowing();
    }

    public final ObjectProperty<EventHandler<Event>> onShownProperty() {
        return gridComboBoxSkin.onShownProperty();
    }

    public final void setOnShown(EventHandler<Event> eh) {
        gridComboBoxSkin.setOnShown(eh);
    }

    public final EventHandler<Event> getOnShown() {
        return gridComboBoxSkin.getOnShown();
    }

    public final ObjectProperty<EventHandler<Event>> onHidingProperty() {
        return gridComboBoxSkin.onHidingProperty();
    }

    public final void setOnHiding(EventHandler<Event> eh) {
        gridComboBoxSkin.setOnHiding(eh);
    }

    public final EventHandler<Event> getOnHiding() {
        return gridComboBoxSkin.getOnHiding();
    }

    public final ObjectProperty<EventHandler<Event>> onHiddenProperty() {
        return gridComboBoxSkin.onHiddenProperty();
    }

    public final void setOnHidden(EventHandler<Event> eh) {
        gridComboBoxSkin.setOnHidden(eh);
    }

    public final EventHandler<Event> getOnHidden() {
        return gridComboBoxSkin.getOnHidden();
    }

    public void arm() {
        gridComboBoxSkin.arm();
    }

    public void disarm() {
        gridComboBoxSkin.disarm();
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return gridComboBoxSkin;
    }
}
