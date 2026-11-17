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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TestNodePreferencesStore {
    // a node of its own per test - the preferences of the applications stay untouched
    private final Preferences node = Preferences.userRoot().node("tf/javahelper/test/" + UUID.randomUUID());
    private final NodePreferencesStore store = new NodePreferencesStore(node);

    @After
    public void removeNode() throws BackingStoreException {
        node.removeNode();
    }

    @Test
    public void putGetRemoveClear() {
        Assert.assertEquals("default", store.get("key", "default"));
        store.put("key", "value");
        store.put("other", "x");
        Assert.assertEquals("value", store.get("key", "default"));
        Assert.assertSame(node, store.node());

        store.remove("key");
        Assert.assertEquals("default", store.get("key", "default"));
        store.clear();
        Assert.assertEquals("gone", store.get("other", "gone"));
    }

    @Test
    public void exportAndImport() {
        store.put("key", "value");
        final ByteArrayOutputStream exported = new ByteArrayOutputStream();
        store.exportPreferences(exported);
        store.clear();
        Assert.assertEquals("default", store.get("key", "default"));

        store.importPreferences(new ByteArrayInputStream(exported.toByteArray()));

        Assert.assertEquals("value", store.get("key", "default"));
    }
}
