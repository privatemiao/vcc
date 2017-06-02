package com.easyway.vcc.net;

import com.ewivt.vhs.dto.request.BaseRequest;
import com.ewivt.vhs.dto.request.RegisterRequest;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by vipzj on 2017-05-25.
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Application application;
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setRtmpAddress("rtmp://10.100.103.13/live");
        request.setClientId("0001");
        request.setClientType(1);
        request.setClientName("Client0001");
        ctx.writeAndFlush(request);

        channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        application.getRxBusSingleton().send(msg);
    }

    public void sendRequest(BaseRequest request) {

        channel.writeAndFlush(request);
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
