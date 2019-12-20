package com.fax.lib.config.storage;

import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

class WriterUtils {

    static void writeValue(ISimpleValueWriter writer, Object value) throws IOException {
        if (value == null) {
            writer.writeTypeCode(StorageConstant.TYPE_NULL);
        } else if (value instanceof SparseArray) {
            writeSparseArrayValue(writer, (SparseArray) value);
        } else if (value instanceof LongSparseArray) {
            writeLongSparseArrayValue(writer, (LongSparseArray) value);
        } else if (value instanceof ArrayMap) {
            writeArrayMap(writer, (ArrayMap) value);
        } else if (value.getClass().isArray()) {
            writeArray(writer, value);
        } else {
            writeSimpleValue(writer, value);
        }
    }

    private static void writeSparseArrayValue(ISimpleValueWriter writer, SparseArray value) throws IOException {
        final int size = value.size();
        int key;
        writer.writeTypeCode(StorageConstant.TYPE_SPARSE_ARRAY);
        writer.writeLength(size);

        for (int i = 0; i < size; i++) {
            key = value.keyAt(i);
            writer.writeInteger(key);
            writeValue(writer, value.valueAt(i));
        }
    }

    private static void writeLongSparseArrayValue(ISimpleValueWriter writer, LongSparseArray value) throws IOException {
        final int size = value.size();
        long key;
        writer.writeTypeCode(StorageConstant.TYPE_LONG_SPARSE_ARRAY);
        writer.writeLength(size);

        for (int i = 0; i < size; i++) {
            key = value.keyAt(i);
            writer.writeLong(key);
            writeValue(writer, value.valueAt(i));
        }
    }

    private static void writeArrayMap(ISimpleValueWriter writer, ArrayMap value) throws IOException {
        final int size = value.size();
        writer.writeTypeCode(StorageConstant.TYPE_ARRAY_MAP);
        writer.writeLength(size);
        final Set<Map.Entry> entries = value.entrySet();
        for (Map.Entry entry : entries) {
            writer.writeString(entry.getKey().toString());
            writeValue(writer, entry.getValue());
        }
    }

    private static void writeArray(ISimpleValueWriter writer, Object value) throws IOException {
        final int result = getArrayElementType(value);
        final byte typeCode = (byte) (StorageConstant.MASK_TYPE_CODE & result);
        final boolean isObject = (result & StorageConstant.FLAG_IS_OBJECT) == StorageConstant.FLAG_IS_OBJECT;


        if (typeCode == StorageConstant.TYPE_UNKNOWN || typeCode == StorageConstant.TYPE_NULL) {
            writer.writeTypeCode(typeCode);
            writer.writeLength(StorageConstant.NULL_LENGTH);
            return;
        }

        writer.writeTypeCode(StorageConstant.TYPE_ARRAY);
        writer.writeTypeCode(typeCode);

        if (isObject) {
            final Object[] array = (Object[]) value;
            writer.writeLength(array.length);
            for (Object item : array) {
                writeSimpleValue(writer, typeCode, item);
            }
        } else {
            switch (typeCode) {
                case StorageConstant.TYPE_INTEGER: {
                    final int[] array = (int[]) value;
                    writer.writeLength(array.length);
                    for (int item : array) {
                        writeSimpleValue(writer, typeCode, item);
                    }
                    break;
                }
                case StorageConstant.TYPE_LONG: {
                    final long[] array = (long[]) value;
                    writer.writeLength(array.length);
                    for (long item : array) {
                        writeSimpleValue(writer, typeCode, item);
                    }
                    break;
                }
                case StorageConstant.TYPE_FLOAT: {
                    final float[] array = (float[]) value;
                    writer.writeLength(array.length);
                    for (float item : array) {
                        writeSimpleValue(writer, typeCode, item);
                    }
                    break;
                }
                case StorageConstant.TYPE_DOUBLE: {
                    final double[] array = (double[]) value;
                    writer.writeLength(array.length);
                    for (double item : array) {
                        writeSimpleValue(writer, typeCode, item);
                    }
                    break;
                }
                case StorageConstant.TYPE_BOOLEAN: {
                    final boolean[] array = (boolean[]) value;
                    writer.writeLength(array.length);
                    for (boolean item : array) {
                        writeSimpleValue(writer, item);
                    }
                    break;
                }
            }
        }
    }

    private static void writeSimpleValue(ISimpleValueWriter writer, Object value) throws IOException {
        final byte code = getValueTypeCode(value);
        writer.writeTypeCode(code);
        writeSimpleValue(writer, code, value);
    }

    private static void writeSimpleValue(ISimpleValueWriter writer, byte typeCode, Object value) throws IOException {
        switch (typeCode) {
            case StorageConstant.TYPE_STRING: {
                writer.writeString((String) value);
                break;
            }
            case StorageConstant.TYPE_INTEGER: {
                writer.writeInteger((Integer) value);
                break;
            }
            case StorageConstant.TYPE_LONG: {
                writer.writeLong((Long) value);
                break;
            }
            case StorageConstant.TYPE_FLOAT: {
                writer.writeFloat((Float) value);
                break;
            }
            case StorageConstant.TYPE_DOUBLE: {
                writer.writeDouble((Double) value);
                break;
            }
            case StorageConstant.TYPE_BOOLEAN: {
                writer.writeBoolean((Boolean) value);
                break;
            }
        }
    }


    private static byte getValueTypeCode(Object value) {
        byte code = StorageConstant.TYPE_UNKNOWN;
        if (value == null) {
            code = StorageConstant.TYPE_NULL;
        } else if (value instanceof String) {
            code = StorageConstant.TYPE_STRING;
        } else if (value instanceof Integer) {
            code = StorageConstant.TYPE_INTEGER;
        } else if (value instanceof Long) {
            code = StorageConstant.TYPE_LONG;
        } else if (value instanceof Float) {
            code = StorageConstant.TYPE_FLOAT;
        } else if (value instanceof Double) {
            code = StorageConstant.TYPE_DOUBLE;
        } else if (value instanceof Boolean) {
            code = StorageConstant.TYPE_BOOLEAN;
        }
        return code;
    }

    private static int getArrayElementType(@NonNull Object value) {

        final Class<?> type = value.getClass().getComponentType();

        byte typeCode = StorageConstant.TYPE_UNKNOWN;
        boolean obj = false;

        if ((type.isAssignableFrom(int.class)) || (obj = type.isAssignableFrom(Integer.class))) {
            typeCode = StorageConstant.TYPE_INTEGER;
        } else if ((type.isAssignableFrom(long.class)) || (obj = type.isAssignableFrom(Long.class))) {
            typeCode = StorageConstant.TYPE_LONG;
        } else if ((type.isAssignableFrom(float.class)) || (obj = type.isAssignableFrom(Float.class))) {
            typeCode = StorageConstant.TYPE_FLOAT;
        } else if ((type.isAssignableFrom(double.class)) || (obj = type.isAssignableFrom(Double.class))) {
            typeCode = StorageConstant.TYPE_DOUBLE;
        } else if ((type.isAssignableFrom(boolean.class)) || (obj = type.isAssignableFrom(Boolean.class))) {
            typeCode = StorageConstant.TYPE_BOOLEAN;
        } else if (String.class.isAssignableFrom(type)) {
            typeCode = StorageConstant.TYPE_STRING;
            obj = true;
        }
        int result = StorageConstant.MASK_TYPE_CODE & typeCode;
        if (obj) {
            result |= StorageConstant.FLAG_IS_OBJECT;
        }
        return result;
    }
}
