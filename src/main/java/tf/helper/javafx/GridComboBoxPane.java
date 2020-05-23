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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.apache.commons.lang3.tuple.Pair;
import tf.helper.general.ObjectsHelper;

/**
 * Extension of GridPane to be used as popup in the GridComboBox.https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/javafx/scene/layout/GridPane.java
 
 Adds a few mthods to the underlying GridPane:
 
 getCurrentGrid(): access to the getGrid() of the GridPane via reflection - since not a public method
 
 Resize content to match its row/column size: Can be switched on for all grid content or only for such spanning multiple rows/cols
 * 
 * @author thomas
 * @param <T>
 */
public class GridComboBoxPane<T extends Region> extends GridPane {
    
    // boolean if grid content should be resized to match its row height
    private final BooleanProperty resizeContentRow = new SimpleBooleanProperty(false);
    // boolean if multi-row grid content should be resized to match its rows height
    private final BooleanProperty resizeContentRowSpan = new SimpleBooleanProperty(false);
    // boolean if grid content should be resized to match its column width
    private final BooleanProperty resizeContentColumn = new SimpleBooleanProperty(false);
    // boolean if multi-column grid content should be resized to match its columns width
    private final BooleanProperty resizeContentColumnSpan = new SimpleBooleanProperty(false);
    
    // map to hold orig height/width values of the items
    private Map<Node, Pair<Double, Double>> origSizes = new HashMap<>();
    
    // know your skin - you never know what it can be used for
    private final GridComboBoxSkin<T> mySkin;
    
    public GridComboBoxPane(final GridComboBoxSkin<T> skin) {
        super();
        
        mySkin = skin;
    }
    
    // getter/setter for properties
    public final BooleanProperty resizeContentRow() { return resizeContentRow; }
    public final void setResizeContentRow(boolean bln) { resizeContentRow.set(bln); }
    public final boolean getResizeContentRow() { return resizeContentRow.get(); }

    public final BooleanProperty resizeContentRowSpan() { return resizeContentRowSpan; }
    public final void setResizeContentRowSpan(boolean bln) { resizeContentRowSpan.set(bln); }
    public final boolean getResizeContentRowSpan() { return resizeContentRowSpan.get(); }

    public final BooleanProperty resizeContentColumn() { return resizeContentColumn; }
    public final void setResizeContentColumn(boolean bln) { resizeContentColumn.set(bln); }
    public final boolean getResizeContentColumn() { return resizeContentColumn.get(); }

    public final BooleanProperty resizeContentColumnSpan() { return resizeContentColumnSpan; }
    public final void setResizeContentColumnSpan(boolean bln) { resizeContentColumnSpan.set(bln); }
    public final boolean getResizeContentColumnSpan() { return resizeContentColumnSpan.get(); }
    
    // expose GridPane.getGrid() to the world
    private static double[][] getCurrentGrid(final GridComboBoxPane gp) {
        double[][] ret = new double [0][0];

        try {
            final Method m = gp.getClass().getSuperclass().getDeclaredMethod("getGrid");
            m.setAccessible(true);
            ret = (double[][]) m.invoke(gp);

            if (ret == null) {
                // no layout pass yet?
                gp.applyCss();
                gp.layout();
                
                ret = (double[][]) m.invoke(gp);
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            Logger.getLogger(GridComboBoxSkin.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }
    
    public double[][] getGrid() {
        return GridComboBoxPane.getCurrentGrid(this);
    }
    
    // adapt layout to resize cells to row/col size 
    @Override 
    protected void layoutChildren() {
        // iterate of children and reset prefWidth/Height based on boolean flags 
        final List<Node> children = new ArrayList<>(getChildren());
        for (Node node: children) {
            // gridlines are stored in a Group...
            if (!Group.class.equals(node.getClass())) {
                if (origSizes.containsKey(node)) {
                    // was already their in last layout run - use stored height/width
                    final Region region = ObjectsHelper.uncheckedCast(node);
                    final Pair<Double, Double> sizes = origSizes.get(node);
                    region.setPrefHeight(sizes.getLeft());
                    region.setPrefWidth(sizes.getRight());
                } else {
                    // new node - store height/width
                    final Region region = ObjectsHelper.uncheckedCast(node);
                    origSizes.put(node, Pair.of(region.getPrefHeight(), region.getPrefWidth()));
                }
            }
        }
        
        // paint the grid to have new row/col sizes
        super.layoutChildren();
        
        // TODO: iterate of children and set prefWidth/Height based on boolean flags
        
        // repaint the grid
        super.layoutChildren();
    }
}
