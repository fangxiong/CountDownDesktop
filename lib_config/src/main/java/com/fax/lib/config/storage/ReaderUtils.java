package com.fax.lib.config.storage;


import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.io.IOException;

class ReaderUtils {

    static Object readValue(ISimpleValueReader reader) throws IOException {
        final byte typeCode = reader.readTypeCode();

        if (isTypeCodeNullValue(typeCode)) {
            return null;
        }

        final Object value;
        switch (typeCode) {
            case StorageConstant.TYPE_SPARSE_ARRAY: {
                final int length = reader.readLength();
                final SparseArray<Object> array = new SparseArray<>(length);
                for (int i = 0; i < length; i++) {
                    Integer key = reader.readInteger();
                    if (key != null) {
                        array.put(key, readValue(reader));
                    }
                }
                value = array;
                break;
            }
            case StorageConstant.TYPE_LONG_SPARSE_ARRAY: {
                final int length = reader.readLength();
                LongSparseArray<Object> array = new LongSparseArray<>(length);
                for (int i = 0; i < length; i++) {
                    Long key = reader.readLong();
                    if (key != null) {
                        array.put(key, readValue(reader));
                    }
                }
                value = array;
                break;
            }
            case StorageConstant.TYPE_ARRAY_MAP: {
                final int length = reader.readLength();
                ArrayMap<String, Object> map = new ArrayMap<>(length);
                for (int i = 0; i < length; i++) {
                    String key = reader.readString();
                    if (key != null) {
                        map.put(key, readValue(reader));
                    }
                }
                value = map;
                break;
            }
            case StorageConstant.TYPE_ARRAY: {
                value = readArray(reader);
                break;
            }
            default: {
                value = readSimpleValue(reader, typeCode);
            }
        }
        return value;
    }

    private static Object readArray(ISimpleValueReader reader) throws IOException {
        final byte itemTypeCode = reader.readTypeCode();
        final int length = reader.readLength();

        if (isTypeCodeNullValue(itemTypeCode) || length == StorageConstant.NULL_LENGTH) {
            return null;
        }

        Object result = null;

        switch (itemTypeCode) {
            case StorageConstant.TYPE_STRING: {
                String[] array = new String[length];
                for (int i = 0; i < length; i++) {
                    array[i] = reader.readString();
                }
                result = array;
                break;
            }
            case StorageConstant.TYPE_INTEGER: {
                int[] array = new int[length];
                Integer value;
                for (int i = 0; i < length; i++) {
                    value = reader.readInteger();
                    if (value == null) {
                        array[i] = 0;
                    } else {
                        array[i] = value;
                    }
                }
                result = array;
                break;
            }
            case StorageConstant.TYPE_LONG: {
                long[] array = new long[length];
                Long value;
                for (int i = 0; i < length; i++) {
                    value = reader.readLong();
                    if (value == null) {
                        array[i] = 0;
                    } else {
                        array[i] = value;
                    }
                }
                result = array;
                break;
            }
            case StorageConstant.TYPE_FLOAT: {
                float[] array = new float[length];
                Float value;
                for (int i = 0; i < length; i++) {
                    value = reader.readFloat();
                    if (value == null) {
                        array[i] = 0;
                    } else {
                        array[i] = value;
                    }
                }
                result = array;
                break;
            }
            case StorageConstant.TYPE_DOUBLE: {
                double[] array = new double[length];
                Double value;
                for (int i = 0; i < length; i++) {
                    value = reader.readDouble();
                    if (value == null) {
                        array[i] = 0;
                    } else {
                        array[i] = value;
                    }
                }
                result = array;
                break;
            }
            case StorageConstant.TYPE_BOOLEAN: {
                boolean[] array = new boolean[length];
                Boolean value;
                for (int i = 0; i < length; i++) {
                    value = reader.readBoolean();
                    if (value == null) {
                        array[i] = false;
                    } else {
                        array[i] = value;
                    }
                }
                result = array;
                break;
            }
        }

        return result;
    }

    private static Object readSimpleValue(ISimpleValueReader reader, byte typeCode) throws IOException {
        Object value = null;
        switch (typeCode) {
            case StorageConstant.TYPE_STRING: {
                value = reader.readString();
                break;
            }
            case StorageConstant.TYPE_INTEGER: {
                value = reader.readInteger();
                break;
            }
            case StorageConstant.TYPE_LONG: {
                value = reader.readLong();
                break;
            }
            case StorageConstant.TYPE_FLOAT: {
                value = reader.readFloat();
                break;
            }
            case StorageConstant.TYPE_DOUBLE: {
                value = reader.readDouble();
                break;
            }
            case StorageConstant.TYPE_BOOLEAN: {
                value = reader.readBoolean();
                break;
            }
        }
        return value;
    }

    private static boolean isTypeCodeNullValue(byte typeCode) {
        return typeCode == StorageConstant.TYPE_UNKNOWN || typeCode == StorageConstant.TYPE_NULL;
    }

}
