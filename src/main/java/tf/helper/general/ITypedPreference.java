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

/**
 * One typed preference of an application, usually a constant of its preference enum:
 * <pre>{@code
 * public enum MyPreferences implements ITypedPreference {
 *     WINDOW_WIDTH("windowWidth", Double.toString(1200), Double.class);
 *     ... constructor and the four getters ...
 * }
 * double width = MyPreferences.WINDOW_WIDTH.getAsType();
 * }</pre>
 * The value is stored as text: enums by name, everything else by toString(). Reading parses it with
 * {@link GeneralParser} - a value that can't be parsed gives the default.
 */
public interface ITypedPreference {
    /**
     * @return key in the store
     */
    String getKey();

    /**
     * @return the default as text, e.g. Double.toString(1200) or MyEnum.VALUE.name()
     */
    String getDefaultValue();

    /**
     * @return type of the value, e.g. Double.class or an enum class
     */
    Class<?> getValueClass();

    /**
     * @return store of the application, e.g. a {@link NodePreferencesStore}
     */
    IPreferencesStore getStore();

    /**
     * @return the stored text, the default if nothing is stored
     */
    default String getAsString() {
        return getStore().get(getKey(), getDefaultValue());
    }

    /**
     * @param <T> type of the value
     * @return the stored value - the default if nothing is stored or the stored text can't be parsed
     */
    default <T> T getAsType() {
        try {
            return ObjectsHelper.uncheckedCast(GeneralParser.parse(getAsString(), ObjectsHelper.uncheckedCast(getValueClass())));
        } catch (Exception ex) {
            return getDefaultAsType();
        }
    }

    /**
     * @param <T> type of the value
     * @return the default value
     */
    default <T> T getDefaultAsType() {
        return ObjectsHelper.uncheckedCast(GeneralParser.parse(getDefaultValue(), ObjectsHelper.uncheckedCast(getValueClass())));
    }

    /**
     * @param <T> type of the value
     * @param value value to store - an enum is stored by its name, so that a toString() for display doesn't matter
     */
    default <T> void put(final T value) {
        getStore().put(getKey(), value instanceof Enum<?> constant ? constant.name() : value.toString());
    }
}
