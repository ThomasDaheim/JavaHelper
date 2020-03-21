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

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author thomas
 */
public enum UsefulKeyCodes {
    CNTRL_A(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY)),
    CNTRL_C(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY)),
    CNTRL_V(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY)),
    SHIFT_CNTRL_V(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY, KeyCombination.SHIFT_DOWN)),
    CNTRL_X(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY)),
    SHIFT_DEL(new KeyCodeCombination(KeyCode.DELETE, KeyCombination.SHIFT_DOWN)),
    DEL(new KeyCodeCombination(KeyCode.DELETE)),
    SHIFT_INSERT(new KeyCodeCombination(KeyCode.INSERT, KeyCombination.SHIFT_DOWN)),
    INSERT(new KeyCodeCombination(KeyCode.INSERT)),
    // TFE, 20200214: add some for save, cancel, esc, ...
    CNTRL_S(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY)),
    ESCAPE(new KeyCodeCombination(KeyCode.ESCAPE));

    private final KeyCodeCombination keyCode;

    private UsefulKeyCodes(final KeyCodeCombination key) {
        keyCode = key;
    }

    public KeyCodeCombination getKeyCodeCombination() {
        return keyCode;
    }

    public boolean match(final KeyEvent event) {
        return keyCode.match(event);
    }
}
