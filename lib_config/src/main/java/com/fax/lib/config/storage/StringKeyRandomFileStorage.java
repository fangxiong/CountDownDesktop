package com.fax.lib.config.storage;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.fax.lib.config.utils.AtomicRandomAccessFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.fax.lib.config.utils.Utils.decodeString;
import static com.fax.lib.config.utils.Utils.encodeString;

class StringKeyRandomFileStorage implements IStringKeyStorage {

    private final RandomFileController mController;
    private final ArrayMap<String, AtomicRandomAccessFile> mFiles = new ArrayMap<>();

    StringKeyRandomFileStorage(File dir) {
        mController = new RandomFileController(dir);
    }

    @Override
    public Object read(String key) {
        return mController.read(getFile(key));
    }

    @Override
    public void write(String key, Object value) {
        mController.write(getFile(key), value);
    }

    @Override
    public boolean delete(String key) {
        AtomicRandomAccessFile file;
        synchronized (this) {
            file = mFiles.get(key);
        }
        return mController.delete(file);
    }

    @Override
    public boolean hasStorageChanged(String key) {
        AtomicRandomAccessFile file;
        synchronized (this) {
            file = mFiles.get(key);
        }
        return mController.hasStorageChanged(file);
    }

    @Override
    public boolean delete() {
        return mController.delete();
    }

    @Override
    public boolean hasStorageChanged() {
        AtomicRandomAccessFile[] files;
        synchronized (this) {
            final int size = mFiles.size();
            files = new AtomicRandomAccessFile[size];
            for (int i = 0; i < size; i++) {
                files[i] = mFiles.valueAt(i);
            }
        }
        return mController.hasStorageChanged(files);
    }

    private AtomicRandomAccessFile getFile(String key) {
        AtomicRandomAccessFile file;
        synchronized (mFiles) {
            file = mFiles.get(key);
            if (file == null) {
                file = mController.newFile(encodeString(key));
                mFiles.put(key, file);
            }
        }
        return file;
    }

    @NonNull
    @Override
    public String[] getAllKeys() {
        final String[] strKeys = mController.getAllKeys();
        if (strKeys.length <= 0) {
            return new String[0];
        }
        List<String> keys = new ArrayList<>(strKeys.length);
        for (String key : strKeys) {
            String value = decodeString(key);
            if (value != null) {
                keys.add(value);
            }
        }
        final int count = keys.size();
        if (count <= 0) {
            return new String[0];
        }
        return keys.toArray(new String[0]);
    }
}
