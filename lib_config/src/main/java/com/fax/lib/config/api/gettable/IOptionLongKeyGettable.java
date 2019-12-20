package com.fax.lib.config.api.gettable;

public interface IOptionLongKeyGettable extends IAllKeysGettable<long[]> {

    /**
     * base values
     */
    void putString(long key, String value);

    String getString(long key, String defaultValue);


    int getInt(long key, int defaultValue);


    long getLong(long key, long defaultValue);


    float getFloat(long key, float defaultValue);


    double getDouble(long key, double defaultValue);


    boolean getBool(long key, boolean defaultValue);


    /**
     * array values
     */
    String[] getStringArray(long key, String[] defaultValue);


    int[] getIntArray(long key, int[] defaultValue);


    long[] getLongArray(long key, long[] defaultValue);


    float[] getFloatArray(long key, float[] defaultValue);


    double[] getDoubleArray(long key, double[] defaultValue);


    boolean[] getBoolArray(long key, boolean[] defaultValue);
}
