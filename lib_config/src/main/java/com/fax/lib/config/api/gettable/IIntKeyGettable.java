package com.fax.lib.config.api.gettable;

public interface IIntKeyGettable extends IAllKeysGettable<int[]> {

    /**
     * base values
     */
    String getString(int key);

    int getInt(int key);

    long getLong(int key);

    float getFloat(int key);

    double getDouble(int key);

    boolean getBool(int key);

    /**
     * array values
     */
    String[] getStringArray(int key);

    int[] getIntArray(int key);

    long[] getLongArray(int key);

    float[] getFloatArray(int key);

    double[] getDoubleArray(int key);

    boolean[] getBoolArray(int key);

}
