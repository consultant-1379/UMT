package com.ericsson.eniq.streamterminator;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.eniq.streamterminator.configuration.StandaloneConfiguration;
import com.ericsson.oss.mediation.netty.Engine;
import com.ericsson.oss.mediation.netty.EngineFactory;
import com.ericsson.oss.mediation.netty.extension.provider.jaxb.JAXBConfigurationProvider;
import com.ericsson.oss.mediation.netty.extension.provider.jaxb.JAXBConfigurationReader;
import com.ericsson.oss.mediation.netty.extension.provider.jaxb.ResourceReader;

//
//
// for testing use test/java/com.ericsson.eniq.streamterminator.TestMain 
//
//

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String STANDALONE_CONFIGURATION = "standalone_configuration.xml";
    private static final String ROOT_DIR = "definitions";

    private static Engine engine;

    private Main() {
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {

        String streamingConfigDir = System.getProperty(JAXBConfigurationProvider.PROPERTY_JAXB_CONFIGURATION_ROOT);
        if (streamingConfigDir == null) {
            System.setProperty(JAXBConfigurationProvider.PROPERTY_JAXB_CONFIGURATION_ROOT, ROOT_DIR);
            streamingConfigDir = ROOT_DIR;
        }

        engine = EngineFactory.getInstance();
        engine.start();

        final StandaloneConfiguration configuratuon = getStandaloneConfiguration(streamingConfigDir);
        if (configuratuon.getStartDatapath() != null && configuratuon.getStartDatapath().getDatapath() != null) {
            for (final String datapathName : configuratuon.getStartDatapath().getDatapath()) {
                engine.startDataPath(datapathName);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Hook());
        LOGGER.debug("Netty Engine Standalone started successfully.");
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    private static class Hook extends Thread {
        @Override
        public void run() {
            engine.stop();
            LOGGER.info("Netty Stream Terminator is DOWN!");
        }
    }

    public static StandaloneConfiguration getStandaloneConfiguration(final String streamingConfigDir) {
        final File file = new ResourceReader().getFileResource(streamingConfigDir + "/" + STANDALONE_CONFIGURATION);
        return new JAXBConfigurationReader().getConfiguration(file, StandaloneConfiguration.class);
    }

}
