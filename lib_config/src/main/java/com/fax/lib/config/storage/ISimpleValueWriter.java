package com.fax.lib.config.storage;

import java.io.IOException;

interface ISimpleValueWriter {

    void writeLength(int length) throws IOException;

    void writeTypeCode(byte typeCode) throws IOException;

    void writeString(String value) throws IOException;

    void writeInteger(Integer value) throws IOException;

    void writeLong(Long value) throws IOException;

    void writeFloat(Float value) throws IOException;

    void writeDouble(Double value) throws IOException;

    void writeBoolean(Boolean value) throws IOException;

}
