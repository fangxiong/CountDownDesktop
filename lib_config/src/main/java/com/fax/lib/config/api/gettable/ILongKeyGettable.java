package com.fax.lib.config.api.gettable;


public interface ILongKeyGettable extends IAllKeysGettable<long[]> {
    /**
     * base values
     */
    String getString(long key);

    int getInt(long key);

    long getLong(long key);

    float getFloat(long key);

    double getDouble(long key);

    boolean getBool(long key);

    /**
     * array values
     */
    String[] getStringArray(long key);

    int[] getIntArray(long key);

    long[] getLongArray(long key);

    float[] getFloatArray(long key);

    double[] getDoubleArray(long key);

    boolean[] getBoolArray(long key);

}
