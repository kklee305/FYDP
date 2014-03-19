package com.fydp.smarttouchassistant.config;

import android.util.Log;

public class CommonConfig {
	
    private static CommonConfig instance = null;
//    private static final boolean IS_LOGGING_ENABLED = true;
    private static final boolean IS_DEBUGGABLE = true;
    private String currentContext = "";
    
    public static synchronized CommonConfig aCommonConfig() {
        if (instance == null)
            instance = new CommonConfig();
        return instance;
    }

    public boolean isDebuggable() {
        return IS_DEBUGGABLE;
    }
    
    public String getCurrentContext() {
    	return currentContext;
    }
    
    public void setCurrentContext(String context) {
    	Log.d("","Common config updated with context "+ context);
    	this.currentContext = context;
    }
}
