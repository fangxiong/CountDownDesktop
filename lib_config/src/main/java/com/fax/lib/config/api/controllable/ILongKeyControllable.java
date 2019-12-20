package com.fax.lib.config.api.controllable;

public interface ILongKeyControllable {

    void remove(long key);

    boolean contains(long key);

    void clear();

    void delete();

}
