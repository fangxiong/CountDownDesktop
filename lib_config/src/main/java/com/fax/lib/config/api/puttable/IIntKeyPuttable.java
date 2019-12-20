package com.fax.lib.config.api.puttable;

public interface IIntKeyPuttable {

    /**
     * base values
     */
    void putString(int key, String value);


    void putInt(int key, int value);


    void putLong(int key, long value);


    void putFloat(int key, float value);


    void putDouble(int key, double value);


    void putBool(int key, boolean value);


    void putStringArray(int key, String[] value);

    /**
     * array values
     */

    void putIntArray(int key, int[] value);


    void putLongArray(int key, long[] value);


    void putFloatArray(int key, float[] value);


    void putDoubleArray(int key, double[] value);


    void putBoolArray(int key, boolean[] value);

}
