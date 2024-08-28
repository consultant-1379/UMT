/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.mediation.netty.test.integration.cases;

import com.ericsson.oss.mediation.netty.channel.ThreadLocalAccumulatorProvider;
import com.ericsson.oss.mediation.netty.extension.metrics.registry.NettyMetricsRegistry;
import com.ericsson.oss.mediation.netty.test.integration.framework.defaults.*;
import com.ericsson.oss.mediation.test.tools.xterminator.data.sources.DefaultUerttDatasource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.oss.mediation.netty.Engine;
import com.ericsson.oss.mediation.netty.component.nodechecker.NodeCheckerComponent;
import com.ericsson.oss.mediation.netty.test.integration.framework.logger.LogUtil;

public class UerttEngineTest extends AbstractIntegrationTest {

    private UerttTerminatorSetup engineSetup;
    private Engine engine;

    //    private DefaultEngineSetup engineSetup;
    //    private Engine engine;

//    @Override
//    void sendEvents(Defaults.EventType eventType) throws InterruptedException {
//        super.sendEvents(eventType);
//    }
//
//    @Before
//    public void startEngine() throws Exception {
//        this.engineSetup = new UerttTerminatorSetup();
//        this.engineSetup.setDefaultDatapathComponentConfigurations();
//        this.engineSetup.setDefaultDatapaths();
//        this.engineSetup.setDefaultEngineConfiguration();
//        this.engineSetup.addEnabledExtension(NettyMetricsRegistry.class);
//        this.engineSetup.addEnabledExtension(ThreadLocalAccumulatorProvider.class);
//        this.engineSetup.addComponentToDatapathStart(UerttDefaults.UERTT_IN_DATAPATH, NodeCheckerComponent.class.getCanonicalName());
//    }
//
//    @After
//    public void cleanUp() {
//        if (this.engine != null) {
//            this.engine.stop();
//        }
//    }
//
//    @Test
//    public void EngineStart_startOperation_NoErrors() throws Exception {
//        this.engine = this.engineSetup.startEngine();
//        sendEvents(UerttDefaults.UERTT_IN_ADDRESS, UerttDefaults.UERTT_IN_PORT, new DefaultUerttDatasource());
//        Assert.assertEquals(LogUtil.INSTANCE.getErrorMessages().size(), 0);
//        Assert.assertEquals(LogUtil.INSTANCE.getWarnMessages().size(), 0);
//    }

}
