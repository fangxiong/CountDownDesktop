package com.fax.lib.config;

import android.app.Application;

import com.fax.lib.config.api.configure.ITypedKeyConfigure;

public class ConfigManager {
    private static FileStringKeyConfigureManager configureManager;


    public static void init(Application application) {
        configureManager = new FileStringKeyConfigureManager(application,"configure");
    }

    public static ITypedKeyConfigure<String> getMainConfig(){
        return configureManager.getConfigure("main");
    }
}
