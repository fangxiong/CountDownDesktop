package com.fax.lib.config.api.configure;


import com.fax.lib.config.api.controllable.ITypedKeyControllable;
import com.fax.lib.config.api.gettable.ITypedKeyGettable;
import com.fax.lib.config.api.puttable.ITypedKeyPuttable;

public interface ISimpleTypedKeyConfigure<KT> extends ITypedKeyPuttable<KT>, ITypedKeyGettable<KT>, ITypedKeyControllable<KT> {
}
