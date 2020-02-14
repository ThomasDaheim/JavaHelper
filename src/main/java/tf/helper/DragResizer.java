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
package tf.helper;

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
     * the region.
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

    private final Region region;
    private final ResizeArea area;

    // in which area is the mouse cursor in?
    private boolean inBottom = false;
    private boolean inTop = false;
    private boolean inRight = false;
    private boolean inLeft = false;

    private double startX;
    private double startY;
    
    private boolean initMinWidth;
    private boolean initMinHeight;
    
    private boolean dragging;
    
    private DragResizer(final Region aRegion, final ResizeArea anArea) {
        region = aRegion;
        area = anArea;
    }

    public static void makeResizable(final Region region, final ResizeArea area) {
        final DragResizer resizer = new DragResizer(region, area);
        
        region.setOnMousePressed((MouseEvent event) -> {
            resizer.mousePressed(event);
        });
        region.setOnMouseDragged((MouseEvent event) -> {
            resizer.mouseDragged(event);
        });
        region.setOnMouseMoved((MouseEvent event) -> {
            resizer.mouseOver(event);
        });
        region.setOnMouseReleased((MouseEvent event) -> {
            resizer.mouseReleased(event);
        });
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
    }

    protected void mouseOver(MouseEvent event) {
        if(isInDraggableZone(event) || dragging) {
//            System.out.println("inBottom: " + inBottom + ", " + "inTop: " + inTop + ", " + "inLeft: " + inLeft + ", " + "inRight: " + inRight);
            region.setCursor(Cursor.S_RESIZE);
        }
        else {
            region.setCursor(Cursor.DEFAULT);
        }
    }

    protected boolean isInDraggableZone(MouseEvent event) {
        // test for all combinations of top, bottom, left, right
        inBottom = event.getY() > (region.getHeight() - RESIZE_MARGIN);
        inTop = event.getY() < RESIZE_MARGIN;
        inRight = event.getX() > (region.getWidth()- RESIZE_MARGIN);
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
            double newWidth = region.getMinWidth();
            if (inLeft) {
                newWidth += (mousex - startX);
            } else {
                newWidth -= (mousex - startX);
                // right: startX is max value - we need to recalc difference to previous
                startX = mousex;
            }
//            System.out.println("mousex: " + mousex + ", " + "x: " + startX + ", " + "newWidth: " + newWidth);
            region.setMinWidth(newWidth);
            region.setMaxWidth(newWidth);
            region.setPrefWidth(newWidth);
        }

        if ((inBottom && area.listenBottom()) || (inTop && area.listenTop())) {
            double mousey = event.getY();
            double newHeight = region.getMinHeight();
            if (inBottom) {
                newHeight += (mousey - startY);
                // bottom: startY is max value - we need to recalc difference to previous
                startY = mousey;
            } else {
                newHeight -= (mousey - startY);
            }
//            System.out.println("mousey: " + mousey + ", " + "y: " + startY + ", " + "newHeight: " + newHeight);
            region.setMinHeight(newHeight);
            region.setMaxHeight(newHeight);
            region.setPrefHeight(newHeight);
        }
    }

    protected void mousePressed(MouseEvent event) {
        // ignore clicks outside of the draggable margin
        if(!isInDraggableZone(event)) {
            return;
        }
        
        dragging = true;
        
        // make sure that the minimum width / height is set to the current height once,
        // setting a min height that is smaller than the current height will have no
        if (!initMinWidth) {
            region.setMinWidth(region.getWidth());
            initMinWidth = true;
        }
        startX = event.getX();
        
        if (!initMinHeight) {
            region.setMinHeight(region.getHeight());
            initMinHeight = true;
        }
        startY = event.getY();
    }
}
