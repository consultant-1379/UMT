package com.ericsson.oss.mediation.netty.test.integration.framework.defaults;

import com.ericsson.eniq.streamterminator.component.decoder.uertt.GPBLengthDecoderComponent;
import com.ericsson.oss.mediation.netty.component.decoder.EventDecoderComponent;
import com.ericsson.oss.mediation.netty.component.discard.ReleaseBufferComponent;
import com.ericsson.oss.mediation.netty.component.exception.handler.ExceptionHandlerComponent;
import com.ericsson.oss.mediation.netty.component.indicator.StreamIndicatorComponent;
import com.ericsson.oss.mediation.netty.component.multiplex.connector.MultiplexConnectorComponent;
import com.ericsson.oss.mediation.netty.component.multiplex.decoder.MultiplexDecoderComponent;
import com.ericsson.oss.mediation.netty.component.multiplex.handshake.MultiplexClientHandshakeComponent;
import com.ericsson.oss.mediation.netty.component.multiplex.handshake.MultiplexServerHandshakeComponent;
import com.ericsson.oss.mediation.netty.component.multiplex.nbichecker.NbiCheckerComponent;
import com.ericsson.oss.mediation.netty.component.record.indicator.RecordIndicatorComponent;
import com.ericsson.oss.mediation.netty.component.statistics.StatisticsComponent;
import com.ericsson.oss.mediation.netty.configuration.Components;
import com.ericsson.oss.mediation.netty.configuration.DatapathConfiguration;


public class UerttDatapath {

    public static DatapathConfiguration getDefaultInDatapathConfig() {
        return getDefaultStreamInDatapathConfig(UerttDefaults.DEFAULT_STREAM_IN_TRANSPORT, UerttDefaults.UERTT_IN_DATAPATH, UerttDefaults.UERTT_IN_ADDRESS,
                UerttDefaults.UERTT_IN_PORT, UerttDefaults.UERTT_WORKER_EXECUTOR);
    }

    public static DatapathConfiguration getDefaultOutDatapathConfig() {
        return getDefaultStreamOutDatapathConfig(UerttDefaults.DEFAULT_STREAM_OUT_TRANSPORT, UerttDefaults.UERTT_OUT_DATAPATH, UerttDefaults.UERTT_OUT_ADDRESS,
                UerttDefaults.UERTT_OUT_PORT, UerttDefaults.UERTT_WORKER_EXECUTOR);
    }

    public static DatapathConfiguration getDefaultClientDatapathConfig(final String datapathName) {
        return getDefaultStreamClientDatapathConfig(UerttDefaults.DEFAULT_STREAM_CLIENT_TRANSPORT, datapathName, UerttDefaults.UERTT_OUT_ADDRESS,
                UerttDefaults.UERTT_OUT_PORT);
    }

    private static DatapathConfiguration getDefaultStreamInDatapathConfig(final String transport, final String datapathName, final String address,
                                                                          final int port, final String workerExecutor) {
        final DatapathConfiguration streamInConfig = new DatapathConfiguration();
        streamInConfig.setTransport(transport);
        streamInConfig.setName(datapathName);
        final Components streamInComponents = new Components();
//        streamInComponents.setComponent(new String[] { EventDecoderComponent.class.getCanonicalName(), StatisticsComponent.class.getCanonicalName(),
//                StreamIndicatorComponent.class.getCanonicalName(), RecordIndicatorComponent.class.getCanonicalName(),
//                MultiplexConnectorComponent.class.getCanonicalName(), ExceptionHandlerComponent.class.getCanonicalName() });


                streamInComponents.setComponent(new String[] { GPBLengthDecoderComponent.class.getCanonicalName(), StatisticsComponent.class.getCanonicalName(),
                StreamIndicatorComponent.class.getCanonicalName(), RecordIndicatorComponent.class.getCanonicalName(),
                MultiplexConnectorComponent.class.getCanonicalName(), ExceptionHandlerComponent.class.getCanonicalName() });

//        streamInComponents.setComponent(new String[] { GPBLengthDecoderComponent.class.getCanonicalName(), StreamIndicatorComponent.class.getCanonicalName(),
//                StatisticsComponent.class.getCanonicalName(), MultiplexConnectorComponent.class.getCanonicalName(), ExceptionHandlerComponent.class.getCanonicalName() });


        streamInConfig.setComponents(streamInComponents);
        streamInConfig.setAddress(address);
        streamInConfig.setPort(port);
        streamInConfig.setBossExecutorReference(UerttDefaults.STREAM_BOSS_EXECUTOR);
        streamInConfig.setWorkerExecutorReference(workerExecutor);
        return streamInConfig;
    }

    private static DatapathConfiguration getDefaultStreamOutDatapathConfig(final String transport, final String datapathName, final String address,
                                                                           final int port, final String workerExecutor) {
        final DatapathConfiguration streamOutConfig = new DatapathConfiguration();
        streamOutConfig.setTransport(transport);
        streamOutConfig.setName(datapathName);
        final Components streamOutComponents = new Components();
//        streamOutComponents.setComponent(new String[] { MultiplexDecoderComponent.class.getCanonicalName(),
//                MultiplexServerHandshakeComponent.class.getCanonicalName(), ExceptionHandlerComponent.class.getCanonicalName() });
        streamOutComponents.setComponent(new String[] { NbiCheckerComponent.class.getCanonicalName(),
                MultiplexServerHandshakeComponent.class.getCanonicalName(),ExceptionHandlerComponent.class.getCanonicalName() });
        streamOutConfig.setComponents(streamOutComponents);
        streamOutConfig.setAddress(address);
        streamOutConfig.setPort(port);
        streamOutConfig.setBossExecutorReference(UerttDefaults.STREAM_BOSS_EXECUTOR);
        streamOutConfig.setWorkerExecutorReference(workerExecutor);
        return streamOutConfig;
    }

    private static DatapathConfiguration getDefaultStreamClientDatapathConfig(final String transport, final String datapathName,
                                                                              final String address, final int port) {
        final DatapathConfiguration streamClientConfig = new DatapathConfiguration();
        streamClientConfig.setTransport(transport);
        streamClientConfig.setName(datapathName);
        final Components streamClientComponents = new Components();
        streamClientComponents.setComponent(new String[] { MultiplexDecoderComponent.class.getCanonicalName(),
                MultiplexClientHandshakeComponent.class.getCanonicalName(), StatisticsComponent.class.getCanonicalName(),
                ReleaseBufferComponent.class.getCanonicalName() });
        streamClientConfig.setComponents(streamClientComponents);
        streamClientConfig.setAddress(address);
        streamClientConfig.setPort(port);
        streamClientConfig.setBossExecutorReference(UerttDefaults.STREAM_BOSS_EXECUTOR);
        streamClientConfig.setWorkerExecutorReference(UerttDefaults.STREAM_CLIENT_WORKER_EXECUTOR);
        return streamClientConfig;
    }

}
