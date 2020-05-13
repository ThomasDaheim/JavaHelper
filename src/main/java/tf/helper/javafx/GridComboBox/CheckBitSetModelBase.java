/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx.GridComboBox;

import java.util.BitSet;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import com.sun.javafx.collections.MappingChange;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;

/**
 *
 * @author t.feuster
 */
abstract class CheckBitSetModelBase<T> implements IndexedCheckModel<T> { 
    
    /***********************************************************************
     *                                                                     *
     * Internal properties                                                 *
     *                                                                     *
     **********************************************************************/

    private final Map<T, BooleanProperty> itemBooleanMap;
    
    private final BitSet checkedIndices;
    private final ReadOnlyUnbackedObservableList<Integer> checkedIndicesList;
    private final ReadOnlyUnbackedObservableList<T> checkedItemsList;
    
    

    /***********************************************************************
     *                                                                     *
     * Constructors                                                        *
     *                                                                     *
     **********************************************************************/

    CheckBitSetModelBase(final Map<T, BooleanProperty> itemBooleanMap) {
        this.itemBooleanMap = itemBooleanMap;
        
        this.checkedIndices = new BitSet();
        
        this.checkedIndicesList = new ReadOnlyUnbackedObservableList<Integer>() {
            @Override public Integer get(int index) {
                if (index < 0 || index >= getItemCount()) return -1;

                for (int pos = 0, val = checkedIndices.nextSetBit(0);
                    val >= 0 || pos == index;
                    pos++, val = checkedIndices.nextSetBit(val+1)) {
                        if (pos == index) return val;
                }

                return -1;
            }

            @Override public int size() {
                return checkedIndices.cardinality();
            }

            @Override public boolean contains(Object o) {
                if (o instanceof Number) {
                    Number n = (Number) o;
                    int index = n.intValue();

                    return index >= 0 && index < checkedIndices.length() &&
                            checkedIndices.get(index);
                }

                return false;
            }
        };
        
        this.checkedItemsList = new ReadOnlyUnbackedObservableList<T>() {
            @Override public T get(int i) {
                int pos = checkedIndicesList.get(i);
                if (pos < 0 || pos >= getItemCount()) return null;
                return getItem(pos);
            }

            @Override public int size() {
                return checkedIndices.cardinality();
            }
        };
        
        final MappingChange.Map<Integer,T> map = f -> getItem(f);
        
        checkedIndicesList.addListener((ListChangeListener<Integer>) c -> {
            // when the selectedIndices ObservableList changes, we manually call
            // the observers of the selectedItems ObservableList.
            boolean hasRealChangeOccurred = false;
            while (c.next() && ! hasRealChangeOccurred) {
                hasRealChangeOccurred = c.wasAdded() || c.wasRemoved();
            }

            if (hasRealChangeOccurred) {
                c.reset();
                checkedItemsList.callObservers(new MappingChange<>(c, map, checkedItemsList));
            }
            c.reset();
        });
        
        // this code is to handle the situation where a developer is manually
        // toggling the check model, and expecting the UI to update (without
        // this it won't happen!).
        getCheckedItems().addListener((ListChangeListener<T>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (T item : c.getAddedSubList()) {
                        BooleanProperty p = getItemBooleanProperty(item);
                        if (p != null) {
                            p.set(true);
                        }
                    }
                } 
                
                if (c.wasRemoved()) {
                    for (T item : c.getRemoved()) {
                        BooleanProperty p = getItemBooleanProperty(item);
                        if (p != null) {
                            p.set(false);
                        }
                    }
                }
            }
        });
    }
    
    
    
    /***********************************************************************
     *                                                                     *
     * Abstract API                                                        *
     *                                                                     *
     **********************************************************************/

    @Override
    public abstract T getItem(int index);
    
    @Override
    public abstract int getItemCount();
    
    @Override
    public abstract int getItemIndex(T item);
    
    BooleanProperty getItemBooleanProperty(T item) {
        return itemBooleanMap.get(item);
    }
    
    
    /***********************************************************************
     *                                                                     *
     * Public selection API                                                *
     *                                                                     *
     **********************************************************************/
    
    /**
     * Returns a read-only list of the currently checked indices in the CheckBox.
     */
    @Override
    public ObservableList<Integer> getCheckedIndices() {
        return checkedIndicesList;
    }
    
    /**
     * Returns a read-only list of the currently checked items in the CheckBox.
     */
    @Override
    public ObservableList<T> getCheckedItems() {
        return checkedItemsList;
    }

    /** {@inheritDoc} */
    @Override
    public void checkAll() {
        for (int i = 0; i < getItemCount(); i++) {
            check(i);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void checkIndices(int... indices) {
        for (int i = 0; i < indices.length; i++) {
            check(indices[i]);
        }
    }
    
    /** {@inheritDoc} */
    @Override public void clearCheck(T item) {
        int index = getItemIndex(item);
        clearCheck(index);        
    }

    /** {@inheritDoc} */
    @Override
    public void clearChecks() {
        for( int index = 0; index < checkedIndices.length(); index++) {
            clearCheck(index);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearCheck(int index) {
        if (index < 0 || index >= getItemCount()) return;
        checkedIndices.clear(index);
        
        final int changeIndex = checkedIndicesList.indexOf(index);
        checkedIndicesList.callObservers(new NonIterableChange.SimpleRemovedChange<>(changeIndex, changeIndex, index, checkedIndicesList));
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return checkedIndices.isEmpty();
    }
    
    /** {@inheritDoc} */
    @Override public boolean isChecked(T item) {
        int index = getItemIndex(item);
        return isChecked(index);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isChecked(int index) {
        return checkedIndices.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public void toggleCheckState(T item) {
        int index = getItemIndex(item);
        toggleCheckState(index);
    }

    /** {@inheritDoc} */
    @Override
    public void toggleCheckState(int index) {
        if (isChecked(index)) {
            clearCheck(index);
        } else {
            check(index);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void check(int index) {
        if (index < 0 || index >= getItemCount()) return;
        checkedIndices.set(index);
        final int changeIndex = checkedIndicesList.indexOf(index);
        checkedIndicesList.callObservers(new NonIterableChange.SimpleAddChange<>(changeIndex, changeIndex+1, checkedIndicesList));
    }

    /** {@inheritDoc} */
    @Override
    public void check(T item) {
        int index = getItemIndex(item);
        check(index);
    }
    
    /***********************************************************************
     *                                                                     *
     * Private implementation                                              *
     *                                                                     *
     **********************************************************************/
    
    protected void updateMap() {
        // reset the map
        itemBooleanMap.clear();
        for (int i = 0; i < getItemCount(); i++) {
            final int index = i;
            final T item = getItem(index);
            
            final BooleanProperty booleanProperty = new SimpleBooleanProperty(item, "selected", false); //$NON-NLS-1$
            itemBooleanMap.put(item, booleanProperty);
            
            // this is where we listen to changes to the boolean properties,
            // updating the selected indices list (and therefore indirectly
            // the selected items list) when the checkbox is toggled
            booleanProperty.addListener(new InvalidationListener() {
                @Override public void invalidated(Observable o) {
                    if (booleanProperty.get()) {
                        checkedIndices.set(index);
                        final int changeIndex = checkedIndicesList.indexOf(index);
                        checkedIndicesList.callObservers(new NonIterableChange.SimpleAddChange<>(changeIndex, changeIndex+1, checkedIndicesList));                            
                    } else {
                        final int changeIndex = checkedIndicesList.indexOf(index);
                        checkedIndices.clear(index);
                        checkedIndicesList.callObservers(new NonIterableChange.SimpleRemovedChange<>(changeIndex, changeIndex, index, checkedIndicesList));
                    }
                }
            });
        }
    }
}

