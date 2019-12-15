package com.fax.showdt.bean;

import com.fax.showdt.utils.GsonUtils;

public class Bean  {


    public final String toJSONString() {
        return GsonUtils.toJsonWithSerializeNulls(this);
    }

    public final String toPrettyJSONString() {
        return GsonUtils.toJsonWithSerializeNulls(this);
    }


    @Override
    public final String toString() {
        return toPrettyJSONString();
    }

}
