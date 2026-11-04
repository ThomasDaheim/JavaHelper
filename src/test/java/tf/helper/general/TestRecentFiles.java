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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestRecentFiles {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    // preferences in memory - the real stores of the applications use java.util.prefs
    private static class MapStore implements IPreferencesStore {
        private final Map<String, String> values = new HashMap<>();

        @Override
        public String get(final String key, final String defaultValue) {
            return values.getOrDefault(key, defaultValue);
        }

        @Override
        public void put(final String key, final String value) {
            values.put(key, value);
        }

        @Override
        public void clear() {
            values.clear();
        }

        @Override
        public void remove(final String key) {
            values.remove(key);
        }

        @Override
        public void exportPreferences(final OutputStream os) {
        }

        @Override
        public void importPreferences(final InputStream is) {
        }
    }

    @Test
    public void testFilesKeepOnlyFiles() throws IOException {
        final MapStore store = new MapStore();
        final File file = temp.newFile("track.gpx");
        final File folder = temp.newFolder("fotos");

        final RecentFiles recent = new RecentFiles(store, 5);
        recent.addRecentFile(file.getAbsolutePath());
        recent.addRecentFile(folder.getAbsolutePath());

        Assert.assertEquals(List.of(file.getAbsolutePath()), new RecentFiles(store, 5).getRecentFiles());
    }

    @Test
    public void testDirectoriesKeepOnlyDirectories() throws IOException {
        final MapStore store = new MapStore();
        final File file = temp.newFile("track.gpx");
        final File folder = temp.newFolder("fotos");

        final RecentFiles recent = new RecentFiles(store, 5, true);
        recent.addRecentFile(folder.getAbsolutePath());
        recent.addRecentFile(file.getAbsolutePath());

        Assert.assertEquals(List.of(folder.getAbsolutePath()), new RecentFiles(store, 5, true).getRecentFiles());
    }

    @Test
    public void testMissingDirectoriesAreDropped() throws IOException {
        final MapStore store = new MapStore();
        final File kept = temp.newFolder("kept");
        final File deleted = temp.newFolder("deleted");

        final RecentFiles recent = new RecentFiles(store, 5, true);
        recent.addRecentFile(kept.getAbsolutePath());
        recent.addRecentFile(deleted.getAbsolutePath());
        Assert.assertTrue(deleted.delete());

        Assert.assertEquals(List.of(kept.getAbsolutePath()), new RecentFiles(store, 5, true).getRecentFiles());
    }
}
