/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.general;

/**
 * Interface for classes holding preferences.
 * Need to implement load & save to an IPreferenceStore.
 * 
 * @author thomas
 */
public interface IPreferencesHolder {
    public default void loadPreferences(final IPreferencesStore store) {}
    public default void savePreferences(final IPreferencesStore store) {}
}
