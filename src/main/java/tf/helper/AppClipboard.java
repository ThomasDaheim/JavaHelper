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

import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Pair;

/**
 * Drop-In replacement for Clipboard but working on app local data storage.
 * Not quit drop-in since
 *   a) all methods in Clipboard are marked final
 *   b) implementing TKClipboard would required implementing drag & drop support as well
 *   c) QuantumClipboard is final & not public
 * So code creates a TKClipboard internally and delegates all calls to it = don't use system clipboard
 * @author thomas
 */
public class AppClipboard {
    private final static AppClipboard INSTANCE = new AppClipboard();

    // the actual worker here :-)
    private static TKClipboard peer;
    /**
     * Whether user has put something on this clipboard. Needed for DnD.
     */
    private boolean contentPut = false;
    
    private AppClipboard() {
        super();
    }
    
    public static AppClipboard getInstance() {
        if (peer == null) {
            peer = Toolkit.getToolkit().createLocalClipboard();
        }

        return INSTANCE;
    }
    
    public boolean contentPut() {
        return contentPut;
    }
    
    // Clears the clipboard of any and all content.
    public void clear() {
        setContent(null);
    }

    // Puts content onto the clipboard.
    @SuppressWarnings("unchecked")
    public boolean setContent(Map<DataFormat, Object> content) {
        if (content == null) {
            contentPut = false;
            peer.putContent(new Pair[0]);
            return true;
        } else {
            Pair<DataFormat, Object>[] data = new Pair[content.size()];
            int index = 0;
            for (Map.Entry<DataFormat, Object> entry : content.entrySet()) {
                data[index++] = new Pair<>(entry.getKey(), entry.getValue());
            }
            contentPut = peer.putContent(data);
            return contentPut;
        }
    }

    // Puts content onto the clipboard.
    @SuppressWarnings("unchecked")
    public boolean setContent(DataFormat format, Object content) {
        if (content == null) {
            contentPut = false;
            peer.putContent(new Pair[0]);
            return true;
        } else {
            Pair<DataFormat, Object>[] data = new Pair[1];
            data[0] = new Pair<>(format, content);
            contentPut = peer.putContent(data);
            return contentPut;
        }
    }
    
    // Returns the content stored in this clipboard of the given type, or null if there is no content with this type.
    public Object getContent(DataFormat dataFormat) {
        return peer.getContent(dataFormat);
    }

    // Returns the content stored in this clipboard of the given type, or null if there is no content with this type.
    public Map<DataFormat, Object> getContentAsMap(DataFormat dataFormat) {
        final Map<DataFormat, Object> result = new HashMap<>();

        final Object content = peer.getContent(dataFormat);
        if (content != null) {
            result.put(dataFormat, content);
        }
        
        return result;
    }

    // Gets the set of DataFormat types on this Clipboard instance which have associated data registered on the clipboard.
    public Set<DataFormat> getContentTypes() {
        return peer.getContentTypes();
    }

    // Tests whether there is any content on this clipboard of the given DataFormat type.
    public boolean hasContent(DataFormat dataFormat) {
        return peer.hasContent(dataFormat);
    }

    //Gets whether an List of Files (DataFormat.FILES) has been registered on this Clipboard.
    public boolean hasFiles() {
        return peer.hasContent(DataFormat.FILES);
    }

    // Gets the List of Files from the clipboard which had previously been registered.
    @SuppressWarnings("unchecked")
    public List<File> getFiles() {
        return (List<File>) peer.getContent(DataFormat.FILES);
    }

    // Gets whether an HTML text String (DataFormat.HTML) has been registered on this Clipboard.
    public boolean hasHtml() {
        return peer.hasContent(DataFormat.HTML);
    }

    // Gets the HTML text String from the clipboard which had previously been registered.
    public String getHtml() {
        return (String) peer.getContent(DataFormat.HTML);
    }

    // Gets whether an Image (DataFormat.IMAGE) has been registered on this Clipboard.
    public boolean hasImage() {
        return peer.hasContent(DataFormat.IMAGE);
    }

    // Gets the Image from the clipboard which had previously been registered.
    public Image getImage() {
        return (Image) peer.getContent(DataFormat.IMAGE);
    }

    // Gets whether an RTF String (DataFormat.RTF) has been registered on this Clipboard.
    public boolean hasRtf() {
        return peer.hasContent(DataFormat.RTF);
    }

    // Gets the RTF text String from the clipboard which had previously been registered.
    public String getRtf() {
        return (String) peer.getContent(DataFormat.RTF);
    }

    // Gets whether a plain text String (DataFormat.PLAIN_TEXT) has been registered on this Clipboard.
    public boolean hasString() {
        return peer.hasContent(DataFormat.PLAIN_TEXT);
    }

    // Gets the plain text String from the clipboard which had previously been registered.
    public String getString() {
        return (String) peer.getContent(DataFormat.PLAIN_TEXT);
    }

    // Gets whether a url String (DataFormat.URL) has been registered on this Clipboard.
    public boolean hasUrl() {
        return peer.hasContent(DataFormat.URL);
    }
    // Gets the URL String from the clipboard which had previously been registered.
    public String getUrl() {
        return (String) peer.getContent(DataFormat.URL);
    }

    // Gets the current system clipboard, through which data can be stored and retrieved.
    public static Clipboard getSystemClipboard() {
        return Clipboard.getSystemClipboard();
    }
}
