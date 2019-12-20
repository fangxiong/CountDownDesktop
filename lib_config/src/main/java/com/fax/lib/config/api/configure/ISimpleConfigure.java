package com.fax.lib.config.api.configure;

public interface ISimpleConfigure extends ISimpleIntKeyConfigure {

    ISimpleIntKeyConfigure getIntKeySubset(int key);

    ISimpleLongKeyConfigure getLongKeySubset(int key);

    ISimpleTypedKeyConfigure<String> getStringKeySubset(int key);


}
