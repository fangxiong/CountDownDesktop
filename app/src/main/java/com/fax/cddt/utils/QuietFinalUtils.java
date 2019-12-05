package com.fax.cddt.utils;


import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.Lock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class QuietFinalUtils {

    public static void close(final Object... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Object object : closeables) {
            close(object);
        }
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

    public static void recycle(final Object... recyclables) {
        if (recyclables == null) {
            return;
        }
        for (final Object object : recyclables) {
            recycle(object);
        }
    }

    public static void recycle(@Nullable final Object recyclable) {
        if (recyclable == null) {
            return;
        }

        if (handleRecycle(recyclable)) {
            return;
        }

        invokeFinal(recyclable, "recycle");
    }

    public static void flush(final Object... flushables) {
        if (flushables == null) {
            return;
        }
        for (final Object object : flushables) {
            flush(object);
        }
    }


    public static void flush(@Nullable final Object flushable) {
        if (flushable == null) {
            return;
        }

        if (handleFlush(flushable)) {
            return;
        }

        invokeFinal(flushable, "flush");
    }


    public static void unlock(final Object... locks) {
        if (locks == null) {
            return;
        }
        for (final Object object : locks) {
            unlock(object);
        }
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

    private static boolean handleClose(@NonNull final Object closeable) {
        boolean handle = false;
        try {
            if (closeable instanceof XmlResourceParser) {
                handle = true;
                ((XmlResourceParser) closeable).close();
            } else if (closeable instanceof DatagramSocket) {
                handle = true;
                ((DatagramSocket) closeable).close();
            } else if (closeable instanceof Response) {
                handle = true;
                ((Response) closeable).close();
            } else if (closeable instanceof ResponseBody) {
                handle = true;
                ((ResponseBody) closeable).close();
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

    private static boolean handleRecycle(@NonNull final Object recyclable) {
        boolean handle = false;
        try {
            if (recyclable instanceof AccessibilityNodeInfoCompat) {
                handle = true;
                ((AccessibilityNodeInfoCompat) recyclable).recycle();
            } else if (recyclable instanceof AccessibilityNodeInfo) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    handle = true;
                    ((AccessibilityNodeInfo) recyclable).recycle();
                }
            } else if (recyclable instanceof AccessibilityEvent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    handle = true;
                    ((AccessibilityEvent) recyclable).recycle();
                }
            } else if (recyclable instanceof TypedArray) {
                handle = true;
                ((TypedArray) recyclable).recycle();
            } else if (recyclable instanceof Bitmap) {
                handle = true;
                final Bitmap bitmap = (Bitmap) recyclable;
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } else if (recyclable instanceof Recyclable) {
                handle = true;
                final Recyclable r = (Recyclable) recyclable;
                if (!r.isRecycled()) {
                    r.recycle();
                }
            }
        } catch (Exception ignored) {
        }
        return handle;
    }

    private static boolean handleFlush(@NonNull Object flushable) {
        boolean handle = false;
        try {
            if (flushable instanceof OutputStream) {
                handle = true;
                ((OutputStream) flushable).flush();
            } else if (flushable instanceof Writer) {
                handle = true;
                ((Writer) flushable).flush();
            } else if (flushable instanceof Flushable) {
                handle = true;
                ((Flushable) flushable).flush();
            }
        } catch (Exception ignored) {
        }
        return handle;
    }

    private static void invokeFinal(Object object, String name) {
        if (object == null) {
            return;
        }
        final Method method = ReflectUtils.getMethodDeep(object.getClass(), name);

        if (method == null || !method.isAccessible()) {
            return;
        }

        try {
            method.invoke(object);
        } catch (Exception ignored) {
        }
    }
}
