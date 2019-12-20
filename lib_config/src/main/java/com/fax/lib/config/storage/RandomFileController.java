package com.fax.lib.config.storage;

import androidx.annotation.NonNull;

import com.fax.lib.config.api.gettable.IAllKeysGettable;
import com.fax.lib.config.utils.AtomicRandomAccessFile;
import com.fax.lib.config.utils.IKey;

import java.io.*;

import static com.fax.lib.config.utils.Utils.deleteQuietly;

class RandomFileController implements StorageConstant, IAllKeysGettable<String[]> {

    private final File mDir;

    RandomFileController(File dir) {
        mDir = dir;
    }

    Object read(AtomicRandomAccessFile file) {
        Object value = null;
        final IKey key = file.lock();
        if (key == null) {
            return null;
        }
        try {
            final RandomAccessFile in = file.openReadChecked();

            if (in == null) {
                file.closeRead(null);
                return null;
            }

            try {
                value = onRead(in);
            } catch (IOException ignored) {
            } finally {
                file.closeRead(in);
            }
            return value;
        } finally {
            key.unlock();
        }
    }

    void write(AtomicRandomAccessFile file, Object value) {
        final IKey key = file.lock();
        if (key == null) {
            return;
        }
        try {
            RandomAccessFile out = null;
            try {
                out = file.startWrite();
                onWrite(out, value);
                file.finishWrite(out);
            } catch (IOException ignored) {
                file.failWrite(out);
            }
        } finally {
            key.unlock();
        }
    }

    boolean delete(AtomicRandomAccessFile file) {
        return file == null || file.delete();
    }

    boolean hasStorageChanged(AtomicRandomAccessFile file) {
        return file == null || file.hasChanged();
    }

    boolean delete() {
        return deleteQuietly(mDir);
    }

    boolean hasStorageChanged(AtomicRandomAccessFile[] files) {
        boolean result = false;
        for (AtomicRandomAccessFile file : files) {
            result = file.hasChanged() || result;
        }
        return result;
    }

    AtomicRandomAccessFile newFile(String strKey) {
        return new AtomicRandomAccessFile(new File(mDir, strKey));
    }

    private void onWrite(DataOutput out, Object value) throws IOException {
        WriterUtils.writeValue(new DataSimpleValueWriter(out), value);
    }

    private Object onRead(DataInput in) throws IOException {
        return ReaderUtils.readValue(new DataSimpleValueReader(in));
    }

    @NonNull
    @Override
    public String[] getAllKeys() {
        final String[] names = mDir.list();
        return names == null ? new String[0] : names;
    }
}
