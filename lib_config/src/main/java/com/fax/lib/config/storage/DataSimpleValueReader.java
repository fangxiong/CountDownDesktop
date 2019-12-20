package com.fax.lib.config.storage;

import java.io.DataInput;
import java.io.IOException;

class DataSimpleValueReader implements ISimpleValueReader {
    private final DataInput in;

    DataSimpleValueReader(DataInput in) {
        this.in = in;
    }

    @Override
    public int readLength() throws IOException {
        return in.readInt();
    }

    @Override
    public byte readTypeCode() throws IOException {
        return in.readByte();
    }

    @Override
    public String readString() throws IOException {
        return nextState() ? in.readUTF() : null;
    }

    @Override
    public Integer readInteger() throws IOException {
        return nextState() ? in.readInt() : null;
    }

    @Override
    public Long readLong() throws IOException {
        return nextState() ? in.readLong() : null;
    }

    @Override
    public Float readFloat() throws IOException {
        return nextState() ? in.readFloat() : null;
    }

    @Override
    public Double readDouble() throws IOException {
        return nextState() ? in.readDouble() : null;
    }

    @Override
    public Boolean readBoolean() throws IOException {
        return nextState() ? in.readBoolean() : null;
    }

    private boolean nextState() throws IOException {
        return in.readBoolean();
    }
}
