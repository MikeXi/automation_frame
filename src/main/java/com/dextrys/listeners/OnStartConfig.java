package com.dextrys.listeners;

import com.dextrys.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;


public class OnStartConfig extends TestListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(OnStartConfig.class);

    public void onStart(ITestContext testContext) {
        Config.get().initialize();
    }
}