package com.dextrys.listeners;

import com.dextrys.utils.Rand;
//import com.dextrys.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;


public class LifecycleLogger extends TestListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LifecycleLogger.class);

    @Override
    public void onTestStart(ITestResult tr) {
        logger.info(">>>>>>>>>>>>> " + tr.getName() + " >>>>>>>>>>>>>");
    }

    @Override
    public void beforeConfiguration(ITestResult tr) {
        //Utils.solveCaptcha();
        Rand.resetLastDateRange();
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        logger.info("<<<<<<<<<<<<< SUCCESS <<<<<<<<<<<<<");
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        logger.info("<<<<<<<<<<<<< FAILURE <<<<<<<<<<<<<");
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        logger.info("<<<<<<<<<<<<< SKIPPED <<<<<<<<<<<<<");
    }

}