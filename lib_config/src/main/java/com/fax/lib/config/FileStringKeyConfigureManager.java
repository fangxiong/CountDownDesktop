package com.fax.lib.config;

import android.content.Context;
import android.util.ArrayMap;

import com.fax.lib.config.storage.IStringKeyStorage;
import com.fax.lib.config.storage.StorageFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fax.lib.config.utils.Utils.*;

public final class FileStringKeyConfigureManager {

    private static final List<String> EMPTY_NAMESPACES = new ArrayList<>();
    private final Map<String, FileStringKeyConfigure<String>> sSharedPrefs = new ArrayMap<>();

    private final byte[] mSync = new byte[0];
    private final File mPreferencesDir;

    public FileStringKeyConfigureManager(Context context, String dirName) {
        this(context.getDir(dirName, Context.MODE_PRIVATE));
    }

    public FileStringKeyConfigureManager(File dir) {
        mPreferencesDir = dir;
    }

    public final FileStringKeyConfigure<String> getConfigure(String namespace) {
        FileStringKeyConfigure<String> sp;
        IStringKeyStorage storage = null;
        synchronized (mSync) {
            sp = get(namespace);
            if (sp != null && !(storage = sp.getStorage()).hasStorageChanged()) {
                return sp;
            }
        }

        if (sp == null) {
            storage = newStorage(namespace);
        }

        synchronized (mSync) {
            if (sp != null) {
                sp.reload();
            } else {
                sp = get(namespace);
                if (sp == null) {
                    sp = new FileStringKeyConfigure<>(storage, namespace);
                    put(namespace, sp);
                }
            }
            return sp;
        }
    }

    public final boolean deleteConfigure(FileStringKeyConfigure<String> configure) {
        final IStringKeyStorage storage = configure.getStorage();
        final String namespace = configure.getNamespace();
        synchronized (mSync) {
            remove(namespace);
        }
        if (storage == null) {
            return true;
        }
        try {
            storage.delete();
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    File makeFilename(File base, String name) {
        return new File(base, encodeString(name));
    }

    File getConfigureFile(String namespace) {
        return makeFilename(mPreferencesDir, namespace);
    }

    public final List<String> getAllNamespaces() {
        List<String> namespaces = null;
        if (isDirectory(mPreferencesDir)) {
            String[] names = mPreferencesDir.list();
            if (names != null) {
                namespaces = new ArrayList<>(names.length);
                for (String name : names) {
                    name = decodeString(name);
                    if (name != null) {
                        namespaces.add(name);
                    }
                }
            }
        }
        return namespaces == null ? EMPTY_NAMESPACES : namespaces;
    }

    FileStringKeyConfigure<String> get(String namespace) {
        return sSharedPrefs.get(namespace);
    }

    void put(String namespace, FileStringKeyConfigure<String> configure) {
        sSharedPrefs.put(namespace, configure);
    }

    void remove(String namespace) {
        sSharedPrefs.remove(namespace);
    }

    private IStringKeyStorage newStorage(String namespace) {
        return StorageFactory.newStringKeyStorage(getConfigureFile(namespace));
    }


}
