package com.fax.lib.config.utils;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.ReentrantLock;

import static com.fax.lib.config.utils.Utils.checkDir;

public final class AtomicFileLock implements ILevelLock {

    private static final ArrayMap<String, AtomicFileLock> POOL = new ArrayMap<>();

    public static final int LOCK_LEVEL_THREAD = 100;
    public static final int LOCK_LEVEL_PROCESS = 200;

    private static final String TAG = "test_atomic_file";

    private final ReentrantLock mThreadLock = new ReentrantLock();
    private final IKey mThreadKey = new ThreadKey();

    private FileLock mProcessLock = null;
    private RandomAccessFile mOpenedProcessLockFile = null;
    private File mProcessLockFile;
    private final IKey mProcessKey = new ProcessKey();

    public synchronized static AtomicFileLock obtain(File lockFile) {
        if (lockFile == null) {
            throw new NullPointerException("lock file can not be null");
        }
        final String path = lockFile.getAbsolutePath();
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("lock path is empty");
        }
        AtomicFileLock fileLock = POOL.get(path);
        if (fileLock == null) {
            POOL.put(path, fileLock = new AtomicFileLock(lockFile));
        }
        return fileLock;
    }

    private AtomicFileLock(@NonNull File lockFile) {
        mProcessLockFile = lockFile;
    }

    private IKey lockThreadLevel() {
        mThreadLock.lock();
        return mThreadKey;
    }

    private IKey lockProcessLevel() {
        mThreadLock.lock();
        try {
            if (!checkDir(this.mProcessLockFile.getParentFile())) {
                throw new IOException("Check config dir fail: " + this.mProcessLockFile.getParentFile());
            }
            if (!mProcessLockFile.exists() && !mProcessLockFile.createNewFile()) {
                throw new IOException("Create lock file fail");
            }
            mOpenedProcessLockFile = new RandomAccessFile(mProcessLockFile, "rw");
            mProcessLock = mOpenedProcessLockFile.getChannel().lock();
            return mProcessKey;
        } catch (Throwable ignored) {
        }
        mProcessKey.unlock();
        return null;
    }

    @Override
    public final IKey lock(int level) {
        switch (level) {
            case LOCK_LEVEL_THREAD: {
                return lockThreadLevel();
            }
            case LOCK_LEVEL_PROCESS: {
                return lockProcessLevel();
            }
        }
        throw new RuntimeException("Unsupported lock level: " + level);
    }

    private class ThreadKey implements IKey {

        @Override
        public void unlock() {
            mThreadLock.unlock();
        }
    }

    private final class ProcessKey extends ThreadKey {

        @Override
        public final void unlock() {
            try {
                Utils.unlock(mProcessLock);
                Utils.close(mOpenedProcessLockFile);
                mProcessLock = null;
                mOpenedProcessLockFile = null;
            } finally {
                super.unlock();
            }
        }
    }

}
