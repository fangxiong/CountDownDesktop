package com.fax.lib.config.utils;

import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.*;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;

public class Utils {

    public static final String MODE_READWRITE = "rw";
    public static final String MODE_READONLY = "r";

    private static final Base32 BASE32 = new Base32();

    public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

    private static final long FILE_COPY_BUFFER_SIZE = 1024 * 1024 * 30;

    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    public static boolean exists(String filePath) {
        return exists(safeNewFile(filePath));
    }

    @Nullable
    private static File safeNewFile(@Nullable String path) {
        return path == null ? null : new File(path);
    }

    public static boolean isFile(File file) {
        return exists(file) && file.isFile();
    }

    public static boolean isReadableFile(String path) {
        return isReadableFile(new File(path));
    }

    public static boolean isReadableFile(File file) {
        return exists(file) && file.isFile() && file.canRead();
    }

    public static boolean isDirectory(File file) {
        return exists(file) && file.isDirectory();
    }

    public static String encodeString(String value) {
        value = toLowerCase(BASE32.encodeToString(getBytes(value)));
        if (value != null) {
            value = value.replace("=", "");
        }
        return value;
    }

    public static String decodeString(String value) {
        return newString(BASE32.decode(toUpperCase(value)));
    }

    public static String toLowerCase(String value) {
        return value == null ? null : value.toLowerCase();
    }

    public static String toUpperCase(String value) {
        return value == null ? null : value.toUpperCase();
    }

    public static void unlock(@Nullable final Object lock) {
        if (lock == null) {
            return;
        }

        if (handleUnlock(lock)) {
            return;
        }

        invokeFinal(lock, "unlock");
    }

    public static void close(@Nullable final Object closeable) {
        if (closeable == null) {
            return;
        }

        if (handleClose(closeable)) {
            return;
        }

        invokeFinal(closeable, "close");
    }


    private static boolean handleClose(@NonNull final Object closeable) {
        boolean handle = false;
        try {
            if (closeable instanceof XmlResourceParser) {
                handle = true;
                ((XmlResourceParser) closeable).close();
            } else if (closeable instanceof DatagramSocket) {
                handle = true;
                ((DatagramSocket) closeable).close();
            } else if (closeable instanceof FileOutputStream) {
                handle = true;
                final FileOutputStream fos = (FileOutputStream) closeable;
                try {
                    fos.getFD().sync();
                } catch (Exception ignored) {
                }
                fos.close();
            } else if (closeable instanceof OutputStream) {
                handle = true;
                ((OutputStream) closeable).close();
            } else if (closeable instanceof RandomAccessFile) {
                ((RandomAccessFile) closeable).close();
            } else if (closeable instanceof Cursor) {
                handle = true;
                ((Cursor) closeable).close();
            } else if (closeable instanceof AssetFileDescriptor) {
                handle = true;
                ((AssetFileDescriptor) closeable).close();
            } else if (closeable instanceof ParcelFileDescriptor) {
                handle = true;
                ((ParcelFileDescriptor) closeable).close();
            } else if (closeable instanceof InputStream) {
                handle = true;
                ((InputStream) closeable).close();
            } else if (closeable instanceof Closeable) {
                handle = true;
                ((Closeable) closeable).close();
            } else if (closeable instanceof AutoCloseable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    handle = true;
                    ((AutoCloseable) closeable).close();
                }
            }
        } catch (Exception ignored) {
        }
        return handle;
    }

    private static boolean handleUnlock(@NonNull final Object lock) {
        boolean handle = false;
        try {
            if (lock instanceof FileLock) {
                handle = true;
                ((FileLock) lock).release();
            } else if (lock instanceof IKey) {
                handle = true;
                ((IKey) lock).unlock();
            } else if (lock instanceof Lock) {
                handle = true;
                ((Lock) lock).unlock();
            }
        } catch (Exception ignored) {
        }
        return handle;
    }

    private static void invokeFinal(Object object, String name) {
        if (object == null) {
            return;
        }
        final Method method = getMethod(object.getClass(), name);

        if (method == null || !method.isAccessible()) {
            return;
        }

        try {
            method.invoke(object);
        } catch (Exception ignored) {
        }
    }

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... classes) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            if ((clazz = clazz.getSuperclass()) != null) {
                method = getMethod(clazz, methodName, classes);
            }
        }
        return method;
    }

    public static boolean checkDir(File dir) {
        if (dir == null) {
            return false;
        }
        boolean result;
        if (!dir.exists()) {
            result = dir.mkdirs();
        } else {
            result = dir.isDirectory() || (deleteQuietly(dir) && dir.mkdirs());
        }
        return result;
    }


    public static boolean deleteQuietly(final File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (final Exception ignored) {
        }

        try {
            return file.delete();
        } catch (final Exception ignored) {
            return false;
        }
    }

    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    public static boolean isSymlink(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }

        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            final File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile()) || isBrokenSymlink(file);
    }

    private static boolean isBrokenSymlink(final File file) throws IOException {
        // if file exists then if it is a symlink it's not broken
        if (file.exists()) {
            return false;
        }
        // a broken symlink will show up in the list of files of its parent directory
        final File canon = file.getCanonicalFile();
        File parentDir = canon.getParentFile();
        if (parentDir == null || !parentDir.exists()) {
            return false;
        }

        // is it worthwhile to create a FileFilterUtil method for this?
        // is it worthwhile to create an "identity"  IOFileFilter for this?
        File[] fileInDir = parentDir.listFiles(
                new FileFilter() {
                    public boolean accept(File aFile) {
                        return aFile.equals(canon);
                    }
                }
        );
        return fileInDir != null && fileInDir.length > 0;
    }

    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }

        if (!directory.delete()) {
            final String message =
                    "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }


    public static void moveFile(final File srcFile, final File destFile) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.exists()) {
            throw new IOException("Destination '" + destFile + "' already exists");
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }
        final boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile);
            if (!srcFile.delete()) {
                deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile +
                        "' after copy to '" + destFile + "'");
            }
        }
    }

    public static void copyFile(final File srcFile, final File destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }

    public static void copyFile(final File srcFile, final File destFile,
                                final boolean preserveFileDate) throws IOException {
        checkFileRequirements(srcFile, destFile);
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        final File parentFile = destFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new IOException("Destination '" + parentFile + "' directory cannot be created");
            }
        }
        if (destFile.exists() && destFile.canWrite() == false) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }

    private static void doCopyFile(final File srcFile, final File destFile, final boolean preserveFileDate)
            throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            final long size = input.size(); // TODO See IO-386
            long pos = 0;
            long count = 0;
            while (pos < size) {
                final long remain = size - pos;
                count = remain > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : remain;
                final long bytesCopied = output.transferFrom(input, pos, count);
                if (bytesCopied == 0) { // IO-385 - can happen if file is truncated after caching the size
                    break; // ensure we don't loop forever
                }
                pos += bytesCopied;
            }
        } finally {
            close(output, fos, input, fis);
        }

        final long srcLen = srcFile.length(); // TODO See IO-386
        final long dstLen = destFile.length(); // TODO See IO-386
        if (srcLen != dstLen) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static void close(final Object... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Object object : closeables) {
            close(object);
        }
    }

    public static String newString(final byte[] bytes) {
        return newString(bytes, DEFAULT_CHARSET);
    }

    public static byte[] getBytes(String string) {
        return getBytes(string, DEFAULT_CHARSET);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static byte[] getBytes(final String string, final Charset charset) {
        if (string == null) {
            return null;
        }
        return string.getBytes(charset);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static String newString(final byte[] bytes, final Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }

    private static void checkFileRequirements(File src, File dest) throws FileNotFoundException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (dest == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!src.exists()) {
            throw new FileNotFoundException("Source '" + src + "' does not exist");
        }
    }

    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    private static File[] verifiedListFiles(File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }
}
