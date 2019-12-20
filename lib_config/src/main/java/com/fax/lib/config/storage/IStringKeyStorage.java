package com.fax.lib.config.storage;


import com.fax.lib.config.api.gettable.IAllKeysGettable;

public interface IStringKeyStorage extends StorageConstant, IAllKeysGettable<String[]> {

    Object read(String key);

    void write(String key, Object value);

    boolean delete(String key);

    boolean hasStorageChanged(String key);

    boolean delete();

    boolean hasStorageChanged();
}
