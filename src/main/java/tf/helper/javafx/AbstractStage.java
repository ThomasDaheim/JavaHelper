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

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author thomas
 */
public abstract class AbstractStage extends Stage {
    private final String ROOT_PANE_CSS = "abstract-stage";
    private final String GRID_PANE_CSS = "abstract-stage-grid-pane";
    private final String SCROLL_PANE_CSS = "abstract-stage-scroll-pane";
    
    private final GridPane myGridPane = new GridPane();
    private final VBox myRootPane = new VBox();

    public final static Insets INSET_NONE = new Insets(0, 0, 0, 0);
    public final static Insets INSET_SMALL = new Insets(0, 8, 0, 8);
    public final static Insets INSET_TOP = new Insets(8, 8, 0, 8);
    public final static Insets INSET_BOTTOM = new Insets(0, 8, 8, 8);
    public final static Insets INSET_TOP_BOTTOM = new Insets(8, 8, 8, 8);
    
    protected static enum ButtonPressed {
        ACTION_BUTTON,
        CANCEL_BUTTON;
    }
    
    private ButtonPressed buttonPressed;
    
    public AbstractStage() {
        initStage();
    }
    
    public GridPane getGridPane() {
        return myGridPane;
    }
    
    public VBox getRootPane() {
        return myRootPane;
    }
    
    public ButtonPressed getButtonPressed() {
        return buttonPressed;
    }
    public boolean wasActionButtonPressed() {
        return ButtonPressed.ACTION_BUTTON.equals(buttonPressed);
    }
    public boolean wasCancelButtonPressed() {
        return ButtonPressed.CANCEL_BUTTON.equals(buttonPressed);
    }
    
    private void initStage() {
        final ScrollPane scrollPane = new ScrollPane(myGridPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(INSET_NONE);
        
        myRootPane.getChildren().add(scrollPane);
        myRootPane.setPadding(INSET_NONE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        setScene(new Scene(myRootPane));
        setResizable(false);

        myRootPane.getStyleClass().add(ROOT_PANE_CSS);
        myGridPane.getStyleClass().add(GRID_PANE_CSS);
        scrollPane.getStyleClass().add(SCROLL_PANE_CSS);
    }
    
    public void setActionAccelerator(final Button button) {
        button.setDefaultButton(true);
        // more than one action handler - implementation has its own
        // https://stackoverflow.com/a/29880122
        button.addEventHandler(ActionEvent.ACTION, (t) -> {
            buttonPressed = ButtonPressed.ACTION_BUTTON;
        });
        
        final Runnable saveRN = () -> {
            buttonPressed = ButtonPressed.ACTION_BUTTON;
            button.fire();
        };

        getScene().getAccelerators().put(UsefulKeyCodes.CNTRL_S.getKeyCodeCombination(), saveRN);
    }
    
    public void setCancelAccelerator(final Button button) {
        button.setCancelButton(true);
        button.addEventHandler(ActionEvent.ACTION, (t) -> {
            buttonPressed = ButtonPressed.CANCEL_BUTTON;
        });
    }
    
    @Override
    public void showAndWait() {
        buttonPressed = null;
        
        if (!super.isShowing()) {
            super.showAndWait();
        } else {
            super.toFront();
        }
    }
}
