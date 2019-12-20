package com.fax.lib.config.api.gettable;


import androidx.annotation.NonNull;

public interface IAllKeysGettable<KAT> {

    @NonNull
    KAT getAllKeys();
}
