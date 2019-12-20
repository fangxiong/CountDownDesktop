package com.fax.lib.config.api.configure;


import com.fax.lib.config.api.controllable.ITypedKeyControllable;
import com.fax.lib.config.api.gettable.IOptionTypedKeyGettable;
import com.fax.lib.config.api.puttable.ITypedKeyPuttable;

/**
 * Created with Android Studio
 * <p/>
 * Project: lockscreen
 * Author: zhangshaolin
 * Date:   14/11/15
 * Time:   下午8:38
 * Email:  zsl@kuba.bz
 */
public interface ITypedKeyConfigure<KT> extends ITypedKeyPuttable<KT>, IOptionTypedKeyGettable<KT>, ITypedKeyControllable<KT> {
}
