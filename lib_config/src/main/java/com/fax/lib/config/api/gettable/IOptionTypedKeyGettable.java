package com.fax.lib.config.api.gettable;

public interface IOptionTypedKeyGettable<KT> extends IAllKeysGettable<KT[]> {

    /**
     * base values
     */
    void putString(KT key, String value);

    String getString(KT key, String defaultValue);


    int getInt(KT key, int defaultValue);


    long getLong(KT key, long defaultValue);


    float getFloat(KT key, float defaultValue);


    double getDouble(KT key, double defaultValue);


    boolean getBool(KT key, boolean defaultValue);


    /**
     * array values
     */
    String[] getStringArray(KT key, String[] defaultValue);


    int[] getIntArray(KT key, int[] defaultValue);


    long[] getLongArray(KT key, long[] defaultValue);


    float[] getFloatArray(KT key, float[] defaultValue);


    double[] getDoubleArray(KT key, double[] defaultValue);


    boolean[] getBoolArray(KT key, boolean[] defaultValue);
}
