package com.ericsson.oss.mediation.netty.test.integration.framework.defaults;

import com.ericsson.eniq.streamterminator.component.decoder.uertt.GPBLengthDecoderComponent;
import com.ericsson.oss.mediation.netty.component.indicator.configuration.StreamIndicatorConfiguration;
import com.ericsson.oss.mediation.netty.component.multiplex.handshake.configuration.ClientFilterConfig;
import com.ericsson.oss.mediation.netty.component.record.indicator.configuration.RecordIndicatorConfiguration;
import com.ericsson.oss.mediation.netty.component.statistics.configuration.StatisticsConfig;
import com.ericsson.oss.mediation.netty.configuration.EngineConfiguration;
import com.ericsson.oss.mediation.netty.configuration.EngineExecutorGroup;
import com.ericsson.oss.mediation.netty.configuration.ExecutorConfiguration;

public final class UerttTerminatorSetup extends DefaultEngineSetup {

    @Override
    public void setDefaultDatapaths() {
        addDatapath(UerttDefaults.UERTT_IN_DATAPATH, UerttDatapath.getDefaultInDatapathConfig());
        addDatapath(UerttDefaults.UERTT_OUT_DATAPATH, UerttDatapath.getDefaultOutDatapathConfig());
    }

    @Override
    public void setDefaultDatapathComponentConfigurations() {
//        addComponentConfiguration(UerttDefaults.UERTT_IN_DATAPATH, GPBLengthDecoderComponent.class,
//                UerttDefaultComponentConfigurations.getGPBLengthDecoderComponentConfig(UerttDefaults.UERTT_OUT_DATAPATH));
        addComponentConfiguration(UerttDefaults.UERTT_IN_DATAPATH, StatisticsConfig.class, DefaultComponentConfigurations.getStatisticsConfig());
        addComponentConfiguration(UerttDefaults.UERTT_IN_DATAPATH, StreamIndicatorConfiguration.class,
                DefaultComponentConfigurations.getCelltraceStreamIndicatorConfig());
        addComponentConfiguration(UerttDefaults.UERTT_IN_DATAPATH, RecordIndicatorConfiguration.class,
                DefaultComponentConfigurations.getCelltraceRecordIndicatorConfig());
        addComponentConfiguration(UerttDefaults.UERTT_OUT_DATAPATH, ClientFilterConfig.class,DefaultComponentConfigurations.getCelltraceFilterConfig());
    }

    @Override
    public void setDefaultEngineConfiguration() {
        final EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setExecutorConfiguration(getExecutorConfig());
        this.configurationProvider.setEngineConfiguration(engineConfiguration);
    }

    private ExecutorConfiguration getExecutorConfig() {
        final EngineExecutorGroup streamBossExecutorGroup = new EngineExecutorGroup();
        streamBossExecutorGroup.setCount(1);
        streamBossExecutorGroup.setName(UerttDefaults.STREAM_BOSS_EXECUTOR);

        final EngineExecutorGroup uerttExecutorGroup = new EngineExecutorGroup();
        uerttExecutorGroup.setCount(1);
        uerttExecutorGroup.setName(UerttDefaults.UERTT_WORKER_EXECUTOR);

        final ExecutorConfiguration executorConfig = new ExecutorConfiguration();
        executorConfig.setEngineExecutorGroup(new EngineExecutorGroup[] { streamBossExecutorGroup, uerttExecutorGroup });
        return executorConfig;
    }

}
