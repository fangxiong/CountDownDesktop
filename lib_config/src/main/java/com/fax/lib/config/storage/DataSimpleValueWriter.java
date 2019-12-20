package com.fax.lib.config.storage;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.DataOutput;
import java.io.IOException;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
class DataSimpleValueWriter implements ISimpleValueWriter {

    private final DataOutput out;

    DataSimpleValueWriter(DataOutput out) {
        this.out = out;
    }

    @Override
    public void writeLength(int length) throws IOException {
        out.writeInt(length);
    }

    @Override
    public void writeTypeCode(byte typeCode) throws IOException {
        out.writeByte(typeCode);
    }

    @Override
    public void writeString(String value) throws IOException {
        if (state(value)) {
            out.writeUTF(value);
        }
    }

    @Override
    public void writeInteger(Integer value) throws IOException {
        if (state(value)) {
            out.writeInt(value);
        }
    }

    @Override
    public void writeLong(Long value) throws IOException {
        if (state(value)) {
            out.writeLong(value);
        }
    }

    @Override
    public void writeFloat(Float value) throws IOException {
        if (state(value)) {
            out.writeFloat(value);
        }
    }

    @Override
    public void writeDouble(Double value) throws IOException {
        if (state(value)) {
            out.writeDouble(value);
        }
    }

    @Override
    public void writeBoolean(Boolean value) throws IOException {
        if (state(value)) {
            out.writeBoolean(value);
        }
    }

    private boolean state(Object value) throws IOException {
        final boolean notNull = value != null;
        out.writeBoolean(notNull);
        return notNull;
    }

}
