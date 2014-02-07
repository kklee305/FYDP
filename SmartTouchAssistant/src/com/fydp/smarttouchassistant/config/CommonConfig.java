package com.fydp.smarttouchassistant.config;

public class CommonConfig {
	
    private static CommonConfig instance = null;
//    private static final boolean IS_LOGGING_ENABLED = true;
    private static final boolean IS_DEBUGGABLE = true;
    
    public static synchronized CommonConfig aCommonConfig() {
        if (instance == null)
            instance = new CommonConfig();
        return instance;
    }

    public boolean isDebuggable() {
        return IS_DEBUGGABLE;
    }
    
}
