package com.fax.lib.config.storage;

import java.io.File;

public final class StorageFactory {

    public static IStringKeyStorage newStringKeyStorage(File dir) {
        return new StringKeyRandomFileStorage(dir);
    }

}
