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

import com.sun.javafx.tk.TKClipboard;
import java.io.File;
import java.security.AccessControlContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;
import tf.helper.general.ObjectsHelper;

/**
 * Drop-In replacement for Clipboard but working on app local data storage.
 * Not quit drop-in since
 *   a) all methods in Clipboard are marked final
 *   b) implementing TKClipboard would required implementing drag & drop support as well
 *   c) QuantumClipboard is final & not public
 *   d) LocalClipboard is final
 * So duplicate code from LocalClipboard = don't use system clipboard
 * @author thomas
 */
public class AppClipboard implements TKClipboard {
    private final static AppClipboard INSTANCE = new AppClipboard();
    
    private final Map<DataFormat, Object> values = new HashMap<>();
    
    /**
     * Whether user has put something on this clipboard. Needed for DnD.
     */
    private final BooleanProperty hasContentProperty = new SimpleBooleanProperty(false);
    // poor mans change listener to avoid ObservableMap & its exposure
    private final IntegerProperty putCountProperty = new SimpleIntegerProperty(0);
    
    private AppClipboard() {
        super();
    }
    
    public static AppClipboard getInstance() {
        return INSTANCE;
    }
    
    public BooleanProperty hasContentProperty() {
        return hasContentProperty;
    }
    
    public IntegerProperty putCountProperty() {
        return putCountProperty;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean putContent(Pair<DataFormat, Object>... content) {
        return putContent(true, content);
    }

    @SuppressWarnings("unchecked")
    private boolean putContent(final boolean doClear, final Pair<DataFormat, Object>... content) {
        for (final Pair<DataFormat, Object> pair: content) {
            if (pair.getKey() == null) {
                throw new NullPointerException("AppClipboard.putContent: null data format");
            }
            if (pair.getValue() == null) {
                throw new NullPointerException("AppClipboard.putContent: null data");
            }
        }

        // all OK, replace clipboard content
        if (doClear) {
            values.clear();
            hasContentProperty.set(false);
            putCountProperty.set(0);
        }
        for (final Pair<DataFormat, Object> pair: content) {
            values.put(pair.getKey(), pair.getValue());
            hasContentProperty.set(true);
            putCountProperty.set(putCountProperty.get()+1);
        }

        return hasContentProperty.get();
    }
    
    // Clears the clipboard of any and all content.
    public void clear() {
        setContent(null);
    }
    
    public void clearContent(DataFormat dataFormat) {
        values.remove(dataFormat);
    }

    // Puts content onto the clipboard.
    public boolean setContent(Map<DataFormat, Object> content) {
        return newContent(true, content);
    }

    // Puts content onto the clipboard.
    public boolean addContent(Map<DataFormat, Object> content) {
        return newContent(false, content);
    }
    
    private boolean newContent(final boolean doClear, Map<DataFormat, Object> content) {
        // slightly different than the code in Clipboard: we don't have to fiddle around here with hasContentProperty
        if (content == null) {
            putContent(doClear, ObjectsHelper.uncheckedCast(new Pair[0]));
            return true;
        } else {
            Pair<DataFormat, Object>[] data = ObjectsHelper.uncheckedCast(new Pair[content.size()]);
            int index = 0;
            for (Map.Entry<DataFormat, Object> entry : content.entrySet()) {
                data[index++] = new Pair<>(entry.getKey(), entry.getValue());
            }
            return putContent(doClear, data);
        }
    }

    // Puts content onto the clipboard.
    public boolean setContent(DataFormat format, Object content) {
        return newContent(true, format, content);
    }
    
    // Adds content onto the clipboard.
    public boolean addContent(DataFormat format, Object content) {
        return newContent(false, format, content);
    }
    
    private boolean newContent(final boolean doClear, DataFormat format, Object content) {
        // slightly different than the code in Clipboard: we don't have to fiddle around here with hasContentProperty
        if (content == null) {
            putContent(doClear, ObjectsHelper.uncheckedCast(new Pair[0]));
            return true;
        } else {
            Pair<DataFormat, Object>[] data = ObjectsHelper.uncheckedCast(new Pair[1]);
            data[0] = new Pair<>(format, content);
            return putContent(doClear, data);
        }
    }
    
    // Returns the content stored in this clipboard of the given type, or null if there is no content with this type.
    @Override
    public Object getContent(DataFormat dataFormat) {
        return values.get(dataFormat);
    }
    
    public ClipboardContent getAsClipboardContent(DataFormat dataFormat) {
        final ClipboardContent result = new ClipboardContent();
        
        result.put(dataFormat, getContent(dataFormat));
        
        return result;
    }

    // Gets the set of DataFormat types on this Clipboard instance which have associated data registered on the clipboard.
    @Override
    public Set<DataFormat> getContentTypes() {
        return Collections.unmodifiableSet(new HashSet<>(values.keySet()));
    }

    // Tests whether there is any content on this clipboard of the given DataFormat type.
    @Override
    public boolean hasContent(DataFormat dataFormat) {
        return values.containsKey(dataFormat);
    }

    //Gets whether an List of Files (DataFormat.FILES) has been registered on this Clipboard.
    public boolean hasFiles() {
        return hasContent(DataFormat.FILES);
    }

    // Gets the List of Files from the clipboard which had previously been registered.
    public List<File> getFiles() {
        return ObjectsHelper.uncheckedCast(getContent(DataFormat.FILES));
    }

    // Gets whether an HTML text String (DataFormat.HTML) has been registered on this Clipboard.
    public boolean hasHtml() {
        return hasContent(DataFormat.HTML);
    }

    // Gets the HTML text String from the clipboard which had previously been registered.
    public String getHtml() {
        return (String) getContent(DataFormat.HTML);
    }

    // Gets whether an Image (DataFormat.IMAGE) has been registered on this Clipboard.
    public boolean hasImage() {
        return hasContent(DataFormat.IMAGE);
    }

    // Gets the Image from the clipboard which had previously been registered.
    public Image getImage() {
        return (Image) getContent(DataFormat.IMAGE);
    }

    // Gets whether an RTF String (DataFormat.RTF) has been registered on this Clipboard.
    public boolean hasRtf() {
        return hasContent(DataFormat.RTF);
    }

    // Gets the RTF text String from the clipboard which had previously been registered.
    public String getRtf() {
        return (String) getContent(DataFormat.RTF);
    }

    // Gets whether a plain text String (DataFormat.PLAIN_TEXT) has been registered on this Clipboard.
    public boolean hasString() {
        return hasContent(DataFormat.PLAIN_TEXT);
    }

    // Gets the plain text String from the clipboard which had previously been registered.
    public String getString() {
        return (String) getContent(DataFormat.PLAIN_TEXT);
    }

    // Gets whether a url String (DataFormat.URL) has been registered on this Clipboard.
    public boolean hasUrl() {
        return hasContent(DataFormat.URL);
    }
    // Gets the URL String from the clipboard which had previously been registered.
    public String getUrl() {
        return (String) getContent(DataFormat.URL);
    }

    // Gets the current system clipboard, through which data can be stored and retrieved.
    public static Clipboard getSystemClipboard() {
        return Clipboard.getSystemClipboard();
    }

    @Override
    public void setSecurityContext(final AccessControlContext ctx) {
        // ctx not needed
    }
    
    @Override
    public Set<TransferMode> getTransferModes() {
        throw new IllegalStateException();
    }

    @Override
    public void setDragView(final Image image) {
        throw new IllegalStateException();
    }

    @Override
    public void setDragViewOffsetX(final double offsetX) {
        throw new IllegalStateException();
    }

    @Override
    public void setDragViewOffsetY(final double offsetY) {
        throw new IllegalStateException();
    }

    @Override
    public Image getDragView() {
        throw new IllegalStateException();
    }

    @Override
    public double getDragViewOffsetX() {
        throw new IllegalStateException();
    }

    @Override
    public double getDragViewOffsetY() {
        throw new IllegalStateException();
    }
}
