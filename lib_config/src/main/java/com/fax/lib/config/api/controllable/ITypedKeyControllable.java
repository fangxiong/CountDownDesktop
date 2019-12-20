package com.fax.lib.config.api.controllable;

public interface ITypedKeyControllable<KT> {

    void remove(KT key);

    boolean contains(KT key);

    void clear();

    void delete();

}
