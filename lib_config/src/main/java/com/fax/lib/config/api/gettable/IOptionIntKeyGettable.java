package com.fax.lib.config.api.gettable;

public interface IOptionIntKeyGettable extends IAllKeysGettable<int[]> {

    /**
     * base values
     */
    void putString(int key, String value);

    String getString(int key, String defaultValue);


    int getInt(int key, int defaultValue);


    long getLong(int key, long defaultValue);


    float getFloat(int key, float defaultValue);


    double getDouble(int key, double defaultValue);


    boolean getBool(int key, boolean defaultValue);


    /**
     * array values
     */
    String[] getStringArray(int key, String[] defaultValue);


    int[] getIntArray(int key, int[] defaultValue);


    long[] getLongArray(int key, long[] defaultValue);


    float[] getFloatArray(int key, float[] defaultValue);


    double[] getDoubleArray(int key, double[] defaultValue);


    boolean[] getBoolArray(int key, boolean[] defaultValue);
}
