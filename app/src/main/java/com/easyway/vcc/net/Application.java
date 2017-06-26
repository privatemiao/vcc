package com.easyway.vcc.net;

import android.content.Context;

import com.easyway.vcc.serial.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Created by vipzj on 2017-05-24.
 */

public class Application extends android.app.Application {

    public static final ClientHandler demoClientHandler = new ClientHandler();
    public static final String CONTROLLER_IP = "192.168.47.30";
    public static final int CONTROLLER_PORT = 9333;
    public static ChannelFuture f;

    private RxBus _rxBus = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("qupai-media-thirdparty");
        System.loadLibrary("qupai-media-jni");
        startNettyClient();
    }

    private void startNettyClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                demoClientHandler.setApplication(Application.this);

                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline p = ch.pipeline();
                                    p.addLast(
                                            new ObjectEncoder(),
                                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                            demoClientHandler);
                                }
                            });

                    // Make the connection attempt.
                    f = b.connect(CONTROLLER_IP, CONTROLLER_PORT).sync();

                    // Wait until the connection is closed.
                    f.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }

            }
        }).start();
    }

    public RxBus getRxBusSingleton() {
        if (_rxBus == null) {
            _rxBus = new RxBus();
        }

        return _rxBus;
    }

    // For Serial Port
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
                        /* Open the serial port */
            mSerialPort = new SerialPort(new File("/dev/ttyS4"), 9600, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
