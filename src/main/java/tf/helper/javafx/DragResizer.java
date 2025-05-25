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

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * Based on the code from andytill - https://gist.github.com/andytill/4369729
 * @author thomas
 */

/**
 * {@link DragResizer} can be used to add mouse listeners to a {@link Region}
 * and make it resizable by the user by clicking and dragging the border in the
 * same way as a window.
 * <p>
 * Usage: <pre>DragResizer.makeResizable(Region, ResizeAreas);</pre>
 * 
 * @author atill
 * 
 */
public class DragResizer {
    /**
     * The margin around the control that a user can click in to start resizing
 the listenRegion.
     */
    private static final int RESIZE_MARGIN = 5;
    
    // where should we listen for resize events?
    public enum ResizeArea {
        ALL,
        TOP,
        BOTTOM,
        TOP_BOTTOM,
        LEFT,
        RIGHT,
        LEFT_RIGHT;
        
        public boolean listenTop() {
            return (ALL.equals(this) || TOP.equals(this) || TOP_BOTTOM.equals(this));
        }
        public boolean listenBottom() {
            return (ALL.equals(this) || BOTTOM.equals(this) || TOP_BOTTOM.equals(this));
        }
        public boolean listenLeft() {
            return (ALL.equals(this) || LEFT.equals(this) || LEFT_RIGHT.equals(this));
        }
        public boolean listenRight() {
            return (ALL.equals(this) || RIGHT.equals(this) || LEFT_RIGHT.equals(this));
        }
    }

    private final Region listenRegion;
    private final Region resizeRegion;
    private final ResizeArea area;

    // in which area is the mouse cursor in?
    private boolean inBottom = false;
    private boolean inTop = false;
    private boolean inRight = false;
    private boolean inLeft = false;

    private double startX;
    private double startY;
    
    private boolean dragging;
    
    private DragResizer(final Region lRegion, final Region rRegion, final ResizeArea anArea) {
        listenRegion = lRegion;
        resizeRegion = rRegion;
        area = anArea;
    }

    public static void makeResizable(final Region lRegion, final Region rRegion, final ResizeArea anArea) {
        final DragResizer resizer = new DragResizer(lRegion, rRegion, anArea);
        
        lRegion.setOnMousePressed((MouseEvent event) -> {
            resizer.mousePressed(event);
        });
        lRegion.setOnMouseDragged((MouseEvent event) -> {
            resizer.mouseDragged(event);
        });
        lRegion.setOnMouseMoved((MouseEvent event) -> {
            resizer.mouseOver(event);
        });
        lRegion.setOnMouseReleased((MouseEvent event) -> {
            resizer.mouseReleased(event);
        });
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        listenRegion.setCursor(Cursor.DEFAULT);
    }

    protected void mouseOver(MouseEvent event) {
        if(isInDraggableZone(event) || dragging) {
//            System.out.println("inBottom: " + inBottom + ", " + "inTop: " + inTop + ", " + "inLeft: " + inLeft + ", " + "inRight: " + inRight);
            // cursor change is only visible of set to NONE on any overlapping childs
            if (inBottom && inLeft) {
                listenRegion.setCursor(Cursor.NE_RESIZE);
            } else if (inBottom && inRight) {
                listenRegion.setCursor(Cursor.NW_RESIZE);
            } else if (inTop && inLeft) {
                listenRegion.setCursor(Cursor.SE_RESIZE);
            } else if (inTop && inRight) {
                listenRegion.setCursor(Cursor.SW_RESIZE);
            } else {
                if (inBottom || inTop) {
                    listenRegion.setCursor(Cursor.V_RESIZE);
                } else {
                    listenRegion.setCursor(Cursor.W_RESIZE);
                }
            }
        }
        else {
            listenRegion.setCursor(Cursor.DEFAULT);
        }
    }

    protected boolean isInDraggableZone(MouseEvent event) {
        // test for all combinations of top, bottom, left, right
        inBottom = event.getY() > (listenRegion.getHeight() - RESIZE_MARGIN);
        inTop = event.getY() < RESIZE_MARGIN;
        inRight = event.getX() > (listenRegion.getWidth()- RESIZE_MARGIN);
        inLeft = event.getX() < RESIZE_MARGIN;
        
        return (inBottom && area.listenBottom()) ||
                (inTop && area.listenTop()) ||
                (inLeft && area.listenLeft()) ||
                (inRight && area.listenRight());
    }

    protected void mouseDragged(MouseEvent event) {
        if(!dragging) {
            return;
        }
        
        if ((inLeft && area.listenLeft()) || (inRight && area.listenRight())) {
            double mousex = event.getX();
            double newWidth = resizeRegion.getPrefWidth();
            if (inLeft) {
                newWidth -= (mousex - startX);
            } else {
                newWidth += (mousex - startX);
                // right: startX is max value - we need to recalc difference to previous
                startX = mousex;
            }
//            System.out.println("mousex: " + mousex + ", " + "x: " + startX + ", " + "newWidth: " + newWidth);
            resizeRegion.setPrefWidth(newWidth);
        }

        if ((inBottom && area.listenBottom()) || (inTop && area.listenTop())) {
            double mousey = event.getY();
            double newHeight = resizeRegion.getPrefHeight();
            if (inBottom) {
                newHeight += (mousey - startY);
                // bottom: startY is max value - we need to recalc difference to previous
                startY = mousey;
            } else {
                newHeight -= (mousey - startY);
            }
//            System.out.println("mousey: " + mousey + ", " + "y: " + startY + ", " + "newHeight: " + newHeight);
            resizeRegion.setPrefHeight(newHeight);
        }
    }

    protected void mousePressed(MouseEvent event) {
        // ignore clicks outside of the draggable margin
        if(!isInDraggableZone(event)) {
            return;
        }
        
        dragging = true;
        startX = event.getX();
        startY = event.getY();
    }
}
