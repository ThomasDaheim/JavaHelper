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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import tf.helper.general.ObjectsHelper;

/**
 * Helper to create a menu or a combobox with colored lines menu item.
 * 
 * @author thomas
 */
public class ColorSelection {
    private final static ColorSelection INSTANCE = new ColorSelection();
    
    final static String MENU_TEXT = "Color";
    final static Map<Color, String> JAVAFX_COLORS = getJavaFXColorNames();
    
    private ColorSelection() {
    }

    public static ColorSelection getInstance() {
        return INSTANCE;
    }
    
    /**
     * Return a Map with all all defined colors in JavaFX. The key is the static
     * name of color and the value contains an instance of a Color object.
     */
    private static Map<Color, String> getJavaFXColorNames() {
        final Field[] declaredFields = Color.class.getDeclaredFields();
        final Map<Color, String> colors = new HashMap<>();
        
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                try {
                    colors.put((Color)field.get(null), field.getName());
                } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(ColorSelection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return colors;
    }    
    
    public Menu createColorSelectionMenu(final List<Color> colors, final EventHandler<ActionEvent> callback) {
        final Menu result = new Menu(MENU_TEXT);
        result.setUserData(MENU_TEXT);
        
        ToggleGroup group = new ToggleGroup();
        
        final List<Line> lines = getColoredLines(colors);
        // create menuitem for each color and set event handler
        for (Line line : lines) {
            final RadioMenuItem colorMenu = new RadioMenuItem("");
            colorMenu.setGraphic(line);
            colorMenu.setSelected(false);
            colorMenu.setToggleGroup(group);
            colorMenu.setOnAction(callback);
            colorMenu.setUserData(line.getUserData());
            
            result.getItems().add(colorMenu);
        }
        
        return result;
    }
    
    public ComboBox<Line> createColorSelectionComboBox(final List<Color> colors) {
        final ComboBox<Line> result = new ComboBox<>();
                
        result.getItems().addAll(getColoredLines(colors));
        
        result.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if (isNowShowing) {
                // set focus on selected item
                if (result.getSelectionModel().getSelectedIndex() > -1) {
                    // https://stackoverflow.com/a/36548310
                    // https://stackoverflow.com/a/47933342
                    final ListView<Line> lv = ObjectsHelper.uncheckedCast(((ComboBoxListViewSkin) result.getSkin()).getPopupContent());
                    lv.scrollTo(result.getSelectionModel().getSelectedIndex());
                }
            }
        });
        // clone selected line into button cell - otherwise node gets deleted from dropdown list
        result.setButtonCell(new ListCell<Line>() {
            @Override
            protected void updateItem(Line item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    // create new Line with same properties...
                    final Line line = new Line();
                    line.setStartX(item.getStartX());
                    line.setStartY(item.getStartY());
                    line.setEndX(item.getEndX());
                    line.setEndY(item.getEndY());
                    line.setStroke(item.getStroke());
                    line.setStrokeWidth(item.getStrokeWidth());
                    line.setUserData(item.getUserData());
                    
                    setGraphic(line);
                }
            }            
        });
        
        return result;
    }
    
    private List<Line> getColoredLines(final List<Color> colors) {
        final List<Line> result = new ArrayList<>();
        
        for (Color color : colors) {
            final Line colorLine = new Line(0,0,60,0);
            colorLine.setStroke(color);
            colorLine.setStrokeWidth(8);
            colorLine.setUserData(color);

            final Tooltip t = new Tooltip(JAVAFX_COLORS.get(color));
            Tooltip.install(colorLine, t);
            
            result.add(colorLine);
        }
        
        return result;
    }
    
    public void selectColorInMenu(Menu colors, Color color) {
        // check if right kind of menu
        if (colors == null ||
                !MENU_TEXT.equals(colors.getText()) ||
                colors.getUserData() == null ||
                !(colors.getUserData() instanceof String) ||
                !MENU_TEXT.equals((String) colors.getUserData()) ||
                color == null) {
            return;
        }
        
        // iterate over submenues to find THE ONE
        for (MenuItem item : colors.getItems()) {
            if ((item instanceof RadioMenuItem) && color.equals(item.getUserData())) {
                ((RadioMenuItem) item).setSelected(true);
                
                break;
            }
        }
    }
    
    public void selectColorInComboBox(ComboBox<Line> colors, Color color) {
        // check if right kind of menu
        if (colors == null || color == null) {
            return;
        }
        
        // iterate over lines to find THE ONE
        for (Line item : colors.getItems()) {
            if (color.equals((Color) item.getUserData())) {
                colors.getSelectionModel().select(item);
                break;
            }
        }
    }
}
