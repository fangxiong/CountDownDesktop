package com.fax.lib.config;


import com.fax.lib.config.api.configure.ITypedKeyConfigure;

public abstract class MapLikeStringKeyConfigure extends AbsStringKeyBroadConfigure implements ITypedKeyConfigure<String> {

    private final AbsStringKeyBroadConfigure mBasicConfigure = new CheckStringKeyBroadConfigure(this);

    @Override
    public final void putString(String key, String value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final String getString(String key, String defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (String) v : defaultValue;
    }

    @Override
    public final void putInt(String key, int value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final int getInt(String key, int defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (int) v : defaultValue;
    }

    @Override
    public final void putLong(String key, long value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final long getLong(String key, long defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (long) v : defaultValue;
    }

    @Override
    public final void putFloat(String key, float value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final float getFloat(String key, float defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (float) v : defaultValue;
    }

    @Override
    public final void putDouble(String key, double value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final double getDouble(String key, double defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (double) v : defaultValue;
    }

    @Override
    public final void putBool(String key, boolean value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final boolean getBool(String key, boolean defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (boolean) v : defaultValue;
    }

    @Override
    public final void putStringArray(String key, String[] value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final String[] getStringArray(String key, String[] defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (String[]) v : defaultValue;
    }

    @Override
    public final void putIntArray(String key, int[] value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final int[] getIntArray(String key, int[] defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (int[]) v : defaultValue;
    }

    @Override
    public final void putLongArray(String key, long[] value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final long[] getLongArray(String key, long[] defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (long[]) v : defaultValue;
    }

    @Override
    public final void putFloatArray(String key, float[] value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final float[] getFloatArray(String key, float[] defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (float[]) v : defaultValue;
    }

    @Override
    public final void putDoubleArray(String key, double[] value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final double[] getDoubleArray(String key, double[] defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (double[]) v : defaultValue;
    }

    @Override
    public final void putBoolArray(String key, boolean[] value) {
        mBasicConfigure.put(key, value);
    }

    @Override
    public final boolean[] getBoolArray(String key, boolean[] defaultValue) {
        Object v = mBasicConfigure.get(key);
        return v != null ? (boolean[]) v : defaultValue;
    }


}
