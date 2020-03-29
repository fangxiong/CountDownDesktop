package com.fax.showdt.manager;

import android.text.TextUtils;
import android.util.Log;

import com.fax.lib.config.ConfigManager;
import com.fax.showdt.ConstantString;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.utils.GsonUtils;

public class CommonConfigManager {

    private volatile static CommonConfigManager mInstance = null;

    private CommonConfigManager(){}
    public static CommonConfigManager getInstance(){
        if(mInstance== null){
            synchronized (CommonConfigManager.class){
                if(mInstance == null){
                    mInstance = new CommonConfigManager();
                }
            }
        }
        return mInstance;
    }


    public  void setWidgetConfig(CustomWidgetConfig config){
        ConfigManager.getMainConfig().putString(ConstantString.key_widget_config,config.toJSONString());
        Log.i("test_config_put:",GsonUtils.toJsonWithSerializeNulls(config));

    }

    public  CustomWidgetConfig getWidgetConfig(){
       String json =  ConfigManager.getMainConfig().getString(ConstantString.key_widget_config,"");
        Log.i("test_config_get:",json);
       if(!TextUtils.isEmpty(json)){
           return GsonUtils.parseJsonWithGson(json,CustomWidgetConfig.class);
       }
       return  null;
    }

    /**
     * 表示同意了隐私协议
     */
    public void setHadAllowedPrivacy(){
        ConfigManager.getMainConfig().putBool(ConstantString.allow_privacy,true);
    }

    /**
     * 获取是否同意了隐私协议
     * @return
     */
    public boolean isHadAllowedPrivacy(){
        return ConfigManager.getMainConfig().getBool(ConstantString.allow_privacy,false);
    }
}
