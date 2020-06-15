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
package tf.helper.javafx;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.util.StringConverter;

/**
 * ComboBox that show a GridPane as popup. 
 * To this end the skin GridComboBoxSkin implements a getPopUp() the returns a GridPane which
 * contains the items. Minimum requirement for an item is to be a Region - this is needed for 
 * automatic scaling of width & height to have same behaviour as a normal ComboBox: the full row & col is highlighted
 * in case of mouse-over or selection and not only the size of the region contained in the GridPane "cell".
 * This resizing needs to be done manually since the GridPane doesn't consist of individual cells that can be styled put places
 * its items directly on the canvas.
 * 
 * Design decissions: A ComboBox offers get/setItem() methods to add content to the PopUp. But using those methods 
 * the full functionality of the underlying GridPane can't be used easily. The caller would need to call various static 
 * GridsPane.setXXX methods on the node that should be added to the GridComboBox instead of using the various available 
 * add() methods of the GridPan.
 * 
 * Options: 
 * 
 * 1) Offer all static GridPane methods here as well and simply forward to the internal GridPane instance
 * 2) Expose the internal GridPane via getGridPane() to the use
 * 
 * 2) leads to a much smaller interface but exposes the internals to the user. So option 1) is choosen in this implementation. 
 * It allows makes sure that there are no 2 independent ways to add content to the GridComboBox. That would require a mechanism 
 * to keep the lists from getItems() and getChildren() in sync.
 * 
 * Note: all updates to items<T> of the grid and to items of the underlying ComboBox<String> are done in GridComboBoxSkin.
 * 
 * @author thomas
 * @param <T>
 */
public class GridComboBox<T extends Region> extends ComboBox<String> {

    private final boolean styleSheetAdded = getStylesheets().add(GridComboBox.class .getResource("/GridComboBox.css").toExternalForm());
    
    private static <T> StringConverter<T> defaultStringConverter() {
        return new StringConverter<T>() {
            @Override public String toString(T t) {
                return t == null ? null : t.toString();
            }

            @Override public T fromString(String string) {
                // TODO: what could be a good default here?
                Logger.getLogger(GridComboBox.class.getName()).log(Level.WARNING, "GridComboBox: defaultStringConverter() used, returning null node");
                return null;
            }
        };
    }

    // convinience implementation for Label
    public static StringConverter<Label> labelStringConverter() {
        return new StringConverter<Label>() {
            @Override public String toString(Label s) {
                return s == null ? null : s.getText();
            }

            @Override public Label fromString(String string) {
                return new Label(string);
            }
        };
    }

    private final GridComboBoxSkin<T> gridComboBoxSkin;
    
    public GridComboBox() {
        super();
        
        gridComboBoxSkin = new GridComboBoxSkin<>(this, null);
    }

    public GridComboBox(ObservableList<T> ol) {
        super();
        
        gridComboBoxSkin = new GridComboBoxSkin<>(this, ol);
    }
    
    // --- string converter
    /**
     * Converts the user-typed input (when the ComboBox is
     * {@link #editableProperty() editable}) to an object of type T, such that
     * the input may be retrieved via the  {@link #valueProperty() value} property.
     * @return the converter property
     */
    public ObjectProperty<StringConverter<T>> gridConverterProperty() { return gridConverter; }
    private ObjectProperty<StringConverter<T>> gridConverter =
            new SimpleObjectProperty<>(this, "gridConverter", defaultStringConverter());
    public final void setGridConverter(StringConverter<T> value) { gridConverterProperty().set(value); }
    public final StringConverter<T> getGridConverter() {return gridConverterProperty().get(); }
    
    @Override
    protected javafx.scene.control.Skin<?> createDefaultSkin() {
        return gridComboBoxSkin;
    }
    
    /**************************************************************************
     * 
     * Helper methods
     * 
     **************************************************************************/
    
    public void scrollTo(T node) {
        gridComboBoxSkin.scrollTo(node);
    }

    /**************************************************************************
     * 
     * GridPane methods - only forward to GridComboBoxSkin
     * 
     **************************************************************************/
    
    public final ObservableList<T> getGridItems() {
        return gridComboBoxSkin.getGridItems();
    }

    public final DoubleProperty hgapProperty() {
        return gridComboBoxSkin.hgapProperty();
    }

    public final void setHgap(double d) {
        gridComboBoxSkin.setHgap(d);
    }

    public final double getHgap() {
        return gridComboBoxSkin.getHgap();
    }

    public final DoubleProperty vgapProperty() {
        return gridComboBoxSkin.vgapProperty();
    }

    public final void setVgap(double d) {
        gridComboBoxSkin.setVgap(d);
    }

    public final double getVgap() {
        return gridComboBoxSkin.getVgap();
    }

    public final ObjectProperty<Pos> alignmentProperty() {
        return gridComboBoxSkin.alignmentProperty();
    }

    public final void setAlignment(Pos pos) {
        gridComboBoxSkin.setAlignment(pos);
    }

    public final Pos getAlignment() {
        return gridComboBoxSkin.getAlignment();
    }

    public final BooleanProperty gridLinesVisibleProperty() {
        return gridComboBoxSkin.gridLinesVisibleProperty();
    }

    public final void setGridLinesVisible(boolean bln) {
        gridComboBoxSkin.setGridLinesVisible(bln);
    }

    public final boolean isGridLinesVisible() {
        return gridComboBoxSkin.isGridLinesVisible();
    }

    public final ObservableList<RowConstraints> getRowConstraints() {
        return gridComboBoxSkin.getRowConstraints();
    }

    public final ObservableList<ColumnConstraints> getColumnConstraints() {
        return gridComboBoxSkin.getColumnConstraints();
    }

    public void add(T node) {
        gridComboBoxSkin.add(node);
    }

    public void add(T node, int i, int i1) {
        gridComboBoxSkin.add(node, i, i1);
    }

    public void add(T node, int i, int i1, int i2, int i3) {
        gridComboBoxSkin.add(node, i, i1, i2, i3);
    }

    public void addRow(int i, T[] nodes) {
        gridComboBoxSkin.addRow(i, nodes);
    }

    public void addColumn(int i, T[] nodes) {
        gridComboBoxSkin.addColumn(i, nodes);
    }

    @Override
    public Orientation getContentBias() {
        return gridComboBoxSkin.getContentBias();
    }

    public final int getRowCount() {
        return gridComboBoxSkin.getRowCount();
    }

    public final int getColumnCount() {
        return gridComboBoxSkin.getColumnCount();
    }

    public final Bounds getCellBounds(int i, int i1) {
        return gridComboBoxSkin.getCellBounds(i, i1);
    }

    /**************************************************************************
     * 
     * GridComboBoxPane methods - only forward to GridComboBoxSkin
     * 
     **************************************************************************/
    
    public final BooleanProperty resizeContentRow() { return gridComboBoxSkin.resizeContentRow(); }
    public final void setResizeContentRow(boolean bln) { gridComboBoxSkin.setResizeContentRow(bln); }
    public final boolean getResizeContentRow() { return gridComboBoxSkin.getResizeContentRow(); }

    public final BooleanProperty resizeContentRowSpan() { return gridComboBoxSkin.resizeContentRowSpan(); }
    public final void setResizeContentRowSpan(boolean bln) { gridComboBoxSkin.setResizeContentRowSpan(bln); }
    public final boolean getResizeContentRowSpan() { return gridComboBoxSkin.getResizeContentRowSpan(); }

    public final BooleanProperty resizeContentColumn() { return gridComboBoxSkin.resizeContentColumn(); }
    public final void setResizeContentColumn(boolean bln) { gridComboBoxSkin.setResizeContentColumn(bln); }
    public final boolean getResizeContentColumn() { return gridComboBoxSkin.getResizeContentColumn(); }

    public final BooleanProperty resizeContentColumnSpan() { return gridComboBoxSkin.resizeContentColumnSpan(); }
    public final void setResizeContentColumnSpan(boolean bln) { gridComboBoxSkin.setResizeContentColumnSpan(bln); }
    public final boolean getResizeContentColumnSpan() { return gridComboBoxSkin.getResizeContentColumnSpan(); }
}
