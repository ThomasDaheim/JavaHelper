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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestTypedPreference {
    // preferences in memory
    private static final class MapStore implements IPreferencesStore {
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

    private static final MapStore STORE = new MapStore();

    // a choice shown with its own text - as a ChoiceBox would show it
    private enum Service {
        GEONAMES, MAPBOX;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    // like the preference enums of the applications
    private enum TestPreferences implements ITypedPreference {
        SIZE("size", Double.toString(1.5), Double.class),
        ENABLED("enabled", Boolean.toString(true), Boolean.class),
        COUNT("count", Integer.toString(3), Integer.class),
        NAME("name", "", String.class),
        SERVICE("service", Service.GEONAMES.name(), Service.class);

        private final String key;
        private final String defaultValue;
        private final Class<?> valueClass;

        TestPreferences(final String key, final String defaultValue, final Class<?> valueClass) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.valueClass = valueClass;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Class<?> getValueClass() {
            return valueClass;
        }

        @Override
        public IPreferencesStore getStore() {
            return STORE;
        }
    }

    @Before
    public void clear() {
        STORE.clear();
    }

    @Test
    public void typedValues() {
        Assert.assertEquals(1.5, (Double) TestPreferences.SIZE.getAsType(), 1e-9);
        TestPreferences.SIZE.put(2.25);
        TestPreferences.ENABLED.put(false);
        TestPreferences.COUNT.put(7);
        TestPreferences.NAME.put("Meran");

        Assert.assertEquals(2.25, (Double) TestPreferences.SIZE.getAsType(), 1e-9);
        Assert.assertEquals(Boolean.FALSE, TestPreferences.ENABLED.getAsType());
        Assert.assertEquals(Integer.valueOf(7), TestPreferences.COUNT.getAsType());
        Assert.assertEquals("Meran", TestPreferences.NAME.getAsString());
        Assert.assertEquals(1.5, (Double) TestPreferences.SIZE.getDefaultAsType(), 1e-9);
    }

    @Test
    public void invalidValueGivesTheDefault() {
        STORE.put("size", "a lot");
        STORE.put("service", "SOMETHING");
        Assert.assertEquals(1.5, (Double) TestPreferences.SIZE.getAsType(), 1e-9);
        Assert.assertEquals(Service.GEONAMES, TestPreferences.SERVICE.getAsType());
    }

    @Test
    public void enumIsStoredByName() {
        TestPreferences.SERVICE.put(Service.MAPBOX);
        // not "Mapbox" from toString() - that couldn't be read back
        Assert.assertEquals("MAPBOX", STORE.get("service", ""));
        Assert.assertEquals(Service.MAPBOX, TestPreferences.SERVICE.getAsType());
    }

    @Test
    public void storedNamesAreReadAsEnum() {
        // stored as plain text by an older version
        STORE.put("service", "MAPBOX");
        Assert.assertEquals(Service.MAPBOX, TestPreferences.SERVICE.getAsType());
    }
}
