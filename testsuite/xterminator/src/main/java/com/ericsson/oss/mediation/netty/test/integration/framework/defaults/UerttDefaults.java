package com.ericsson.oss.mediation.netty.test.integration.framework.defaults;


public class UerttDefaults {

    public static final String UERTT_IN_DATAPATH = "stream_in_uertt";
    public static final int UERTT_IN_PORT = 10101;
    public static final String UERTT_IN_ADDRESS = "0.0.0.0";
    public static final String UERTT_OUT_DATAPATH = "stream_out_uertt";
    public static final String UERTT_CLIENT_DATAPATH = "stream_client_uertt";
    static final int UERTT_OUT_PORT = 10111;
    static final String UERTT_OUT_ADDRESS = "0.0.0.0";



    public static final String DEFAULT_STREAM_IN_TRANSPORT = "NIO_SOCKET_SERVER";
    public static final String DEFAULT_STREAM_OUT_TRANSPORT = "NIO_SOCKET_SERVER";
    public static final String DEFAULT_STREAM_CLIENT_TRANSPORT = "NIO_SOCKET_CLIENT";

    public static final String UERTT_WORKER_EXECUTOR = "uertt_worker";



    public static final String STREAM_BOSS_EXECUTOR = "stream_boss";
    public static final String STREAM_CLIENT_WORKER_EXECUTOR = "stream_client_worker";
}
