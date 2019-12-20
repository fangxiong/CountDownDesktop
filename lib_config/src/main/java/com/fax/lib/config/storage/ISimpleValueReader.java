package com.fax.lib.config.storage;

import java.io.IOException;

interface ISimpleValueReader {

    int readLength() throws IOException;

    byte readTypeCode() throws IOException;

    String readString() throws IOException;

    Integer readInteger() throws IOException;

    Long readLong() throws IOException;

    Float readFloat() throws IOException;

    Double readDouble() throws IOException;

    Boolean readBoolean() throws IOException;

}
