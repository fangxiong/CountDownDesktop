package com.fax.lib.config.api.gettable;

public interface ITypedKeyGettable<KT> extends IAllKeysGettable<KT[]> {

    /**
     * base values
     */
    String getString(KT key);

    int getInt(KT key);

    long getLong(KT key);

    float getFloat(KT key);

    double getDouble(KT key);

    boolean getBool(KT key);

    /**
     * array values
     */
    String[] getStringArray(KT key);

    int[] getIntArray(KT key);

    long[] getLongArray(KT key);

    float[] getFloatArray(KT key);

    double[] getDoubleArray(KT key);

    boolean[] getBoolArray(KT key);

}
