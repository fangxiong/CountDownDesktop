package com.fax.lib.config.api.puttable;

public interface ITypedKeyPuttable<KT> {

    /**
     * base values
     */
    void putString(KT key, String value);


    void putInt(KT key, int value);


    void putLong(KT key, long value);


    void putFloat(KT key, float value);


    void putDouble(KT key, double value);


    void putBool(KT key, boolean value);


    /**
     * array values
     */
    void putStringArray(KT key, String[] value);


    void putIntArray(KT key, int[] value);


    void putLongArray(KT key, long[] value);


    void putFloatArray(KT key, float[] value);


    void putDoubleArray(KT key, double[] value);


    void putBoolArray(KT key, boolean[] value);

}
