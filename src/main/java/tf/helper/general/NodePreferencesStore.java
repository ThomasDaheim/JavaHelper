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
package tf.helper.general;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

/**
 * Preferences of an application in a java.util.prefs node - the part the preference enums of GPXEditor and GeoTagger
 * had each on their own. Problems of the backing store are logged, reading falls back to the default.
 */
public class NodePreferencesStore implements IPreferencesStore {
    private static final Logger LOGGER = Logger.getLogger(NodePreferencesStore.class.getName());

    private final Preferences node;

    /**
     * @param node node of the application, e.g. Preferences.userNodeForPackage(MyApp.class)
     */
    public NodePreferencesStore(final Preferences node) {
        this.node = Objects.requireNonNull(node);
    }

    /**
     * @return the node of the application
     */
    public Preferences node() {
        return node;
    }

    @Override
    public String get(final String key, final String defaultValue) {
        try {
            return node.get(key, defaultValue);
        } catch (SecurityException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return defaultValue;
        }
    }

    @Override
    public void put(final String key, final String value) {
        node.put(key, value);
    }

    @Override
    public void clear() {
        try {
            node.clear();
        } catch (BackingStoreException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void remove(final String key) {
        node.remove(key);
    }

    @Override
    public void exportPreferences(final OutputStream os) {
        try {
            node.exportSubtree(os);
        } catch (BackingStoreException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Imports exported preferences into their nodes - the application has to read its settings again afterwards.
     *
     * @param is preferences as written by exportPreferences
     */
    @Override
    public void importPreferences(final InputStream is) {
        try {
            Preferences.importPreferences(is);
        } catch (InvalidPreferencesFormatException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
