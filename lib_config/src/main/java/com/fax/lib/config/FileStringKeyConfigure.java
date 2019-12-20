package com.fax.lib.config;

import android.util.ArrayMap;

import com.fax.lib.config.storage.IStringKeyStorage;

public class FileStringKeyConfigure<NT> extends MapLikeStringKeyConfigure {

    private static final Object LOADED_NULL_OBJ = new byte[0];

    private final IStringKeyStorage mStorage;
    private final NT mNamespace;

    private final ArrayMap<String, Object> mMap = new ArrayMap<>();
    private volatile boolean isAvailable = true;

    FileStringKeyConfigure(IStringKeyStorage storage, NT namespace) {
        mStorage = storage;
        mNamespace = namespace;
    }

    @Override
    public boolean contains(String key) {
        synchronized (this) {
            final int index = mMap.indexOfKey(key);
            return index >= 0 && index < mMap.size();
        }
    }

    void reload() {
        synchronized (this) {
            final int size = mMap.size();
            String key;
            for (int i = 0; i < size; i++) {
                key = mMap.keyAt(i);
                if (mStorage.hasStorageChanged(key)) {
                    mMap.setValueAt(i, mStorage.read(key));
                }
            }
        }
    }

    @Override
    public void remove(String key) {
        synchronized (this) {
            mMap.remove(key);
        }
        mStorage.delete(key);
    }

    @Override
    public void clear() {
        synchronized (this) {
            mMap.clear();
        }
        mStorage.delete();
    }

    @Override
    public void delete() {
        mStorage.delete();
        isAvailable = false;
    }

    boolean isAvailable() {
        return isAvailable;
    }

    IStringKeyStorage getStorage() {
        return mStorage;
    }

    NT getNamespace() {
        return mNamespace;
    }

    @Override
    public void put(String key, Object value) {
        synchronized (this) {
            mMap.put(key, value);
        }
        mStorage.write(key, value);
    }

    @Override
    public Object get(String key) {
        Object value;
        synchronized (this) {
            value = mMap.get(key);
        }
        if (value == null || mStorage.hasStorageChanged(key)) {
            value = mStorage.read(key);
            if (value == null) {
                value = LOADED_NULL_OBJ;
            }
            synchronized (this) {
                mMap.put(key, value);
            }
        }
        return value == LOADED_NULL_OBJ ? null : value;
    }

    @Override
    public String[] getAllKeys() {
        return mStorage.getAllKeys();
    }
}
