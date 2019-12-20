package com.fax.lib.config.storage;

public interface StorageConstant {
    String TAG = "test_config";

    byte TYPE_NULL = 0x00;
    byte TYPE_STRING = 0x01;
    byte TYPE_INTEGER = 0x02;
    byte TYPE_LONG = 0x03;
    byte TYPE_FLOAT = 0x04;
    byte TYPE_DOUBLE = 0x05;
    byte TYPE_BOOLEAN = 0x06;
    byte TYPE_ARRAY = 0x09;
    byte TYPE_SPARSE_ARRAY = 0x0a;
    byte TYPE_LONG_SPARSE_ARRAY = 0x0b;
    byte TYPE_ARRAY_MAP = 0x0c;
    byte TYPE_UNKNOWN = (byte) 0xff;

    int FLAG_IS_OBJECT = 1 << 8;
    int MASK_TYPE_CODE = 0x000000ff;

    int NULL_LENGTH = -1;
    int EOF = -1;


}
