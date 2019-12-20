package com.fax.lib.config.api.puttable;

public interface ILongKeyPuttable {

    /**
     * base values
     */
    void putString(long key, String value);


    void putInt(long key, int value);


    void putLong(long key, long value);


    void putFloat(long key, float value);


    void putDouble(long key, double value);


    void putBool(long key, boolean value);


    /**
     * array values
     */
    void putStringArray(long key, String[] value);


    void putIntArray(long key, int[] value);


    void putLongArray(long key, long[] value);


    void putFloatArray(long key, float[] value);


    void putDoubleArray(long key, double[] value);


    void putBoolArray(long key, boolean[] value);

}
