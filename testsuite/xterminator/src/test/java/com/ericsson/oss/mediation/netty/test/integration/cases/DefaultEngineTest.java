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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.oss.mediation.netty.Engine;
import com.ericsson.oss.mediation.netty.component.nodechecker.NodeCheckerComponent;
import com.ericsson.oss.mediation.netty.test.integration.framework.defaults.DefaultEngineSetup;
import com.ericsson.oss.mediation.netty.test.integration.framework.defaults.Defaults;
import com.ericsson.oss.mediation.netty.test.integration.framework.defaults.Defaults.EventType;
import com.ericsson.oss.mediation.netty.test.integration.framework.defaults.IntegrationTestFactory;
import com.ericsson.oss.mediation.netty.test.integration.framework.logger.LogUtil;

/**
 * @author ebujkri
 * 
 */
public class DefaultEngineTest extends AbstractIntegrationTest {

    private DefaultEngineSetup engineSetup;
    private Engine engine;

    @Before
    public void startEngine() throws Exception {
        final IntegrationTestFactory setup = new IntegrationTestFactory(0);
        this.engineSetup = setup.getTerminatorSetup();
        this.engineSetup.addComponentToDatapathStart(Defaults.CELLTRACE_IN_DATAPATH, NodeCheckerComponent.class.getCanonicalName());
    }

    @After
    public void cleanUp() {
        if (this.engine != null) {
            this.engine.stop();
        }
    }

    @Test
    public void EngineStart_startOperation_NoErrors() throws Exception {
        this.engine = this.engineSetup.startEngine();
        sendEvents(EventType.CELLTRACE);
        Assert.assertEquals(LogUtil.INSTANCE.getErrorMessages().size(), 0);
        Assert.assertEquals(LogUtil.INSTANCE.getWarnMessages().size(), 0);
    }
}
