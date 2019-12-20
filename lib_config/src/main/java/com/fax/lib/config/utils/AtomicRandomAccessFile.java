package com.fax.lib.config.utils;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.fax.lib.config.utils.AtomicFileLock.LOCK_LEVEL_PROCESS;
import static com.fax.lib.config.utils.AtomicFileLock.LOCK_LEVEL_THREAD;
import static com.fax.lib.config.utils.Utils.*;

/**
 * 原子文件类， 支持多线程和多进程之间读写文件
 */
public class AtomicRandomAccessFile implements ILock {

    private static final String TAG = "test_atomic_file";


    private final File mDir;
    private final File mBaseName;
    private final File mBackupName;
    private final AtomicFileLock mLock;

    private long mTimestamp = 0;

    public AtomicRandomAccessFile(File baseName) {
        this.mDir = baseName.getParentFile().getAbsoluteFile();
        this.mBaseName = baseName;
        this.mBackupName = new File(baseName.getPath() + ".bak");
        this.mLock = AtomicFileLock.obtain(new File(baseName.getPath() + ".lock"));
        updateTimestamp();

    }

    public boolean delete() {
        final IKey key = lock();
        if (key == null) {
            return false;
        }
        boolean result;
        try {
            result = deleteQuietly(mBaseName);
            result = deleteQuietly(mBackupName) && result;
            return result;
        } finally {
            key.unlock();
        }
    }

    public RandomAccessFile startWrite() throws IOException {
        checkDir();
        backup();

        RandomAccessFile file;

        try {
            file = new RandomAccessFile(this.mBaseName, MODE_READWRITE);
        } catch (FileNotFoundException var6) {
            File parent = this.mBaseName.getParentFile();
            if (!parent.mkdir()) {
                throw new IOException("Couldn\'t create directory " + this.mBaseName);
            }

            boolean createResult = false;
            try {
                createResult = this.mBaseName.createNewFile();
            } catch (IOException ex) {
            }

            if (!createResult) {
                throw new IOException("Couldn\'t create directory " + this.mBaseName);
            }

            try {
                file = new RandomAccessFile(this.mBaseName, MODE_READWRITE);
            } catch (FileNotFoundException var5) {
                throw new IOException("Couldn\'t create " + this.mBaseName);
            }
        }

        return file;
    }

    public void finishWrite(RandomAccessFile file) {
        if (file != null) {
            sync(file);
            try {
                file.close();
                deleteQuietly(this.mBackupName);
                updateTimestamp();
            } catch (IOException ignored) {
            }
        }

    }

    public void failWrite(RandomAccessFile file) {
        if (file != null) {
            sync(file);

            try {
                file.close();
                recoveryBackup();
            } catch (IOException ignored) {
            }
        }
    }

    @Nullable
    public RandomAccessFile openReadChecked() {
        try {
            recoveryBackupIfExists();
        } catch (IOException ignored) {
        }
        if (this.mBaseName.exists()) {
            try {
                return new RandomAccessFile(this.mBaseName, MODE_READONLY);
            } catch (FileNotFoundException ignored) {
            }
        }
        return null;
    }

    public RandomAccessFile openRead() throws FileNotFoundException {
        try {
            recoveryBackupIfExists();
        } catch (IOException ignored) {
        }
        return new RandomAccessFile(this.mBaseName, MODE_READONLY);
    }

    public void closeRead(@Nullable RandomAccessFile in) {
        Utils.close(in);
    }

    public boolean hasChanged() {
        // 只是读取文件信息， 更新的是内存里的信息， 所以不需要加进程锁
        final IKey key = mLock.lock(LOCK_LEVEL_THREAD);
        try {
            final File file = mBaseName;
            return file == null || !file.exists() || mTimestamp != file.lastModified();
        } finally {
            key.unlock();
        }
    }

    @Override
    public IKey lock() {
        return mLock.lock(LOCK_LEVEL_PROCESS);
    }

    private static boolean sync(RandomAccessFile file) {
        try {
            if (file != null) {
                file.getFD().sync();
            }
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void updateTimestamp() {
        // 只是读取文件信息， 更新的是内存里的信息， 所以不需要加进程锁
        final IKey key = mLock.lock(LOCK_LEVEL_THREAD);
        try {
            final File file = mBaseName;
            if (file != null && file.exists()) {
                mTimestamp = file.lastModified();
            }
        } finally {
            key.unlock();
        }
    }

    private void checkDir() throws IOException {
        if (!Utils.checkDir(this.mDir)) {
            throw new IOException("Check config dir fail: " + this.mDir);
        }
    }

    private void recoveryBackup() throws IOException {
        Utils.deleteQuietly(this.mBaseName);
        Utils.moveFile(this.mBackupName, this.mBaseName);
    }

    private void recoveryBackupIfExists() throws IOException {
        if (mBackupName.exists()) {
            recoveryBackup();
        }
    }

    private void backup() {
        if (this.mBaseName.exists()) {
            if (!this.mBackupName.exists()) {
                try {
                    Utils.moveFile(this.mBaseName, this.mBackupName);
                } catch (IOException ignored) {
                }
            } else {
                deleteQuietly(this.mBaseName);
            }
        }
    }
}
